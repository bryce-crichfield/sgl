package core
package renderer

import core.kernel.{SystemEvent, Event, RenderEvent}

import org.joml.Vector3f

import org.lwjgl.opengl.GL11.*;
import core.kernel.RenderEvent.ShaderRegistration


class ShaderLibrary {
    val dictionary = new MutMap[String, ShaderProgram]()
    def register(registration: RenderEvent.ShaderRegistration): Option[ShaderProgram] = {
        dictionary.get(registration.id).orElse {
            ShaderProgram.load(registration.vpath, registration.fpath)
                .onFail(println).toOption.map { program =>
                    dictionary.put(registration.id, program)
                    program
                }
            
        }
    }  

    def get(registration: RenderEvent.ShaderRegistration): ShaderProgram = {
        dictionary.get(registration.id).getOrElse(throw new RuntimeException(f"SHDAER FAIL"))
    }

    def get(id: String): ShaderProgram = {
        dictionary.get(id).get
    }
    def close(): Unit = {
        dictionary.values.foreach(_.dispose())
        dictionary.clear()
    }
}

class Renderer() extends core.kernel.process.Process {
    override val id: String = "Renderer"
    val chrono = core.kernel.Chronometer(60.0d)
    var viewport: Viewport = _
    val shader_library = new ShaderLibrary()

    val model_shader = new ShaderRegistration("id", 
        "shaders/v1.glsl", "shaders/f1.glsl")
    var model: Model = _

    override def launch(): Unit = {
        viewport = Viewport.create().getOrElse(null)
        glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );

        glClearColor(0.5f,0.5,0.5, 1.0f)
        // triangle = new Triangle(positions, indices)
        model = Model.load("rsc/model/gator.obj")
        shader_library.register(model_shader)
        println("Renderer: Loading Shaders")
    }

    override def shutdown(): Unit = {
        viewport.destroy()
        shader_library.close()
    }
    override def cycle(events: List[Event]): List[Event] = {
        glClear(GL_COLOR_BUFFER_BIT)

        events.foreach {
            case RenderEvent.DrawCall(id, trans) =>
                println("DRAW MODEL")
                model.draw(shader_library.get(model_shader), trans, null)
            // case registration: RenderEvent.ShaderRegistration => 
            //     shader_library.register(registration) match
            //         case None => println(f"Failed to Register $registration")
            //         case Some(value) => println(f"Registered $value")
            case _ => ()
        }
        // model.draw(shader_library.get(model_shader),  null)
        viewport.update()
        if viewport.close() 
        then List(SystemEvent.SigTerm)
        else viewport.flushEvent()
    }
}