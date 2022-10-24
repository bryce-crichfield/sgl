package core
package kernel
import java.time.{Instant, Duration}

class Chronometer() {
  private var start: Long = _
  private var last: Long = _
  private var current: Long = _
  private var delta: Long = _
  private var interval: Long = _
  private var active: Boolean = false

  def reset(): Unit = {
    start = System.nanoTime()
    last = start
    current = last
    delta = current - last
  }

  def resume(): Unit = {
    active = true
  }

  def suspend(): Unit = {
    active = false
  }

  def tick(): Boolean = {
    if (!active) { return false }
    current = System.nanoTime()
    delta = current - last
    if delta > interval then
      last = current
      true
    else false
  }

  def time(): Long = {
    System.nanoTime()
  }

  def rate(fps: Double): Unit = {
    if (fps <= 0) then interval = 1
    else interval = (1e9 / fps).toLong
  }

  def difference(): Double = {
    this.delta
  }
}
object Chronometer {
  def apply(fps: Double): Chronometer = {
    val chrono = new Chronometer()
    chrono.rate(fps)
    chrono.reset()
    chrono.resume()
    chrono
  }
}
