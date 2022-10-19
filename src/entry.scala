import core.kernel.process.DefaulProcessDispatcher
object ManualKernelTest extends core.kernel.KernelApp {
  val renderer = new DefaulProcessDispatcher(new core.renderer.Renderer())
  val application = new DefaulProcessDispatcher(new core.application.Application())
  this.attach(renderer)
  this.attach(application)
  this.start()
  println("FINISHED")
}

