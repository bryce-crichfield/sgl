package core
package renderer

import core.kernel.{Event, Process}

class Renderer() extends core.kernel.Process {
    override val id: String = "Renderer"
    val chrono = Chronometer(60.0d)
    var viewport: Viewport = _
    
    override def launch(): Unit = {
        viewport = Viewport.create().getOrElse(null)
    }

    override def shutdown(): Unit = {
        viewport.destroy()
    }
    override def cycle(events: List[Event]): List[Event] = {
        events.foreach { 
            case Event.SigTerm =>
                setFlag(Process.Flag.ShouldShutdown, true)
            case _ => println("OTHERWISE")
        }
        viewport.update()
        if viewport.close() 
        then List(Event.SigTerm)
        else List()
    }
}