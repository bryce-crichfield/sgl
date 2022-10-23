package core
package event



trait Event

trait SystemEvent extends Event
object SystemEvent {
  case object SigTerm extends SystemEvent
}


trait RenderEvent extends Event
object RenderEvent {
  case class LoadShader(id: String, vpath: String, fpath: String) extends RenderEvent
  case class LoadModel(id: String, path: String) extends RenderEvent
  case class DrawModel(model_id: String, shader_id: String, model_transform: org.joml.Matrix4f) extends RenderEvent
  case class CameraTranslate (x: Float, y: Float, z: Float) extends RenderEvent
}

trait InputEvent extends Event
case class KeyEvent(code: KeyCode, action: InputAction, modifier: Int) extends InputEvent
case class MouseEvent(code: MouseCode, action: InputAction, mods: Int) extends InputEvent


