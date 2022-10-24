package core
package event



trait Event
case class Update() extends Event

trait SystemEvent extends Event
object SystemEvent {
  case object SigTerm extends SystemEvent
}


trait RenderEvent extends Event
object RenderEvent {
  case class LoadShader(id: String, vpath: String, fpath: String) extends RenderEvent
  case class LoadModel(id: String, path: String) extends RenderEvent
  case class DrawModel(model_id: String, shader_id: String, model_transform: org.joml.Matrix4f, absolute: Boolean) extends RenderEvent

  case class CameraTranslateX (scale: Float) extends RenderEvent
  case class CameraTranslateY (scale: Float) extends RenderEvent
  case class CameraTranslateZ (scale: Float) extends RenderEvent
  case class CameraPan (angle: Float) extends RenderEvent
  case class CameraTilt (angle: Float) extends RenderEvent
  case class CameraRoll (angle: Float) extends RenderEvent
  case class CameraReset() extends RenderEvent


}

trait InputEvent extends Event
case class KeyEvent(code: KeyCode, action: InputAction, modifier: Int) extends InputEvent
case class MouseEvent(code: MouseCode, action: InputAction, mods: Int) extends InputEvent


