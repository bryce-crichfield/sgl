package core
package viewport

import org.lwjgl.glfw.*;
import org.lwjgl.glfw.Callbacks.*;
import org.lwjgl.glfw.GLFW.*;
import org.lwjgl.system.*
import org.lwjgl.system.MemoryStack.*;
import org.lwjgl.system.MemoryUtil.*;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.GL11.*;
import cats.effect.{IO, Resource}
import scala.collection.mutable.{ListBuffer}

case class Viewport (val pointer: Long) {
  bindToController()
  // TODO: Devise a way to strictly bind the lifetime of 
  // this event stream to this object
  private val system_event_buffer = new ListBuffer[SystemEvent]()
  private val input_event_buffer =  new ListBuffer[InputEvent]()

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

  // Called during the setup process to link, 
  // viewport's input to the controller
  private def bindToController(): Unit = {
    val key_callback: GLFWKeyCallbackI = { 
      (window, key, scancode, action, mods) =>
      {
        val code = KeyCode.from(key)
        val act = InputAction.from(action)
        val event = KeyEvent(code, act, mods)
        input_event_buffer.addOne(event)
      }
    }
    val button_callback: GLFWMouseButtonCallbackI = {
      (window, button, action, mods) =>
        {
          val code = MouseCode.from(button)
          val act = InputAction.from(action)
          val event = MouseEvent(code, act, mods)
          input_event_buffer.addOne(event)
        }
    }
    // val move_callback: GLFWCursorPosCallbackI = { 
    //   (window, x, y) =>
    //   {
    //     controller.mouse.x = x
    //     controller.mouse.y = y
    //   }
    // }
    glfwSetKeyCallback(pointer, key_callback)
    glfwSetMouseButtonCallback(pointer, button_callback)
    // glfwSetCursorPosCallback(pointer, move_callback)
  }

  // Note: This will spam the window_stream's event buffer
  // until the buffer is flushed, therefore hundreds of 
  // sigterms may accumulate before the first is resolved
  // We might want to provide some kind of unique signal facility
  glfwSetWindowCloseCallback(pointer, _ => {
    system_event_buffer.addOne(KernelEvent.SigTerm)
  })

  def systemEventDump(): Unsafe[List[SystemEvent]] = Unsafe {
    val events = system_event_buffer.toList
    system_event_buffer.clear()
    events
  }

  def inputEventDump(): Unsafe[List[InputEvent]] = Unsafe {
    val events = input_event_buffer.toList
    input_event_buffer.clear()
    events
  }



}
case class ViewportProvider(val viewport: Viewport) {
  def handle(): Resource[Unsafe, Viewport] = Resource.make {
    Unsafe { 
      glfwMakeContextCurrent(viewport.pointer)
      scala.util.Try(GL.getCapabilities()).toOption match
        case None => GL.createCapabilities()
        case Some(value) => ()
      
      // scala.util.Try(glGetCapabilities())
      // val out = GL.createCapabilities()
      viewport
    }
  } {
    viewport => Unsafe { 
      glfwMakeContextCurrent(MemoryUtil.NULL) 
    }
  }
}
object ViewportProvider {
  // Responsible for initializing and destroying 3 things
  // 1. The GLFW Context
  // 2. The OpenGL Context
  // 3. The Default Viewport
  // With these 3 things loaded, the application may begin
  // def load(kernel: Kernel): Resource[Unsafe, Viewport] = {
  def load(): Resource[Unsafe, ViewportProvider] = {
    Resource.make {
      for {
        init <- Unsafe {
          GLFWErrorCallback.createPrint(System.err).set()
          glfwInit()
        }
        _ <- Util.throwOn(!init)("glfwInit()")
        wid <- Unsafe {
          glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
          glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
          glfwCreateWindow(800, 800, "", 0, 0)
        }
        _ <- Util.throwOn(wid == 0)("glfwCreateWindow()")
        viewport <- Unsafe { Viewport(wid) }
        // _ <- Unsafe { 
        //   viewport.bindToController() 
        //   // viewport.bindToStream() // bind the viewport's own system callbacks
        //                                       // to the global event stream
        // }
        _ <- viewport.center()
      } yield ViewportProvider(viewport)
    } { provider =>
      Unsafe {
        glfwFreeCallbacks(provider.viewport.pointer)
        glfwDestroyWindow(provider.viewport.pointer)
        glfwTerminate()
        glfwSetErrorCallback(null).free()
      }
    }
  }
}
