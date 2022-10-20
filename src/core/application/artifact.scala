package core.application

import org.joml.{Matrix4f, Vector3f}
import core.kernel.RenderEvent.DrawCall

class Artifact(
  val model_id: String
) {
  val transformation = new ArtifactTransformation()
  transformation.scale = transformation.scale.mul(0.005f)
  transformation.rotation_axis = new Vector3f(1.0f, 1.0f, 0.0f)

  // radians per second
  var rate = Math.PI.toFloat / 2f

  def update(time: Float, delta: Float): DrawCall = {
    transformation.rotation_angle = rate * time
    DrawCall(model_id, transformation.apply())
  }

  export transformation.move_left

}

class ArtifactTransformation {
  var rotation_center = new Vector3f(0, 0, 0)
  var rotation_axis = new Vector3f(0, 0, 0)
  var rotation_angle = 0.0f
  var translate = new Vector3f(0, 0, 0)
  var scale = new Vector3f(1.0f, 1.0f, 1.0f)

  def apply(): Matrix4f = {
    new Matrix4f().identity()
      .translate(translate)
      .translate(rotation_center.mul(scale))
      .rotate(rotation_angle, rotation_axis)
      .translate(rotation_center.mul(-1).mul(scale))
      .scale(scale)
  }

  def move_left(): Unit = {
    translate = translate.sub(0.01, 0, 0)
  }
}