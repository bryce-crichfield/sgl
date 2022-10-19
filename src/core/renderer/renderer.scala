package core
package renderer

import core.kernel.{SystemEvent, Event}

import org.lwjgl.opengl.GL11.*;


class Renderer() extends core.kernel.process.Process {
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
        glClear(GL_COLOR_BUFFER_BIT)
        viewport.update()
        if viewport.close() 
        then List(SystemEvent.SigTerm)
        else viewport.flushEvent()
    }
}