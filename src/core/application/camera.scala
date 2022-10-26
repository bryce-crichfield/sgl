package core.application

import org.joml.{Vector3f, Matrix4f, Matrix3f}
import core.kernel.Chronometer


trait Camera {
  def tx(x: Float): Unit 
  def ty(y: Float): Unit 
  def tz(z: Float): Unit
  def rx(x: Float): Unit
  def ry(y: Float): Unit 
  def rz(z: Float): Unit
  def mvp(model_transform: Matrix4f): Matrix4f
}


class GlobalCamera extends Camera {
  var near: Float = 0.1f
  var fovy: Float = Math.PI.toFloat / 2
  var aspect: Float = 1
  var forward: Vector3f = new Vector3f(0, 0, -1)
  var up: Vector3f = new Vector3f(0,1, 0) 
  var right: Vector3f = new Vector3f(1,0,0)
  var position: Vector3f = new Vector3f(0, 0, 0)
  val chrono = Chronometer(1.0f)

  def tx(x: Float): Unit = {
    val direction_scalar_x = new Vector3f()
    right.mul(x, direction_scalar_x)
    position.add(direction_scalar_x)
  } 
  def ty(y: Float): Unit = {
    val direction_scalar_y = new Vector3f()
    up.mul(y, direction_scalar_y)
    position.add(direction_scalar_y)
  } 
  def tz(z: Float): Unit = {
    val direction_scalar_z = new Vector3f()
    forward.mul(z, direction_scalar_z)
    position.add(direction_scalar_z)
  }
  def rx(x: Float): Unit = {
    val rotation = new Matrix3f().rotate(x*.1f, new Vector3f(0, 1.0, 0))
    right.mul(rotation)
    up.mul(rotation)
    forward.mul(rotation)
  }
  def ry(y: Float): Unit = {
    val rotation = new Matrix3f().rotate(y*.1f, new Vector3f(1.0, 1.0, 0))
    right.mul(rotation)
    up.mul(rotation)
    forward.mul(rotation)
  } 
  def rz(z: Float): Unit = {
    val rotation = new Matrix3f().rotate(z*.1f, new Vector3f(0, 0.0, 1.0))
    right.mul(rotation)
    up.mul(rotation)
    forward.mul(rotation)
  }
  


  def mvp(model_transform: Matrix4f): Matrix4f = {
    val eye = new Vector3f()
    position.add(forward, eye)
    var camera = new Matrix4f().lookAt(position, eye, up)
    val projection = new Matrix4f().setPerspective(fovy, aspect, near, 
      100.0f)
    projection.mul((camera.mul(model_transform)))
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

// case RenderEvent.CameraTranslateX(scale) =>
  //   val direction_scalar_x = new Vector3f()
  //   camera.right.mul(scale, direction_scalar_x)
  //   camera.position.add(direction_scalar_x)

  // case RenderEvent.CameraTranslateY(scale) =>
  //   val direction_scalar_y = new Vector3f()
  //   camera.up.mul(scale, direction_scalar_y)
  //   camera.position.add(direction_scalar_y)

  // case RenderEvent.CameraTranslateZ(scale) =>
  //   val direction_scalar_z = new Vector3f()
  //   camera.forward.mul(scale, direction_scalar_z)
  //   camera.position.add(direction_scalar_z)


  // case RenderEvent.CameraPan(angle) =>
  //   val rotation = new Matrix3f().rotate(angle*.1f, new Vector3f(0, 1.0, 0))
  //   camera.right.mul(rotation)
  //   camera.up.mul(rotation)
  //   camera.forward.mul(rotation)
  // case RenderEvent.CameraTilt(angle) =>
  //   val rotation = new Matrix3f().rotate(angle*.1f, new Vector3f(1.0, 1.0, 0))
  //   camera.right.mul(rotation)
  //   camera.up.mul(rotation)
  //   camera.forward.mul(rotation)
  // case RenderEvent.CameraRoll(angle) =>
  //   val rotation = new Matrix3f().rotate(angle*.1f, new Vector3f(0, 0.0, 1.0))
  //   camera.right.mul(rotation)
  //   camera.up.mul(rotation)
  //   camera.forward.mul(rotation)
