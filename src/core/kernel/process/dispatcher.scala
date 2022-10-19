package core.kernel.process

import core.kernel.*

abstract class ProcessDispatcher(val process: Process) extends EventPipe
{
  private val in_buffer = new EventBuffer()
  private val out_buffer = new EventBuffer()

  private [process] def launch(): Unit = {
    process.launch()
  }

  private [process] def cycle(): Unit = {
    val incoming = in_buffer.source()
    val outgoing = process.cycle(incoming)
    out_buffer.sink(outgoing)
  }

  private [process] def shutdown(): Unit = {
    process.shutdown()
  }

  // Events from the manager
  def sink(events: List[Event]): Unit = {
    in_buffer.sink(this.inlet(events))
  }
  
  // Events out to the manager
  def source(): List[Event] = {
    this.outlet(out_buffer.source())
  }
}

