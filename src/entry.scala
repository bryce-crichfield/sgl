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



import scala.collection.mutable.{Queue}
import scala.collection.mutable.{ListBuffer as Buffer}


// class Application() {
//     val input = new Queue[(Int, Int)]()
//     val display = new Display(input)
//     def run(): Unit = {
//         // This line is critical for LWJGL's interoperation with GLFW's
//         // OpenGL context, or any context that is managed externally.
//         // LWJGL detects the context that is current in the current thread,
//         // creates the GLCapabilities instance and makes the OpenGL
//         // bindings available for use.
//         GL.createCapabilities();

//         // Set the clear color
//         glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

//         // Run the rendering loop until the user has attempted to close
//         // the window or has pressed the ESCAPE key.
//         while (!glfwWindowShouldClose(display.window)) {
//         glClear(
//             GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
//         ); // clear the framebuffer

//         glfwSwapBuffers(display.window); // swap the color buffers

//         // Poll for window events. The key callback above will only be
//         // invoked during this call.
//         glfwPollEvents();
//     }
//   }
// }

// trait RenderContext {
//     val content_providers: Buffer[ContentProvider]
//     def enter(): Unit
//     def render(): Unit = {
//         for {
//             provider <- content_providers
//             renderable <- provider.retrieve()
//         } yield renderable.render()
//     }
//     def exit(): Unit
// }
// class Mesh private () extends Renderable {
//     protected val vao = glGenVertexArrays()
//     protected val vbo = glGenBuffers()
// }
// trait Renderable {
//     def render(): Unit
// }
// trait ContentProvider {
//     def retrieve(): Buffer[Renderable]
// }


import cats.effect.*
import cats.effect.{SyncIO, IO}
import core.viewport.{Viewport, ViewportController}
import java.awt.Rectangle

trait SGLApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    for {
      kernel <- IO(new core.Kernel())
      _ <- kernel.run(new core.DefaultKernelSeeder()).to[IO]
    } yield ExitCode.Success
  }
}

object KernelTest extends SGLApp 


// object LoopTest extends SGLApp {
//   import core.model.{Rectangle, GuiVertex}
//   import org.joml.{Vector2f, Vector4f}
//   val rectangle = Rectangle(
//     GuiVertex(
//       Vector2f(-1.0f, -1.0f),
//       Vector4f(1.0, 0.0, 0.0, 1.0f)
//     ),
//     GuiVertex(
//       Vector2f(1.0f, -1.0f),
//       Vector4f(0.0, 1.0, 0.0, 1.0f)
//     ),
//     GuiVertex(
//       Vector2f(-1.0f, 1.0f),
//       Vector4f(0.0, 0.0, 1.0, 1.0f)
//     ),
//     GuiVertex(
//       Vector2f(1.0f, 1.0f),
//       Vector4f(1.0, 0.0, 1.0, 1.0f)
//     ),
//   )
//   override def execute(viewport: Viewport): SyncIO[Unit] = for {
//     _ <- SyncIO { glClearColor(1.0, 0.0, 0.0, 1.0) }
//     _ <- loop(viewport)
//   } yield()

//   def loop(viewport: Viewport): SyncIO[Unit] = for {
//     _ <- SyncIO { viewport.controller.mouse.poll() }
//     _ <- SyncIO { 
//       glClear(GL_COLOR_BUFFER_BIT) 
//       rectangle.render()
//       viewport.update()
//     }
//     _ <- if (viewport.close()) 
//         then SyncIO(()) 
//         else loop(viewport)
//   } yield ()
// }

// object ShaderLoadTest extends SGLApp {
//   override def execute(viewport: Viewport): SyncIO[Unit] = for {
//     _ <- core.Shader.load("shaders/v1.glsl", "shaders/f1.glsl").use {
//       shader => 
//         SyncIO(println("Doing Something With Shader"))
//     }
//   } yield ()
// }

object ClockTest extends App {
  val chrono = core.Chronometer(30)
  while(true) {
    chrono.tick() match 
      case None => ()
      case Some(delta) => println(delta/1e6)
  }
}

import scala.util.Random
import cats.effect.IO
import cats.implicits.*
import cats.effect.std.{CyclicBarrier, Queue as BlockingQueue}
import cats.effect.{Outcome, MonadCancel}

// object RecoveryBehavior extends IOApp {

//   def errorable: IO[Unit] = for { 
//     cond <- IO(Random.nextBoolean())
//     _ <- if cond then IO(())
//          else MonadCancel[IO].canceled
//   } yield ()

//   override def run(args: List[String]): IO[ExitCode] = {
//     for {
//       fiber <- errorable.start
//       outcome <- fiber.join flatMap {
//         case Outcome.Succeeded(_) =>
//           errorable
//         case Outcome.Errored(e) =>
//           IO.println(e)
//         case Outcome.Canceled() =>
//           IO.println("CANCELED")
//       }
//     } yield ExitCode.Success
//   }
// }

// object TwoThreads extends IOApp {

//   def producer(queue: BlockingQueue[IO, Int]): IO[Unit] = {
//     for {
//       int <- IO(Random.nextInt())
//       _ <- queue.offer(int)
//       _ <- IO.println(f"Producer made $int")
//     } yield ()
//   }

//   def consumer(queue: BlockingQueue[IO, Int]): IO[Unit] = {
//     for {
//       int <- queue.take
//       _ <- IO.println(f"Consumer got $int")
//     } yield ()
//   }

//   def loop(queue: BlockingQueue[IO, Int]): IO[Unit] = for {
//     barrier <- CyclicBarrier[IO](2)
//     p <- (producer(queue) >> barrier.await).start
//     c <- (consumer(queue) >> barrier.await).start
//     _ <- (p.join, c.join).tupled
//     _ <- loop(queue)
//   } yield ()

//   override def run(args: List[String]): IO[ExitCode] = {
//     for {
//       queue <- BlockingQueue.unbounded[IO, Int]
//       _ <- loop(queue)
//     } yield ExitCode.Success
//   }


// }