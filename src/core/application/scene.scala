package core.application

import core.event.RenderEvent
import org.joml.Vector3f

class Scene {
  val artifacts = new core.MutBuf[Artifact]()
  val addition = new Artifact("boat", "id")
  addition.local_transform.rotation_axis = new Vector3f(1.0, 0.0, 0.0)
  addition.local_transform.rotation_angle = Math.PI.toFloat / 2.0f
  addition.local_transform.translate.add(new Vector3f(0.0, 0.0, -1))
  addition.local_transform.scale = new Vector3f(1)
  artifacts.addOne(addition)

  def update(): List[RenderEvent] =
    artifacts.map(_.update(0, 0)).toList
}
