package core
package kernel
package process

import scala.collection.mutable.HashMap
import core.kernel.SystemEvent.SigTerm

trait Process 
{
  val id: String
  def launch(): Unit =
    println(f"$id Launch")
  def cycle(events: List[Event]): List[Event]
  def shutdown(): Unit =
    println(f"$id Shutdown")
}

class DefaulProcessDispatcher(override val process: Process) 
  extends ProcessDispatcher(process) 
{
  def inlet(events: List[Event]): List[Event] = events
  def outlet(events: List[Event]): List[Event] = events
}




