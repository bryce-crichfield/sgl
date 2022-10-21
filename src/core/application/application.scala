package core.application

import core.kernel.*
import core.interface.*

class Application() extends core.kernel.process.Process {
    val chrono = core.kernel.Chronometer(120.0)
    override val id: String = "Application"
    val gator = new Artifact("boat", "id")
    override def cycle(events: List[Event]): List[Event] = {
        val response: List[Event] = events.flatMap { 
            case i: InputEvent =>  i match
                case KeyEvent(KeyCode.ESCAPE, _, _) =>
                    List(SystemEvent.SigTerm)
                case KeyEvent(KeyCode.W, InputAction.Press, _) |
                    KeyEvent(KeyCode.W, InputAction.Repeat, _) =>
                    List(RenderEvent.CameraTranslate(0, 0, -1))
                case KeyEvent(KeyCode.A, InputAction.Press, _) |
                    KeyEvent(KeyCode.A, InputAction.Repeat, _) =>
                    List(RenderEvent.CameraTranslate(-1, 0, 0))
                case KeyEvent(KeyCode.S, InputAction.Press, _) |
                    KeyEvent(KeyCode.S, InputAction.Repeat, _) =>
                    List(RenderEvent.CameraTranslate(0, 0, 1))
                case KeyEvent(KeyCode.D, InputAction.Press, _) |
                    KeyEvent(KeyCode.D, InputAction.Repeat, _) =>
                    List(RenderEvent.CameraTranslate(1, 0, 0))
                case _ =>
                    // println(f"Application got $i")
                    List()
            case _ => Nil
        }
        chrono.tick() match 
            case None => response
            case Some(delta) => 
                val adj = delta / 1e6
                // println(adj)
                val time = chrono.time()/1e9f
                gator.update(time, delta)::response
    }
}


