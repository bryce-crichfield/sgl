package core.kernel

// Represent a single high or low signal
class Signal {
  private val channel = new Channel[Unit]()
  def ping(): Unit = {
    channel.offer(())
  } 

  // Wait for the signal to go high and then immediately set low
  // Consequently signal becomes a process invocation tool
  def await(): Unit = {
    channel.take()
  }
}