package core
package kernel

import java.util.concurrent.{LinkedBlockingQueue as Channel}
import java.util.ArrayList
import scala.jdk.CollectionConverters.*
import scala.collection.mutable.{ListBuffer, HashMap}
import java.io.PrintWriter
import java.io.FileWriter
import java.io.BufferedWriter
import core.kernel.Event.SigTerm

class Log(path: String) {
  private val file = new java.io.File(path)
  if (file.exists()) {
    file.delete()
  }

  def write(strings: List[String]): Unit = {
    val writer = new FileWriter(path, true)
    val buffered_writer = new BufferedWriter(writer)
    strings.foreach { message =>
      buffered_writer.write(message)
      buffered_writer.newLine()
    }
    buffered_writer.close()
  }
}

private class Kernel() {
  private val log = new Log("kernel.log")
  val subprocesses = new ListBuffer[Process]()

  // Kernel process do not directly write to the Kernel
  // rather, each maintains an outgoing buffer, which the
  // kernel will retrieve on update
  private val event_buffer = new Buffer[Event]()
  // Kernel Level Log, which is a collation of
  // subprocess logs and the kernel log itself
  private val log_buffer = new Buffer[String]()

  def getFlag(flag: Kernel.Flag): Boolean = flags(flag)
  def setFlag(flag: Kernel.Flag, value: Boolean): Unit = {
    flags.put(flag, value)
  }
  val flags = {
    val map = new HashMap[Kernel.Flag, Boolean]()
    Kernel.Flag_Values.foreach(flag => map.put(flag, false))
    map
  }

  private def event_cycle(): Unit = {
    // A. Kernel Event Buffer |=> Subprocess In Buffer
    // B. Kernel Intermediate Dispatch |=> Subprocess In Buffer
    // B. Subprocess Update |=> Subprocess Out Buffer
    // C. Subprocess In Buffer |=> Kernel Event Buffer
    val process_input_pump = event_buffer.dump()
    process_input_pump.foreach(intermediate_dispatch)
    pump_process_input_buffers(process_input_pump)
    cycle_process()
    val dump = dump_process_output_buffers()
    event_buffer.pump(dump)
  }

  private def intermediate_dispatch(event: Event): Unit = {
    event match
      case Event.SigTerm =>
        setFlag(Kernel.Flag.ShouldShutdown, true)
      case _ => ()
      case null => ()

  }

  private def pump_process_input_buffers(events: List[Event]): Unit = {
    // pump the kernel's event buffer into
    // subprocess parameter data
    subprocesses.foreach { process =>
      process.incoming_event_buffer.pump(events)
    }
  }

  private def cycle_process(): Unit = {
    // call for the subprocesses to act
    subprocesses.foreach(_.cycle_synchronizer.call_cycle())
    // await the return of the subprocesses
    subprocesses.foreach(_.cycle_synchronizer.await_return())
  }

  private def dump_process_output_buffers(): List[Event] = {
    // dump the subprocess returned data into the
    // kernel's event buffer
    subprocesses.flatMap { process =>
      process.outgoing_event_buffer.dump()
    }.toList
  }

  def run(): Unit = {
    subprocesses.foreach(_.start())
    while (!getFlag(Kernel.Flag.ShouldShutdown)) {
      event_cycle()
      // We can add an optional log pump/up/dump step here too
    }
    subprocesses.foreach(_.join())
  }

}
private object Kernel {
  private lazy val Flag_Values = Flag.values.toList
  private[kernel] enum Flag {
    case ShouldShutdown
  }
}
