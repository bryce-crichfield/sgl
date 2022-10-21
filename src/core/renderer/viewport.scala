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
import core.interface.*

class Viewport private (val pointer: Long) {
  private val event_output = new ListBuffer[kernel.Event]()
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
      val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
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
      event_output.addOne(event)
    }
  }
  glfwSetKeyCallback(pointer, key_callback)
  
  val button_callback: GLFWMouseButtonCallbackI = {
  (window, button, action, mods) =>
    {
      val code = MouseCode.from(button)
      val act = InputAction.from(action)
      val event = MouseEvent(code, act, mods)
      event_output.addOne(event)
    }
  }
  glfwSetMouseButtonCallback(pointer, button_callback)

  def flushEvent(): List[kernel.Event] = {
    val events = event_output.toList
    event_output.clear()
    events
  }

}

object Viewport {
  def create(): Unsafe[Viewport] = {
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
        glfwCreateWindow(800, 800, "", 0, 0)
      }
      _ <- Util.throwOn(pointer == 0)("glfwCreateWindow()")
      _ <- Unsafe {
        glfwMakeContextCurrent(pointer)
        GL.createCapabilities()
        glfwShowWindow(pointer)
      }
    } yield Viewport(pointer)
  }
}
