package core.renderer

import org.joml.{Vector3f, Matrix4f}

class GlobalCamera {
  var near: Float = 0.1f
  var fovy: Float = Math.PI.toFloat / 2
  var aspect: Float = 1
  var forward: Vector3f = new Vector3f(0, 0, -1)
  var up: Vector3f = new Vector3f(1,0, 0) 
  var left: Vector3f = up.cross(forward)
  var position: Vector3f = new Vector3f(0, 0, 0)

  def mvp_transform(model_transform: Matrix4f): Matrix4f = {
    val matrix = new Matrix4f()
    val array = new Array[Float](16)
    val direction = new Vector3f()
    position.add(forward, direction)
    val camera = matrix.lookAt(position, direction, up)
    val projection = matrix.perspective(fovy, aspect, near, java.lang.Float.POSITIVE_INFINITY)
    val mvp = projection.mul(camera).mul(model_transform)
    // val mvp = camera.mul(model_transform)
    // mvp.get(array)
    // println(position.toString())
    // println(array.toList)
    mvp
  }
}
