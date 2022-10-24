package core.kernel.process

import core.event.*
import core.kernel.*

abstract class ProcessDispatcher(val process: Process) extends Channel[Event] {
  private val in_buffer = new AsyncBuffer[Event]()
  private val out_buffer = new AsyncBuffer[Event]()

  private[process] def launch(): Unit = {
    process.launch()
  }

  private[process] def cycle(): Unit = {
    val incoming = in_buffer.source()
    incoming.foreach(event => process.input_buffer.addOne(event))
    process.update()
    val outgoing = process.drain_out()
    out_buffer.sink(outgoing)
  }

  private[process] def shutdown(): Unit = {
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
