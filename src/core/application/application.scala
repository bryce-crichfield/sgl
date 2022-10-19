package core.application

import core.kernel.*
import java.awt.event.InputEvent

class Application() extends core.kernel.Process {
    override val id: String = "Application"
    override def cycle(events: List[Event]): List[Event] = {
        events.foreach { 
            case i: core.kernel.InputEvent => 
                println(f"Application got $i")
          case Event.SigTerm =>
            this.setFlag(Process.Flag.ShouldShutdown, true)
        }
        List()
    }
}
