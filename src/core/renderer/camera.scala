package core.renderer

import org.joml.{Vector3f, Matrix4f}
import core.kernel.Chronometer

class GlobalCamera {
  var near: Float = 0.1f
  var fovy: Float = Math.PI.toFloat / 2
  var aspect: Float = 1
  var forward: Vector3f = new Vector3f(0, 0, -1)
  var up: Vector3f = new Vector3f(0,1, 0) 
  var right: Vector3f = new Vector3f(1,0,0)
  var position: Vector3f = new Vector3f(0, 0, 0)
  val chrono = Chronometer(1.0f)

  def mvp_transform(model_transform: Matrix4f): Matrix4f = {
    val tmp = new Vector3f()
    position.add(forward, tmp)
    var camera = new Matrix4f().lookAt(position, tmp, up)
    // val model = model_transform.translate(position)
    // camera = camera.translate(position)
    val projection = new Matrix4f().setPerspective(fovy, aspect, near, 
      100.0f)
    projection.mul((camera.mul(model_transform)))
  }
}
