package core.application

import core.kernel.*
import core.interface.*

class Application() extends core.kernel.process.Process {
    val chrono = core.kernel.Chronometer(120.0)
    override val id: String = "Application"
    val Artifact = new Artifact("gator", "id")
    override def cycle(events: List[Event]): List[Event] = {
        val response = events.flatMap { 
            case i: InputEvent =>  i match
                case KeyEvent(KeyCode.ESCAPE, _, _) =>
                    List(SystemEvent.SigTerm)
                case KeyEvent(KeyCode.A, InputAction.Repeat, _) =>
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


