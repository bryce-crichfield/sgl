package core

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.io.File;
import java.nio.*;
import java.util.Scanner;
import org.lwjgl.glfw.Callbacks.*;
import org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL12.*;
import org.lwjgl.opengl.GL13.*;
import org.lwjgl.opengl.GL14.*;
import org.lwjgl.opengl.GL15.*;
import org.lwjgl.opengl.GL20.*;
import org.lwjgl.opengl.GL21.*;
import org.lwjgl.opengl.GL30.*;
import org.lwjgl.opengl.GL31.*;
import org.lwjgl.opengl.GL32.*;
import org.lwjgl.opengl.GL33.*;
import org.lwjgl.opengl.GL40.*;
import org.lwjgl.opengl.GL41.*;
import org.lwjgl.opengl.GL42.*;
import org.lwjgl.opengl.GL43.*;
import org.lwjgl.opengl.GL44.*;
import org.lwjgl.opengl.GL45.*;
import org.lwjgl.opengl.GL46.*;
import org.lwjgl.system.MemoryStack.*;
import org.lwjgl.system.MemoryUtil.*;
import scala.collection.mutable.{ListBuffer, HashMap}
import core.viewport.{Viewport, ViewportProvider, KeyCode, KeyEvent}
import core.viewport.InputEvent

import cats.{Contravariant, Functor}
import cats.implicits._
import cats.effect._
import cats.effect.{Resource}
import cats.effect.std.{CyclicBarrier, Queue, QueueSource, QueueSink}
import cats.effect.unsafe.implicits.global

trait SystemEvent
object SystemEvent {
  case class Ack(string: String) extends SystemEvent
  case object Update extends KernelEvent
}
trait KernelEvent extends SystemEvent
object KernelEvent {
  case object SigTerm extends KernelEvent
}

trait RenderEvent
class Renderer { 
  private val chrono: Chronometer = Chronometer(1000000)
  var count_delta = 0
  var total_delta = 0.0d
  val big_list = List.fill(100000)(0)
  def cycle(viewport_provider: ViewportProvider, jobs: List[RenderEvent], writeback: Writeback[SystemEvent]): Unsafe[Unit] = for {
    _ <- chrono.tick() match
      case None => Unsafe(())
      case Some(delta) => viewport_provider.handle().use {
        viewport => 
          total_delta = total_delta + delta
          count_delta = count_delta + 1
          
          println((total_delta/count_delta) / 1e10)
          glClear(GL_COLOR_BUFFER_BIT)
          Unsafe(viewport.update()) 
      }
    ack <- Unsafe {
      SystemEvent.Ack("From Render")
    }
    _ <- writeback.offer(ack)
  } yield ()
}

// TODO: Define for RenderEvents
class Application {
  def cycle(input: List[InputEvent], writeback: Writeback[SystemEvent]): Unsafe[Unit] = for {
      // Update -> Events into Writeback
      ack <- Unsafe {
        SystemEvent.Ack(input.toString())
        // would really produce renderables and systemcalls
        // that we would then writeback with
      }
      _ <- writeback.offer(ack)

    } yield ()
}

case class KernelSeed(viewport_provider: ViewportProvider, renderer: Renderer, application: Application)
// A kernel seeder is responsible for loading all application-lifetime
// content, that will be required by the kernel 
trait KernelSeeder {
  def load(): Resource[Unsafe, KernelSeed]
}
class DefaultKernelSeeder extends KernelSeeder {
  def load(): Resource[Unsafe, KernelSeed] = {
    for {
      provider <- ViewportProvider.load()
    } yield KernelSeed(provider, Renderer(), Application())
  }
}

class Kernel {
  private val global_event_stream =  new ListBuffer[SystemEvent]()
  private val global_render_stream = new ListBuffer[RenderEvent]()

  private var is_running: Boolean = true

  def run(seeder: KernelSeeder): Unsafe[Unit] = {
    seeder.load().use { seed =>
      for {
        _ <- seed.viewport_provider.handle().use { viewport => 
            Unsafe {
              glfwSwapInterval(1);
              glfwShowWindow(viewport.pointer);
              glClearColor(1.0f, 0.0f, 0.0f, 1.0f)
            }
          }
        sys_call_stream <- Writeback.unbounded[Unsafe, SystemEvent]
        red_call_stream <- Writeback.unbounded[Unsafe, RenderEvent]
        _ <- kernel_loop(seed, sys_call_stream, red_call_stream)
      } yield ()
    }
  }

  def respond(sys_calls: List[SystemEvent]): Unsafe[Unit] = Unsafe {
    sys_calls.foreach { call => 
      call match
        case SystemEvent.Ack(msg) => ()
        case _ => ()
    }
  }


  // For now this is running well enough, however, it will likely be
  // necessary to more tightly control which io runs on which thread
  // in order to avoid making extraneous calls to glfwMakeContextCurrent
  def kernel_loop(seed: KernelSeed,
    system_events: Writeback[SystemEvent],
    render_events: Writeback[RenderEvent]
  ): Unsafe[Unit] = for {
      input_events <- seed.viewport_provider.handle().use{ viewport => 
        viewport.inputEventDump()
      }
      polled_render_events <- render_events.tryTakeN(None)
      output <- 
        forkRendererAndApplication (
          seed.viewport_provider,
          seed.renderer,
          seed.application,
          polled_render_events,
          input_events
        )
      viewport_sys_calls <- seed.viewport_provider.handle().use { viewport =>
        viewport.systemEventDump()  
      }
      collated: List[SystemEvent] = viewport_sys_calls:::output._1
      _ <- respond(collated)
      // _ <- render_events.offer(output._2)
      _ <- kernel_loop(seed, system_events, render_events)
    } yield ()

  def forkRendererAndApplication(
    viewport_provider: ViewportProvider,
    renderer: Renderer,
    application: Application,
    renderer_events: List[RenderEvent],
    input_events: List[InputEvent],
  ): Unsafe[(List[SystemEvent], List[RenderEvent])] = {
    for {
      render_writeback <- Queue.unbounded[Unsafe, SystemEvent]
      application_writeback <- Queue.unbounded[Unsafe, SystemEvent]
      barrier <- CyclicBarrier[Unsafe](2)
      renderer_fiber <- 
        (renderer.cycle(viewport_provider, renderer_events, render_writeback) >> barrier.await).start
      application_fiber <- 
        (application.cycle(input_events, application_writeback) >> barrier.await).start
      _ <- (renderer_fiber.join, application_fiber.join).tupled
      sysEvOutA <- render_writeback.tryTakeN(None) 
      sysEvOutB <- application_writeback.tryTakeN(None)
    } yield (sysEvOutA:::sysEvOutB) -> List()
  }

}



