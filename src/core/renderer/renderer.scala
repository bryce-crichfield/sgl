package core
package renderer
import core.event.*
import org.joml.Vector3f
import org.lwjgl.opengl.GL11.*

class Renderer() extends core.kernel.process.Process {
  override val id: String = "Renderer"
  val chrono = core.kernel.Chronometer(60.0d)
  var viewport: Viewport = _
  val shader_library = new ShaderLibrary()
  val model_library = new ModelLibrary()
  // val camera = new GlobalCamera()

  override def launch(): Unit = {
    viewport = Viewport.create().getOrElse(null)
    glEnable(GL_DEPTH_TEST)
    // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    glClearColor(0.5f, 0.5, 0.5, 1.0f)

    model_library.load(RenderEvent.LoadModel("boat", "resource/boat.obj"))
    shader_library.load(RenderEvent.LoadShader("id", "shaders/v1.glsl", "shaders/f1.glsl"))
  }

  override def shutdown(): Unit = {
    viewport.destroy()
    shader_library.close()
    model_library.close()
  }
  override def update(): Unit = {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

    // events.foreach {
    //   case event @ RenderEvent.DrawModel(model_id, shader_id, transform) =>
    //     for {
    //       model <- model_library.get(model_id)
    //       shader <- shader_library.get(shader_id)
    //       mvp <- Some(camera.mvp_transform(transform))
    //     } yield model.draw(shader, mvp, null)
    //   case event @ RenderEvent.CameraTranslate(x, y, z) =>
    //     // println("MOVE CAMERA")
    //     val vector = new Vector3f(x, y, z).mul(0.025f)
    //     camera.position.add(vector)
    //   case _ => ()
    // }
    viewport.update()
    
    if viewport.close()
    then out(SystemEvent.SigTerm)
    else viewport.flushEvent()
  }
}
