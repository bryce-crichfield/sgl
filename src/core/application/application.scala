package core.application

import core.kernel.*

class Application() extends core.kernel.process.Process {

    override val id: String = "Application"
    override def cycle(events: List[Event]): List[Event] = {
        events.foreach { 
            case i: core.kernel.InputEvent => 
                println(f"Application got $i")
            case _ => ()
        }
        List()
    }
}


