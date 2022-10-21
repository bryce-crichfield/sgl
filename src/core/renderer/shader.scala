package core
package renderer
import scala.io.Source
import org.lwjgl.opengl.GL20.*;


case class ShaderProgram private (
    private val program: Int,
    private val vertex_shader: Int,
    private val fragment_shader: Int) {

      def dispose(): Unit = {
        glDetachShader(program, vertex_shader)
        glDetachShader(program, fragment_shader)
        glDeleteProgram(program)
      }

      def use(): Unit = {
        glUseProgram(program)
      }
}

object ShaderProgram {
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

  def load(vpath: String, fpath: String): Unsafe[ShaderProgram] = {
    for {
      vsrc <- Unsafe { 
        Source.fromFile(vpath).getLines.map(_.appended('\n')).mkString }
      fsrc <- Unsafe { 
        Source.fromFile(fpath).getLines.map(_.appended('\n')).mkString }
      pid <- Unsafe { glCreateProgram() }
      _ <- Util.throwOn(pid == 0)("glCreateProgram()")
      vshader <- compile(vsrc, GL_VERTEX_SHADER, vpath)
      fshader <- compile(fsrc, GL_FRAGMENT_SHADER, fpath)
      _ <- link(pid, vshader, fshader)
    } yield ShaderProgram(pid, vshader, fshader) 
  }
}

class ShaderLibrary {
    private val dictionary = new MutMap[String, ShaderProgram]()

    def load(load_event: RenderEvent.LoadShader): Option[ShaderProgram] = {
        ShaderProgram.load(load_event.vpath, load_event.fpath)
          .onFail(error => println(f"Renderer Failed Model Load: ${load_event.id}\n$error"))
          .toOption.map { program =>
            dictionary.put(load_event.id, program)
            program
          }
    }  

    def get(id: String): Option[ShaderProgram] = {
      dictionary.get(id)
    }

    def close(): Unit = {
        dictionary.values.foreach(_.dispose())
        dictionary.clear()
    }
}