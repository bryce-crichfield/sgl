package core.application

import core.kernel.*
import core.renderer.{KeyEvent, KeyCode, InputAction}

class Application() extends core.kernel.process.Process {
    val chrono = core.kernel.Chronometer(120.0)
    override val id: String = "Application"
    val Artifact = new Artifact("teapot")
    override def cycle(events: List[Event]): List[Event] = {
        val response = events.flatMap { 
            case i: core.kernel.InputEvent =>  i match
                case KeyEvent(KeyCode.ESCAPE, _, _) =>
                    List(SystemEvent.SigTerm)
                // case KeyEvent(KeyCode.L, InputAction.Release, _) =>
                //     List(RenderEvent.ShaderRegistration("id",
                //         "shaders/v1.glsl",
                //         "shaders/f1.glsl")) 
                case KeyEvent(KeyCode.A, InputAction.Press, _) =>
                    Artifact.move_left() 
                    List()
                case _ =>
                    println(f"Application got $i")
                    List()
            case _ => Nil
        }
        chrono.tick() match 
            case None => response
            case Some(delta) => 
                val adj = delta / 1e6
                println(adj)
                Artifact.update(chrono.time()/1e9f, delta)::response
    }
}


