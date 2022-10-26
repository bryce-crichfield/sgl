package core
package renderer

import core.event.*

class Keyboard {
  private val event_buffer = MutBuf.empty[KeyEvent]
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
  private val event_buffer = MutBuf.empty[InputEvent]
  private var x: Float = 0
  private var y: Float = 0

  def push(code: MouseCode, action: InputAction): Unit = {
    event_buffer.addOne(MouseEvent(code, action, x, y))
  }

  def push(x: Float, y: Float): Unit = {
    this.x = x
    this.y = y
  }

  def poll(): List[InputEvent] = {
    event_buffer.addOne(MousePosition(x, y))
    val out = event_buffer.toList
    event_buffer.clear()
    out
  }
}
