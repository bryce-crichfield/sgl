package core

import cats.effect.IOApp

package object kernel {
  trait KernelApp extends App {
    private val kernel = new Kernel()
    def attach(dispatcher: process.ProcessDispatcher): Unit = {
      // kernel.subprocesses.addOne(process)
      kernel.process_managers.addOne(new process.ProcessManager(dispatcher))
    }
    def start(): Unit = kernel.run()
  }

}
