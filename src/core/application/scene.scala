package core.application

import core.event.RenderEvent
import org.joml.Vector3f
import org.joml.Matrix4f

class SceneNode {
  val model_id = "square"
  val shader_id = "id"
  val local_transform = new ArtifactTransformation()
  val children = new core.MutBuf[SceneNode]()


  def render(camera: Camera, hierarchical_transform: Matrix4f = new Matrix4f()): List[RenderEvent] = {
    val model_transform = new Matrix4f()
    hierarchical_transform.mul(local_transform(), model_transform)
    val mvp_transform = core.Util.toArray(camera.mvp(model_transform))
    val this_render = RenderEvent(_.drawModel(model_id, shader_id, mvp_transform))
    val children_events = children.flatMap { child => child.render(camera, model_transform) }.toList
    this_render::children_events
  }
}


