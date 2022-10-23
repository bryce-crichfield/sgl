package core.event

/** An EventPipe represents a generic node in the global event stream.
  *
  * The pipe can take in (sink) and put out (source) events. Each pipe has an
  * inlet and outlet function, that allows it to transform the event flow in a
  * bidirectional manner.
  *
  * Usage : Events Sunk => Inlet => [Work] => Outlet => Events Sourced
  */
trait Channel[A] {
  def inlet(events: List[A]): List[A]
  def outlet(events: List[A]): List[A]
  def sink(events: List[A]): Unit
  def source(): List[A]
}
