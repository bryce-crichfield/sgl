package core.kernel

import java.util.ArrayList
import scala.jdk.CollectionConverters.*

trait Event
trait SystemEvent extends Event
object SystemEvent {
  case object SigTerm extends SystemEvent
}


/** An EventPipe represents a generic node in the global event stream.
  *
  * The pipe can take in (sink) and put out (source) events. Each pipe has an
  * inlet and outlet function, that allows it to transform the event flow in a
  * bidirectional manner.
  *
  * Usage : Events Sunk => Inlet => [Work] => Outlet => Events Sourced
  */
private [kernel] trait EventPipe {
  def inlet(events: List[Event]): List[Event]
  def outlet(events: List[Event]): List[Event]
  def sink(events: List[Event]): Unit
  def source(): List[Event]
}

/** An EventBuffer is akin to a terminal node in the global event stream. */
private[kernel] class EventBuffer {
  private val buffer = new Channel[Event]()

  def sink(event: List[Event]): Unit = {
    event.foreach(event => buffer.offer(event))
  }

  def source(): List[Event] = {
    val drain = new ArrayList[Event]()
    buffer.drainTo(drain)
    drain.asScala.toList
  }
}
