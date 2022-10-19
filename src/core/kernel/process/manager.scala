package core.kernel.process
import core.kernel.*

import scala.collection.mutable.HashMap

class ProcessManager(dispatcher: ProcessDispatcher) extends Thread, EventPipe {
  private val cycle_synchronizer = new CycleSynchronizer()
  // I like to do this for fields, because it reduces the member variable overhead
  val flags = {
    val map = new HashMap[ProcessManager.Flag, Boolean]()
    ProcessManager.Flag_Values.foreach(flag => map.put(flag, false))
    map
  }
  def getFlag(flag: ProcessManager.Flag): Boolean = flags(flag)
  def setFlag(flag: ProcessManager.Flag, value: Boolean): Unit = {
    flags.put(flag, value)
  }

  def call(): Unit = {
    cycle_synchronizer.call_cycle()
    cycle_synchronizer.await_return()
  }

  override protected[kernel] def inlet(events: List[Event]): List[Event] = {
    events flatMap { event =>
      event match
        case sys_event: SystemEvent =>
          sys_event match
            case SystemEvent.SigTerm =>
              this.setFlag(ProcessManager.Flag.ShouldShutdown, true)
              Nil // consume the sigterm
            case _ => List(sys_event) // forward the event down
        case _ => List(event)
    }
  }

  override protected[kernel] def outlet(events: List[Event]): List[Event] = {
    events // return all event unaltered
  }

  // Events from kernel
  def sink(events: List[Event]): Unit = {
    dispatcher.sink(this.inlet(events))
  }

  // Events out to kernel
  def source(): List[Event] = {
    this.outlet(dispatcher.source())
  }

  final override def run(): Unit = {
    dispatcher.launch()
    while (!getFlag(ProcessManager.Flag.ShouldShutdown)) {
      cycle_synchronizer {
        dispatcher.cycle()
      }
    }
    dispatcher.shutdown()
  }

}
object ProcessManager {
  private val Flag_Values = Flag.values.toList
  enum Flag {
    case ShouldShutdown
  }
}
