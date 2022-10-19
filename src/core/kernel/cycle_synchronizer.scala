package core
package kernel

// Default Communicator that the kernel
// will use to synchronize with subprocesses
private class CycleSynchronizer {
  // Invoked by the kernel to signal the subprocess to work
  // Awaited by the subprocess before starting work
  private val start_signal = new Signal()
  // Invoked by the subprocess to signal the kernel that it is done
  // Awaited by the kernel before continuing kernel-level work
  private val complete_signal = new Signal()

  def call_cycle(): Unit = {
    start_signal.ping()
  }
  def return_cycle(): Unit = {
    complete_signal.ping()
  }

  def await_call(): Unit = {
    start_signal.await()
  }
  def await_return(): Unit = {
    complete_signal.await()
  }
}
