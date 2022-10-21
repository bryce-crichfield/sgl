package core.application

import org.joml.{Matrix4f, Vector3f}
import core.interface.*

class Artifact(
  val model_id: String,
  val shader_id: String
) {
  val local_transform = new ArtifactTransformation()
  val global_transform = new ArtifactTransformation()
  
  local_transform.scale = new Vector3f(0.05f)

  // radians per second
  var rate = Math.PI.toFloat / 2f
  // var rate = 0

// artf->animate(time, delta_time);

// auto model_transform = artf->animation_transform.matrix() * artf->world_transform.matrix();
// auto transform = vp_transform * model_transform;
  def update(time: Float, delta: Float): RenderEvent.DrawModel = {
    // local_transform.rotation_angle = time * rate
    val model_transform = local_transform.apply().mul(global_transform.apply())
    RenderEvent.DrawModel(model_id, shader_id, model_transform)
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

  def move_left(): Unit = {
    translate = translate.sub(0.01, 0, 0)
  }
}