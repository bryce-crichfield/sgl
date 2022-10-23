package core
package kernel
package process

import scala.collection.mutable.HashMap
import core.event.*

trait Process {
  val id: String
  private val output_buffer = new MutBuf[Event]()
  private val input_stream = new Stream[Event]()


  final def out(event: Event): Unit = {
    output_buffer.addOne(event)
  }

  final def in(handler: Handler[Event]): () => Unit = {
    input_stream.sink(handler)
  }

  def launch(): Unit =
    println(f"$id Launch")

  final private [kernel] def cycle(events: List[Event]): List[Event] = {
    events.foreach(event => input_stream.source(event))
    update()
    val publication = output_buffer.toList
    output_buffer.clear()
    publication
  }

  def update(): Unit
  def shutdown(): Unit =
    println(f"$id Shutdown")
}

class DefaulProcessDispatcher(override val process: Process)
    extends ProcessDispatcher(process) {
  def inlet(events: List[Event]): List[Event] = events
  def outlet(events: List[Event]): List[Event] = events
}
