package core
import org.lwjgl.opengl.GL20.*;
import cats.effect.Resource

case class Shader private (
    program: Int,
    vertex_shader: Int,
    fragment_shader: Int
)
import cats.effect.IO
object Shader {
  private def compile(src: String, shader_type: Int, path: String): Unsafe[Int] = {
    for {
      id <- Unsafe { glCreateShader(shader_type) }
      _ <- Util.throwOn(id == 0)(f"glCreateShader($shader_type)")
      _ <- Unsafe {
            glShaderSource(id, src)
            glCompileShader(id) 
          }
      _ <- Util.throwOn(glGetShaderi(id, GL_COMPILE_STATUS) == 0)
            (f"glCompileShader()\n$path\n${glGetShaderInfoLog(id, 1024)}")
    } yield id
  }

  private def link(pid: Int, vid: Int, fid: Int): Unsafe[Unit] = {
    for {
      _ <- Unsafe {
        glAttachShader(pid, vid)
        glAttachShader(pid, fid)
        glLinkProgram(pid)
      }
      _ <- Util.throwOn(glGetProgrami(pid, GL_LINK_STATUS) == 0)
            (f"glLinkProgram()\n${glGetProgramInfoLog(pid, 1024)}")
      _ <- Unsafe { glValidateProgram(pid) }
      _ <- Util.throwOn(glGetProgrami(pid, GL_VALIDATE_STATUS) == 0)
            (f"glValidateProgram\n${glGetProgramInfoLog(pid, 1024)}")
    } yield ()
  }

  def load(vpath: String, fpath: String): Resource[Unsafe, Shader] = {
    Resource.make {
      for {
        vsrc <- Util.read(vpath)
        fsrc <- Util.read(fpath)
        pid <- Unsafe { glCreateProgram() }
        _ <- Util.throwOn(pid == 0)("glCreateProgram()")
        vshader <- compile(vsrc, GL_VERTEX_SHADER, vpath)
        fshader <- compile(fsrc, GL_FRAGMENT_SHADER, fpath)
        _ <- link(pid, vshader, fshader)
      } yield Shader(pid, vshader, fshader)
    } { shader =>
      Unsafe {
        glDetachShader(shader.program, shader.vertex_shader)
        glDetachShader(shader.program, shader.fragment_shader)
        glDeleteProgram(shader.program)
      }  
    }
  }
}
