package core.application

import core.kernel.*

class Application() extends core.kernel.Process {
    override val id: String = "Application"
    override def cycle(events: List[Event]): List[Event] = {
        events.foreach { 
          case Event.SigTerm =>
            this.setFlag(Process.Flag.ShouldShutdown, true)
        }
        List()
    }
}
