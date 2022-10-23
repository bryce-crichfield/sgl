package core.event

import java.util.ArrayList
import java.util.concurrent.LinkedBlockingQueue
import scala.jdk.CollectionConverters.*

/** An EventBuffer is akin to a terminal node in the global event stream. */
class AsyncBuffer[A] {
  private val buffer = new LinkedBlockingQueue[A]()

  def sink(event: List[A]): Unit = {
    event.foreach(a => buffer.offer(a))
  }

  def source(): List[A] = {
    val drain = new ArrayList[A]()
    buffer.drainTo(drain)
    drain.asScala.toList
  }
}
