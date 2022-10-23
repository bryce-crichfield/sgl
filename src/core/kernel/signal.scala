package core.kernel

// Represent a single high or low signal
class Signal {
  private val queue = new java.util.concurrent.LinkedBlockingQueue[Unit]()
  def ping(): Unit = {
    queue.offer(())
  }

  // Wait for the signal to go high and then immediately set low
  // Consequently signal becomes a process invocation tool
  def await(): Unit = {
    queue.take()
  }
}
