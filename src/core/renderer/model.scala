package core
package renderer

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
import org.lwjgl.BufferUtils
import org.joml.Vector3f
import org.joml.Vector2f
import org.joml.Matrix4f
import java.nio.FloatBuffer
import org.lwjgl.assimp.*
import org.lwjgl.assimp.Assimp.*

case class MeshVertex(
    position: Vector3f,
    normal: Vector3f,
    texture_coordinates: Vector2f
) {
  def toBuffer(): FloatBuffer = {
    val floatBuffer =
      BufferUtils.createFloatBuffer(MeshVertex.number_of_elements)
    position.get(0, floatBuffer)
    normal.get(3, floatBuffer)
    texture_coordinates.get(6, floatBuffer)
    floatBuffer
  }
}
object MeshVertex {
  val number_of_elements = 8
  val bytesize = number_of_elements * 4
  val u_position_loc = 0
  val u_normal_loc = 1
  val u_texture_coordinate_loc = 2
  val position_byte_offset = 0
  val normal_byte_offset = 3 * 4
  val texture_coordinates_byte_offset = 6 * 4
}

case class Texture(
    id: Int,
    group: String
)

class Mesh(
    val vertices: MutBuf[MeshVertex] = new MutBuf[MeshVertex](),
    val indices: MutBuf[Int] = new MutBuf[Int](),
    val textures: MutBuf[Texture] = new MutBuf[Texture]()
) {
  private var vao: Int = _
  private var vbo: Int = _
  private var ebo: Int = _
  // Construct --------------------------------------------------------------
  vao = glGenVertexArrays()
  vbo = glGenBuffers()
  ebo = glGenBuffers()
  glBindVertexArray(vao)
  glBindBuffer(GL_ARRAY_BUFFER, vbo)
  glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
  // Fill the VBO with Vertices
  val floatBuffer = BufferUtils.createFloatBuffer(8 * vertices.length)
  for (vector <- vertices) do
    floatBuffer.put(vector.position.x)
    floatBuffer.put(vector.position.y)
    floatBuffer.put(vector.position.z)
    floatBuffer.put(vector.normal.x)
    floatBuffer.put(vector.normal.y)
    floatBuffer.put(vector.normal.z)
    floatBuffer.put(vector.texture_coordinates.x)
    floatBuffer.put(vector.texture_coordinates.y)
  floatBuffer.flip()
  glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW)
  // Fill the EBO with Indices
  glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
  val intBuffer = BufferUtils.createIntBuffer(indices.size)
  for (i <- indices) { intBuffer.put(i) }
  intBuffer.flip()
  glBufferData(GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL_STATIC_DRAW)
  // Configure Attributes
  // Position
  glEnableVertexAttribArray(MeshVertex.u_position_loc)
  glVertexAttribPointer(
    MeshVertex.u_position_loc,
    3,
    GL_FLOAT,
    false,
    MeshVertex.bytesize,
    MeshVertex.position_byte_offset
  )
  // Normal
  glEnableVertexAttribArray(MeshVertex.u_normal_loc)
  glVertexAttribPointer(
    MeshVertex.u_normal_loc,
    3,
    GL_FLOAT,
    false,
    MeshVertex.bytesize,
    MeshVertex.normal_byte_offset
  )
  // Texture Coordinates
  glEnableVertexAttribArray(MeshVertex.u_texture_coordinate_loc)
  glVertexAttribPointer(
    MeshVertex.u_texture_coordinate_loc,
    2,
    GL_FLOAT,
    false,
    MeshVertex.bytesize,
    MeshVertex.texture_coordinates_byte_offset
  )
  glBindVertexArray(0)
  // ------------------------------------------------------------------------

  def draw(shader: ShaderProgram, transform: Matrix4f, texture: Texture) = {
    shader.use()

    val array = Util.toArray(transform)
    println(array.mkString)
    glUniformMatrix4fv(0, false, array)
    // TODO: Bind uniforms and textures
    glBindVertexArray(vao)
    glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0)
    glBindVertexArray(0)
  }
}
object Mesh {
  def load(mesh: AIMesh): Mesh = {
    val out_vertex = new MutBuf[MeshVertex]()
    val out_indices = new MutBuf[Int]()

    for (i <- 0 until mesh.mNumVertices()) {
      val out_position = {
        val vector = mesh.mVertices().get(i)
        new Vector3f(vector.x(), vector.y(), vector.z())
      }
      val out = MeshVertex(
        out_position,
        new Vector3f(0.0f, 0.0f, 0.0f),
        new Vector2f(0.0f, 0.0f)
      )
      out_vertex.addOne(out)
    }

    for (i <- 0 until mesh.mNumFaces) {
      val face = mesh.mFaces.get(i)
      for (j <- 0 until face.mNumIndices) {
        out_indices.addOne(face.mIndices().get(j))
      }
    }
    // TODO: Add support for material/texture loading
    Mesh(out_vertex, out_indices, new MutBuf[Texture]())
  }
}

class Model private (private val meshes: MutBuf[Mesh]) {
  def draw(
      shader: ShaderProgram,
      transform: Matrix4f,
      texture: Texture
  ): Unit = {
    meshes.foreach(_.draw(shader, transform, texture))
  }

  def dispose(): Unit = {
    ()
  }
}
object Model {
  def load(path: String): Unsafe[Model] = Unsafe {
    val scene = aiImportFile(path, Assimp.aiProcess_Triangulate)
    val meshes = new MutBuf[Mesh]()
    val meshes_buffer = scene.mMeshes()
    for (i <- 0 until meshes_buffer.limit()) {
      val mesh = AIMesh.create(meshes_buffer.get(i))
      meshes.addOne(Mesh.load(mesh))
    }
    Model(meshes)
  }
}

class ModelLibrary {
  private val dictionary = new MutMap[String, Model]()
  def load(load_event: RenderEvent.LoadModel): Option[Model] = {
    Model
      .load(load_event.path)
      .onFail(error =>
        println(f"Renderer Failed Model Load: ${load_event.id}\n$error")
      )
      .toOption
      .map { model =>
        dictionary.put(load_event.id, model)
        model
      }
  }

  def get(id: String): Option[Model] = {
    dictionary.get(id)
  }

  def close(): Unit = {
    dictionary.values.foreach(_.dispose())
    dictionary.clear()
  }

}
