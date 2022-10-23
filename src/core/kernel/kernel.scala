package core
package kernel

import java.util.ArrayList
import scala.jdk.CollectionConverters.*
import scala.collection.mutable.{ListBuffer, HashMap}
import java.io.PrintWriter
import java.io.FileWriter
import java.io.BufferedWriter
import core.kernel.process.*
import core.event.*
private class Kernel() extends Channel[Event] {
  val process_managers = new ListBuffer[ProcessManager]()
  // Kernel process do not directly write to the Kernel
  // rather, each maintains an outgoing buffer, which the
  // kernel will retrieve on update
  private val event_buffer = new AsyncBuffer[Event]()
  // Kernel Level Log, which is a collation of
  // subprocess logs and the kernel log itself
  // private val log_buffer = new Buffer[String]()
  def getFlag(flag: Kernel.Flag): Boolean = flags(flag)
  def setFlag(flag: Kernel.Flag, value: Boolean): Unit = {
    flags.put(flag, value)
  }
  val flags = {
    val map = new HashMap[Kernel.Flag, Boolean]()
    Kernel.Flag_Values.foreach(flag => map.put(flag, false))
    map
  }

  override def sink(events: List[Event]): Unit = {
    event_buffer.sink(events)
  }

  override def source(): List[Event] = {
    event_buffer.source()
  }

  override def inlet(events: List[Event]): List[Event] = {
    events.flatMap { event =>
      event match
        case SystemEvent.SigTerm =>
          this.setFlag(Kernel.Flag.ShouldShutdown, true)
          List(event)
        case _ => List(event)
    }
  }
  override def outlet(events: List[Event]): List[Event] = events

  def run(): Unit = {
    process_managers.foreach(_.start())
    while (!getFlag(Kernel.Flag.ShouldShutdown)) {
      val incoming = this.inlet(this.source())
      process_managers.foreach(_.sink(incoming))
      process_managers.foreach(_.call())
      val returned: List[Event] = process_managers.flatMap(_.source()).toList
      this.sink(this.outlet(returned))

    }
    process_managers.foreach(_.join())
  }

}
private object Kernel {
  private lazy val Flag_Values = Flag.values.toList
  private[kernel] enum Flag {
    case ShouldShutdown
  }
}
