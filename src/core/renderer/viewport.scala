package core
package renderer

import org.lwjgl.glfw.*;
import org.lwjgl.glfw.Callbacks.*;
import org.lwjgl.glfw.GLFW.*;
import org.lwjgl.system.*
import org.lwjgl.system.MemoryStack.*;
import org.lwjgl.system.MemoryUtil.*;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.GL11.*;
import scala.collection.mutable.{ListBuffer}
import core.event.*

class Viewport private (val pointer: Long, var width: Int, var height: Int) {

  private val event_output = new ListBuffer[Event]()
  val keyboard = new Keyboard()
  val mouse = new Mouse()
  def close(): Boolean = {
    glfwWindowShouldClose(pointer)
  }

  def update(): Unit = {
    glfwSwapBuffers(pointer)
    glfwPollEvents()
    
  }

  def center(): Unsafe[Unit] = {
    Unsafe(stackPush) map { stack =>
      val width_pointer = stack.mallocInt(1);
      val height_pointer = stack.mallocInt(1);
      glfwGetWindowSize(pointer, width_pointer, height_pointer);
      val monitor = glfwGetPrimaryMonitor()
      val vidmode = glfwGetVideoMode(monitor);
      glfwSetWindowPos(
        pointer,
        (vidmode.width() - width_pointer.get(0)) / 2,
        (vidmode.height() - height_pointer.get(0)) / 2
      );
    }
  }

  def destroy(): Unit = {
    glfwFreeCallbacks(pointer)
    glfwDestroyWindow(pointer)
    glfwMakeContextCurrent(MemoryUtil.NULL)
    glfwTerminate()
    glfwSetErrorCallback(null).free()
  }
  // This seems like a pain 

  val key_callback: GLFWKeyCallbackI = { 
    (window, key, scancode, action, mods) =>
    {
      val code = KeyCode.from(key)
      val act = InputAction.from(action)
      val event = KeyEvent(code, act, mods)
      keyboard.push(event)
    }
  }
  glfwSetKeyCallback(pointer, key_callback)
  
  val button_callback: GLFWMouseButtonCallbackI = {
  (window, button, action, mods) =>
    {
      val code = MouseCode.from(button)
      val act = InputAction.from(action)
      mouse.push(code, act)
    }
  }
  glfwSetMouseButtonCallback(pointer, button_callback)


  val move_callback: GLFWCursorPosCallback = {
    (window, x, y) => {
      val size = new org.joml.Vector2d(width, height)
      val coordinates = new org.joml.Vector2d(x, y)
        .div(size).mul(2).add(-1, -1).mul(1, -1)
      mouse.push(coordinates.x().toFloat, coordinates.y().toFloat)
    }
  }
  glfwSetCursorPosCallback(pointer, move_callback)
  glfwSetInputMode(pointer, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
  def flushEvent(): List[Event] = {
    event_output.addAll(keyboard.poll())
    event_output.addAll(mouse.poll())
    val events = event_output.toList
    event_output.clear()
    events
  }

}

object Viewport {
  def create(width: Int, height: Int): Unsafe[Viewport] = {
    for {
      init <- Unsafe {
        // TODO: Pipe this into kernel.log
        GLFWErrorCallback.createPrint(System.err).set()
        glfwInit()
      }
      _ <- Util.throwOn(!init)("glfwInit()")
      pointer <- Unsafe { 
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
        glfwCreateWindow(width, height, "", 0, 0)
      }
      _ <- Util.throwOn(pointer == 0)("glfwCreateWindow()")
      _ <- Unsafe {
        glfwMakeContextCurrent(pointer)
        GL.createCapabilities()
        glfwShowWindow(pointer)
      }
    } yield Viewport(pointer, width, height)
  }
}
