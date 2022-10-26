package core.application

import core.kernel.*
import core.event.*

import org.joml.Vector3f
class Application() extends core.kernel.process.Process {
  val chrono = core.kernel.Chronometer(30.0)
  val camera = new GlobalCamera()
  override val id: String = "Application"
  val root_node = new SceneNode()
  root_node.local_transform.translate = new Vector3f(0, 0, -2)
  root_node.local_transform.rotation_axis = new Vector3f(1.0, 0, 0)
  root_node.local_transform.rotation_angle = Math.PI.toFloat / 2

  val child = new SceneNode()
  child.local_transform.scale = new Vector3f(0.5, 0.5, 0.5)
  root_node.children.addOne(child)
  out(RenderEvent(_.loadModel("square", "resource/obj/square.obj")))
  out(
    RenderEvent(
      _.loadShader("id", "resource/shaders/v1.glsl", "resource/shaders/f1.glsl")
    )
  )

  val move_speed = 0.1f
  in { case KeyEvent(code, _, _) =>
    code match
      case KeyCode.W  => camera.tz(move_speed)
      case KeyCode.S  => camera.tz(-move_speed)
      case KeyCode.A  => camera.tx(-move_speed)
      case KeyCode.D  => camera.tx(move_speed)
      case KeyCode.Q  => camera.ty(-move_speed)
      case KeyCode.E  => camera.ty(move_speed)
      case KeyCode.H  => ()
      case KeyCode.K  => ()
      case KeyCode.U  => ()
      case KeyCode.J  => ()
      case KeyCode.Y  => ()
      case KeyCode.I  => ()
      case KeyCode.F1 => ()
      case _          => ()
  }
  override def update(): Unit = {
    drain_in()

    root_node.render(camera).foreach(out)
  }
}
