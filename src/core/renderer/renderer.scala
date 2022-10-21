package core
package renderer

import core.kernel.{SystemEvent, Event}
import org.joml.Vector3f
import org.lwjgl.opengl.GL11.*

class Renderer() extends core.kernel.process.Process {
  override val id: String = "Renderer"
  val chrono = core.kernel.Chronometer(60.0d)
  var viewport: Viewport = _
  val shader_library = new ShaderLibrary()
  val model_library = new ModelLibrary()

//   val model_shader =
//     new ShaderLoad("id", "shaders/v1.glsl", "shaders/f1.glsl")
  var model: Model = _

  override def launch(): Unit = {
    viewport = Viewport.create().getOrElse(null)
    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    glClearColor(0.5f, 0.5, 0.5, 1.0f)

    model_library.load(RenderEvent.LoadModel("gator", "rsc/model/gator.obj"))
    shader_library.load(RenderEvent.LoadShader("id", "shaders/v1.glsl", "shaders/f1.glsl"))
  }

  override def shutdown(): Unit = {
    viewport.destroy()
    shader_library.close()
    model_library.close()
  }
  override def cycle(events: List[Event]): List[Event] = {
    glClear(GL_COLOR_BUFFER_BIT)

    events.foreach {
      case RenderEvent.DrawModel(model_id, shader_id, transform) =>
        for {
          model <- model_library.get(model_id)
          shader <- shader_library.get(shader_id)
        } yield model.draw(shader, transform, null)
      // case registration: RenderEvent.ShaderRegistration =>
      //     shader_library.register(registration) match
      //         case None => println(f"Failed to Register $registration")
      //         case Some(value) => println(f"Registered $value")
      case _ => ()
    }
    // model.draw(shader_library.get(model_shader),  null)
    viewport.update()
    if viewport.close()
    then List(SystemEvent.SigTerm)
    else viewport.flushEvent()
  }
}

// Note: This idea still seems useful as a way of organizing
// Sets the OpenGL context in specific ways
// For example, the GUI render and World render
// use different shaders, coordinate systems, and
// configuration options.
// trait Renderer {
//     val shader_programs: ShaderDatabase
//     val models: Set[Model]: ModelDatabase
//     val draw_procedure: ShaderDatabase
//     def begin(): Unit = {
//      setupOpenGLContext
//      respondToLoadCalls()
//     }
//     def render(): Unit = {
//      respondToDrawCalls()
// }
//     def end(): Unit
// }
