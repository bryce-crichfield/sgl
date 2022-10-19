
object ManualKernelTest extends App {
  val kernel = new core.kernel.Kernel()
  kernel.subprocesses.addOne(new core.renderer.Renderer())
  kernel.subprocesses.addOne(new core.application.Application())
  kernel.run()
  println("FINISHED")
}
