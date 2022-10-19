package core
package renderer

import scala.collection.mutable.ListBuffer


case class KeyEvent(code: KeyCode, action: InputAction, modifier: Int) extends core.kernel.InputEvent
case class MouseEvent(code: MouseCode, action: InputAction, mods: Int) extends core.kernel.InputEvent

// TODO: Reintegrate into the Viewport's input stream
// Represents the current state of the viewport's various
// control surfaces.
class ViewportController {
  val keyboard = new Keyboard()
  val mouse = new Mouse()
}

class Keyboard {
  private val event_buffer = ListBuffer.empty[KeyEvent]
  def push(event: KeyEvent): Unit = {
    event_buffer.append(event)
  }

  def poll(): List[KeyEvent] = {
    val out = event_buffer.toList
    event_buffer.clear()
    out
  }
}

class Mouse {
  private val event_buffer = ListBuffer.empty[MouseEvent]
  var x: Double = 0
  var y: Double = 0


  def push(event: MouseEvent): Unit = {
    event_buffer.append(event)
  }

  def poll(): ((Double, Double), List[MouseEvent]) = {
    val out = event_buffer.toList
    event_buffer.clear()
    (x, y) -> out
  }
}
