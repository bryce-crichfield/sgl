package core.application

import core.kernel.*
import core.event.*

import org.joml.Vector3f
class Application() extends core.kernel.process.Process {

  override val id: String = "Application"

  val chrono = core.kernel.Chronometer(30.0)
  val camera = new GlobalCamera()
  val tracker = new CameraJoystick()
  val motion = new PlayerMotion(.25)
  val chunk = new Chunk()

  out(RenderEvent(_.loadModel("cube", "resource/obj/cube.obj")))
  out(
    RenderEvent(
      _.loadShader("id", "resource/shaders/v1.glsl", "resource/shaders/f1.glsl")
    )
  )

  val acceleration = 0.1f
  in { case KeyEvent(code, _, _) =>
    code match
      case KeyCode.W  => 
        camera.tz(acceleration)
      case KeyCode.S  => 
        camera.tz(-acceleration)
      case KeyCode.A  => 
        camera.tx(-acceleration)
      case KeyCode.D  => 
        camera.tx(acceleration)
      case KeyCode.Q  => 
        camera.tx(-acceleration)
      case KeyCode.E  =>
        camera.tx(acceleration) 
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

    chunk.render(camera).foreach(out)
    // root_node.render(camera).foreach(out)
  }
}



class Chunk {
  val world_transform = new ArtifactTransformation()
  val data = Array.fill(16*16*16)

  def render(camera: Camera): List[RenderEvent] = {
    val out = core.MutBuf[RenderEvent]()
    for (x <- 0 until 16) {
      for (y <- 0 until 16) {
        for (z <- 0 until 16) {
          val transform = new ArtifactTransformation()
          transform.translate = new Vector3f(x, y, z)
          transform.scale = new Vector3f(.5f)
          val mvp = core.Util.toArray(camera.mvp(transform()))
          out.addOne(RenderEvent(_.drawModel("cube", "id", mvp)))
        }}
    }

    out.toList
  }
}
