package core
package renderer

import core.kernel.{Event, Process}

import org.lwjgl.opengl.GL11.*;


class Renderer() extends core.kernel.Process {
    override val id: String = "Renderer"
    val chrono = Chronometer(60.0d)
    var viewport: Viewport = _
    
    override def launch(): Unit = {
        viewport = Viewport.create().getOrElse(null)
        glClearColor(1.0f, 0.0f, 0.0f, 1.0f)

    }

    override def shutdown(): Unit = {
        viewport.destroy()
    }
    override def cycle(events: List[Event]): List[Event] = {
        events.foreach { 
            case Event.SigTerm =>
                setFlag(Process.Flag.ShouldShutdown, true)
            case _ => ()
        }
        glClear(GL_COLOR_BUFFER_BIT)
        viewport.update()
        if viewport.close() 
        then List(Event.SigTerm)
        else viewport.flushEvent()
    }
}