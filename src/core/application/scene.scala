package core.application

import core.event.RenderEvent
import org.joml.Vector3f
import org.joml.Matrix4f

class SceneNode {
  val model_id = "square"
  val shader_id = "id"
  val local_transform = new ArtifactTransformation()
  val children = new core.MutBuf[SceneNode]()


  def render(hierarchical_transform: Matrix4f = new Matrix4f()): List[RenderEvent] = {
    val model_transform = new Matrix4f()
    hierarchical_transform.mul(local_transform(), model_transform)

    val this_render = RenderEvent.DrawModel(model_id, shader_id, model_transform, false)
    val children_events = children.flatMap { child => child.render(model_transform) }.toList
    this_render::children_events
  }
}


