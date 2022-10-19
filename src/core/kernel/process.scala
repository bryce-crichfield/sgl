package core.kernel

import scala.collection.mutable.HashMap

trait Process extends Thread {
  // Extending classes should not be fiddling with these facilities
  // But the kernel must so we specify the private accessor
  private[kernel] val cycle_synchronizer = new CycleSynchronizer()
  private[kernel] val incoming_event_buffer = new Buffer[Event]()
  private[kernel] val outgoing_event_buffer = new Buffer[Event]()
  private[kernel] val log_buffer = new Buffer[String]()
  private[kernel] var start_time = 0L

  def getFlag(flag: Process.Flag): Boolean = flags(flag)
  def setFlag(flag: Process.Flag, value: Boolean): Unit = {
    flags.put(flag, value)
  }
  val flags = {
    val map = new HashMap[Process.Flag, Boolean]()
    Process.Flag_Values.foreach(flag => map.put(flag, false))
    map
  }

  val id: String
  // Acquire thread specific resources
  def launch(): Unit = ()
  def cycle(events: List[Event]): List[Event]
  // Release thread specific resource
  def shutdown(): Unit = ()

  def log(debug: String): Unit = {
    val time = (System.currentTimeMillis() - start_time).toLong / 1000.0
    val formatted = f"$id:\t\t$time%1.3fms:\t$debug"
    log_buffer.pump(formatted)
  }

  override def run(): Unit = {
    start_time = System.currentTimeMillis()
    launch()
    // while (!kernel_communicator.kill_signal.check() ) {
    while (!getFlag(Process.Flag.ShouldShutdown)) {
      cycle_synchronizer.await_call()
      val events = cycle(incoming_event_buffer.dump())
      outgoing_event_buffer.pump(events)
      cycle_synchronizer.return_cycle()
    }
    shutdown()
  }
}

object Process {
  private val Flag_Values = Flag.values.toList
  enum Flag {
    case ShouldShutdown
  }
}
