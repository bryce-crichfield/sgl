package core.application

import core.kernel.*
import core.event.*

import org.joml.Vector3f
class Application() extends core.kernel.process.Process {
  val chrono = core.kernel.Chronometer(30.0)
  val camera = new GlobalCamera()
  val tracker = new CameraJoystick()
  val motion = new PlayerMotion(.25)
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

  val acceleration = 0.1f
  in { case KeyEvent(code, _, _) =>
    code match
      case KeyCode.W  => 
        motion.acceleration.add(0, 0, acceleration)
      case KeyCode.S  => 
        motion.acceleration.add(0, 0, -acceleration)
      case KeyCode.A  => 
        motion.acceleration.add(-acceleration, 0, 0)
      case KeyCode.D  => 
        motion.acceleration.add(acceleration, 0, 0)
      case KeyCode.Q  => 
        motion.acceleration.add(0, 0, acceleration)
      case KeyCode.E  => 
        motion.acceleration.add(0, 0, acceleration)
      case KeyCode.ESCAPE => out(SystemEvent.SigTerm)
      case _          => ()
  }


  in {
    case MouseEvent(code, action, x, y) =>
        println(code)
    case MousePosition(x, y) =>
        tracker.panTo(x)

        
  }
  override def update(): Unit =  {
    drain_in()

    val rotation_direction = tracker.update(1)
    camera.rx(-1*rotation_direction*.05f)

    motion.update(0.01)
    val velocity = motion.velocity
    camera.tx(velocity.x())
    camera.ty(velocity.y())
    camera.tz(velocity.z())
    root_node.render(camera).foreach(out)
  }
}
