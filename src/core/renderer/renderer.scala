package core
package renderer
import core.event.*
import org.joml.{Vector3f, Matrix3f}
import org.lwjgl.opengl.GL11.*

class Renderer() extends core.kernel.process.Process {
  override val id: String = "Renderer"
  val chrono = core.kernel.Chronometer(60.0d)
  var viewport: Viewport = _
  val shader_library = new ShaderLibrary()
  val model_library = new ModelLibrary()
  val camera = new GlobalCamera()


  var texture: Texture = _

  in {
    case RenderEvent.LoadModel(id, path) =>
      println(f"LoadModel $id at $path")
      model_library.load(id, path)
  }

  in {
    case RenderEvent.LoadShader(id, vpath, fpath) =>
      println(f"LoadModel $id at $vpath and $fpath")
      shader_library.load(id, vpath, fpath)
  }
  in {
    case RenderEvent.DrawModel(model_id, shader_id, transform, absolute) =>
      for {
        model <- model_library.get(model_id)
        shader <- shader_library.get(shader_id)
        mvp <- if !absolute 
              then Some(camera.mvp_transform(transform))
              else Some(transform)
      } yield model.draw(shader, mvp, null)
  }

  in {
    case RenderEvent.CameraTranslateX(scale) =>
      val direction_scalar_x = new Vector3f()
      camera.right.mul(scale, direction_scalar_x)
      camera.position.add(direction_scalar_x)

    case RenderEvent.CameraTranslateY(scale) =>
      val direction_scalar_y = new Vector3f()
      camera.up.mul(scale, direction_scalar_y)
      camera.position.add(direction_scalar_y)

    case RenderEvent.CameraTranslateZ(scale) =>
      val direction_scalar_z = new Vector3f()
      camera.forward.mul(scale, direction_scalar_z)
      camera.position.add(direction_scalar_z)


    case RenderEvent.CameraPan(angle) =>
      val rotation = new Matrix3f().rotate(angle*.1f, new Vector3f(0, 1.0, 0))
      camera.right.mul(rotation)
      camera.up.mul(rotation)
      camera.forward.mul(rotation)
    case RenderEvent.CameraTilt(angle) =>
      val rotation = new Matrix3f().rotate(angle*.1f, new Vector3f(1.0, 1.0, 0))
      camera.right.mul(rotation)
      camera.up.mul(rotation)
      camera.forward.mul(rotation)
    case RenderEvent.CameraRoll(angle) =>
      val rotation = new Matrix3f().rotate(angle*.1f, new Vector3f(0, 0.0, 1.0))
      camera.right.mul(rotation)
      camera.up.mul(rotation)
      camera.forward.mul(rotation)

    case RenderEvent.CameraReset() => 
      camera.forward = new Vector3f(0, 0, -1)
      camera.up = new Vector3f(0,1, 0) 
      camera.right = new Vector3f(1,0,0)
      camera.position = new Vector3f(0, 0, 0)
  }

  override def launch(): Unit = {
    viewport = Viewport.create().getOrElse(null)
    glEnable(GL_DEPTH_TEST)
    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    glClearColor(0.5f, 0.5, 0.5, 1.0f)


    texture = Texture.load("resource/texture/metal.png").get

  }

  override def shutdown(): Unit = {
    viewport.destroy()
    shader_library.close()
    model_library.close()
  }
  override def update(): Unit = {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    drain_in()
    viewport.update()
    if viewport.close()
    then out(SystemEvent.SigTerm)
    else viewport.flushEvent().foreach(out)
  }
}


