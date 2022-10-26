package core
package event



trait Event
case class Update() extends Event

trait SystemEvent extends Event
object SystemEvent {
  case object SigTerm extends SystemEvent
}

trait RenderService {
  def loadShader(id: String, vpath: String, fpath: String): Unit
  def loadModel(id: String, path: String): Unit
  def drawModel(model_id: String, shader_id: String, mvp: Array[Float]): Unit
}

trait RenderEvent extends Event {
  def apply(service: RenderService): Unit
}
object RenderEvent {
  def apply(f: RenderService => Unit): RenderEvent = {
    new RenderEvent { 
      override def apply(service: RenderService): Unit = 
        f(service)
    }
  }
}

trait InputEvent extends Event
case class KeyEvent(code: KeyCode, action: InputAction, modifier: Int) extends InputEvent
case class MouseEvent(code: MouseCode, action: InputAction, x: Float, y: Float) extends InputEvent
case class MousePosition(x: Float, y: Float) extends InputEvent




