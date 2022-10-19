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
trait InputEvent extends Event
trait RenderEvent extends Event
object Event {
  case object SigTerm extends Event
}
// A generic buffer designed to be used in a
// publish/flush loop
private class Buffer[E] {
  private val buffer = new Channel[E]()

  def pump(event: E): Unit = {
    buffer.offer(event)
  }

  def pump(event: List[E]): Unit = {
    event.foreach(event => buffer.offer(event))
  }

  def dump(): List[E] = {
    val drain = new ArrayList[E]()
    buffer.drainTo(drain)
    drain.asScala.toList
  }
}
