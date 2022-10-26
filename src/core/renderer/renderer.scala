package core
package renderer
import core.event.*
import org.joml.{Vector3f, Matrix3f}
import org.lwjgl.opengl.GL11.*

class Renderer()
    extends core.kernel.process.Process
    with core.event.RenderService {
  override val id: String = "Renderer"
  val chrono = core.kernel.Chronometer(60.0d)
  var viewport: Viewport = _
  val shader_library = new ShaderLibrary()
  val model_library = new ModelLibrary()

  def loadShader(id: String, vpath: String, fpath: String): Unit = {
    println(f"LoadShader $id at $vpath and $fpath")
    shader_library.load(id, vpath, fpath)
  }

  def loadModel(id: String, path: String): Unit = {
    println(f"LoadModel $id at $path")
    model_library.load(id, path)
  }
  def drawModel(
      model_id: String,
      shader_id: String,
      mvp: Array[Float]
  ): Unit = {
    for {
      model <- model_library.get(model_id)
      shader <- shader_library.get(shader_id)
    } yield model.draw(shader, mvp, null)
  }

  var texture: Texture = _

  in { case renderEvent: RenderEvent =>
    renderEvent.apply(this)
  }

  override def launch(): Unit = {
    viewport = Viewport.create(1000,1000).getOrElse(null)
    glEnable(GL_DEPTH_TEST)
    // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    glClearColor(0.5f, 0.5, 0.5, 1.0f)
    texture = Texture.load("resource/texture/metal.png").get
  }

  override def shutdown(): Unit = {
    viewport.destroy()
    shader_library.close()
    model_library.close()
  }
  override def update(): Unit = {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    drain_in()
    viewport.update()
    if viewport.close()
    then out(SystemEvent.SigTerm)
    else viewport.flushEvent().foreach(out)
  }
}
