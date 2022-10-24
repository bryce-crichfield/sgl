package core.application

import core.kernel.*
import core.event.*

import org.joml.Vector3f
class Application() extends core.kernel.process.Process {
    val chrono = core.kernel.Chronometer(30.0)
    override val id: String = "Application"
    val root_node = new SceneNode()
    root_node.local_transform.translate = new Vector3f(0, 0, -2)
    root_node.local_transform.rotation_axis = new Vector3f(1.0, 0, 0)
    root_node.local_transform.rotation_angle = Math.PI.toFloat / 2

    val child = new SceneNode()
    child.local_transform.scale = new Vector3f(0.5, 0.5, 0.5)
    root_node.children.addOne(child)
    out(RenderEvent.LoadModel("square", "resource/obj/square.obj"))
    out(RenderEvent.LoadShader("id", "resource/shaders/v1.glsl", "resource/shaders/f1.glsl"))

    val move_speed = 0.1f
    in {
        case KeyEvent(code, _, _) => 
            code match    
            case KeyCode.W => out(RenderEvent.CameraTranslateZ(move_speed))
            case KeyCode.S => out(RenderEvent.CameraTranslateZ(-move_speed))
            case KeyCode.A => out(RenderEvent.CameraTranslateX(-move_speed))
            case KeyCode.D => out(RenderEvent.CameraTranslateX(move_speed))
            case KeyCode.Q => out(RenderEvent.CameraTranslateY(-move_speed))
            case KeyCode.E => out(RenderEvent.CameraTranslateY(move_speed))
            case KeyCode.H => out(RenderEvent.CameraPan(-0.1f))
            case KeyCode.K => out(RenderEvent.CameraPan(0.1f))
            case KeyCode.U => out(RenderEvent.CameraTilt(0.1f))
            case KeyCode.J => out(RenderEvent.CameraTilt(-0.1f))
            case KeyCode.Y => out(RenderEvent.CameraRoll(-.1f))
            case KeyCode.I => out(RenderEvent.CameraRoll(.1f))
            case KeyCode.F1 => out(RenderEvent.CameraReset())
            case _ => ()
    }
    override def update(): Unit = {
        drain_in()
        root_node.render().foreach(out)
    }
}


