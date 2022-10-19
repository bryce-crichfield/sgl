package core
package kernel

import java.util.concurrent.{LinkedBlockingQueue as Channel}
import java.util.ArrayList
import scala.jdk.CollectionConverters.*
import scala.collection.mutable.{ListBuffer, HashMap}
import java.io.PrintWriter
import java.io.FileWriter
import java.io.BufferedWriter
import core.kernel.process.*
private class Kernel() extends EventPipe {
  val process_managers = new ListBuffer[ProcessManager]()
  // Kernel process do not directly write to the Kernel
  // rather, each maintains an outgoing buffer, which the
  // kernel will retrieve on update
  private val event_buffer = new EventBuffer()
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

  override protected [kernel] def sink(events: List[Event]): Unit = {
    event_buffer.sink(events)
  }
  
  override protected [kernel] def source(): List[Event] = {
    event_buffer.source()
  }

  override protected [kernel] def inlet(events: List[Event]): List[Event] = {
    events.flatMap { event => event match
      case SystemEvent.SigTerm =>
        this.setFlag(Kernel.Flag.ShouldShutdown, true)
        List(event)
      case _ => List(event)
    }
  }
  override protected [kernel] def outlet(events: List[Event]): List[Event] = events

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


  // private def event_cycle(): Unit = {
  //   // A. Kernel Event Buffer |=> Subprocess In Buffer
  //   // B. Kernel Intermediate Dispatch |=> Subprocess In Buffer
  //   // B. Subprocess Update |=> Subprocess Out Buffer
  //   // C. Subprocess In Buffer |=> Kernel Event Buffer
  //   val events_to_pump = event_buffer.source()
  //   intermediate_dispatch(events_to_pump)
  //   pump_process_input_buffers(events_to_pump)
  //   cycle_process()
  //   val events_to_dump = dump_process_output_buffers()
  //   event_buffer.pump(events_to_dump)
  // }

  // private def intermediate_dispatch(events: List[Event]): Unit = {
  //   events.foreach { event => event match
  //     case SystemEvent.SigTerm =>
  //       setFlag(Kernel.Flag.ShouldShutdown, true)
  //     case _ => 
  //       println(f"Kernel got $event")
  //     case null => ()
  //   }
  // }

  // private def pump_process_input_buffers(events: List[Event]): Unit = {
  //   // pump the kernel's event buffer into
  //   // subprocess parameter data
  //   // subprocesses.foreach { process =>
  //   //   process.incoming_event_buffer.pump(events)
  //   // }
  //   process_managers.foreach(_.sink(events))
  // }

  // private def cycle_process(): Unit = {
  //   // call for the subprocesses to act
  //   process_managers.foreach(_.call())
  //   // await the return of the subprocesses
  // }

  // private def dump_process_output_buffers(): List[Event] = {
  //   // dump the subprocess returned data into the
  //   // kernel's event buffer
  //   // subprocesses.flatMap { process =>
  //   //   process.outgoing_event_buffer.dump()
  //   // }.toList
  //   process_managers.flatMap(_.source()).toList
  // }
