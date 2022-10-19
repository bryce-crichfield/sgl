package core.kernel

import java.util.ArrayList
import scala.jdk.CollectionConverters.*

// Represents a system call made by a subprocess
// The kernel is responsible for routing events
// to concerning subprocesses.
// Because the Kernel will have to know how dispatch
// each event, this is currently sealed
// This is tricky
trait Event
trait SystemEvent extends Event
object SystemEvent {
  case object SigTerm extends SystemEvent
}
trait InputEvent extends Event
trait RenderEvent extends Event

trait EventPipe {
  protected [kernel] def inlet(events: List[Event]): List[Event]
  protected [kernel] def outlet(events: List[Event]): List[Event]
  protected [kernel] def sink(events: List[Event]): Unit
  protected [kernel] def source(): List[Event]
}

private [kernel] class EventBuffer {
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
