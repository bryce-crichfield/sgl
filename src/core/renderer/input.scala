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
  private val event_buffer = MutBuf.empty[MouseEvent]
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
