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

  def update(time: Float, delta: Float): RenderEvent = {
    val model_transform = local_transform.apply().mul(global_transform.apply())
    val array_transform = core.Util.toArray(model_transform)
    RenderEvent(_.drawModel(model_id, shader_id, array_transform))
  }
}

