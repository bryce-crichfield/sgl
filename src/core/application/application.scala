package core.application

import core.kernel.*
import core.event.*

import org.joml.Vector3f
class Application() extends core.kernel.process.Process {
    val chrono = core.kernel.Chronometer(30.0)
    override val id: String = "Application"
    val artifact = new Artifact("boat", "id")

    artifact.local_transform.rotation_angle = 90.0f
    artifact.local_transform.rotation_axis = new Vector3f(1.0, 0.0, 0.0)
    // artifact.local_transform.scale = new Vector3f(.15f)
    artifact.local_transform.rotation_center = new Vector3f(.5, 0.0, 0.0)
    

    out(RenderEvent.LoadModel("boat", "resource/obj/boat.obj"))
    out(RenderEvent.LoadShader("id", "resource/shaders/v1.glsl", "resource/shaders/f1.glsl"))


    in {
        case _: Update => 
            // if (chrono.tick()) {
            //     val angle = artifact.local_transform.rotation_angle
            //     val time = chrono.time()
            //     val delta = chrono.difference().toFloat / 1e9.toFloat
            //     artifact.local_transform.rotation_angle = angle + delta
            // }
    }
    val move_speed = 0.01f
    in {
        case KeyEvent(code, _, _) => 
            code match    
            case KeyCode.W => out(RenderEvent.CameraTranslate(0, 0, -move_speed))
            case KeyCode.S => out(RenderEvent.CameraTranslate(0, 0, move_speed))
            case KeyCode.A => out(RenderEvent.CameraTranslate(move_speed, 0, 0))
            case KeyCode.D => out(RenderEvent.CameraTranslate(-move_speed, 0, 0))
            case _ => ()
    }


    override def update(): Unit = {
        drain_in()
       out(artifact.update(0, 0))
    }
}


