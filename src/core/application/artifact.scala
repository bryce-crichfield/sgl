package core.application

import org.joml.{Matrix4f, Vector3f}
import core.event.*

class Artifact(
  val model_id: String,
  val shader_id: String
) {
  var absolute_coordinates = false
  val local_transform = new ArtifactTransformation()
  val global_transform = new ArtifactTransformation()
  // radians per second
  var radians_per_second = 0

  def update(time: Float, delta: Float): RenderEvent.DrawModel = {
    val model_transform = local_transform.apply().mul(global_transform.apply())
    RenderEvent.DrawModel(model_id, shader_id, model_transform, absolute_coordinates)
  }
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
}