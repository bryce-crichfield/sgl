// package core

// import org.lwjgl.*;
// import org.lwjgl.glfw.*;
// import org.lwjgl.opengl.*;
// import org.lwjgl.system.*;
// import java.io.File;
// import java.nio.*;
// import java.util.Scanner;
// import org.lwjgl.glfw.Callbacks.*;
// import org.lwjgl.glfw.GLFW.*;
// import org.lwjgl.opengl.GL11.*;
// import org.lwjgl.opengl.GL12.*;
// import org.lwjgl.opengl.GL13.*;
// import org.lwjgl.opengl.GL14.*;
// import org.lwjgl.opengl.GL15.*;
// import org.lwjgl.opengl.GL20.*;
// import org.lwjgl.opengl.GL21.*;
// import org.lwjgl.opengl.GL30.*;
// import org.lwjgl.opengl.GL31.*;
// import org.lwjgl.opengl.GL32.*;
// import org.lwjgl.opengl.GL33.*;
// import org.lwjgl.opengl.GL40.*;
// import org.lwjgl.opengl.GL41.*;
// import org.lwjgl.opengl.GL42.*;
// import org.lwjgl.opengl.GL43.*;
// import org.lwjgl.opengl.GL44.*;
// import org.lwjgl.opengl.GL45.*;
// import org.lwjgl.opengl.GL46.*;
// import org.lwjgl.system.MemoryStack.*;
// import org.lwjgl.system.MemoryUtil.*;
// import org.lwjgl.BufferUtils

// // Sets the OpenGL context in specific ways
// // For example, the GUI render and World render
// // use different shaders, coordinate systems, and
// // configuration options.
// trait Renderer {
//     val shaders: Map[String, Shader]
//     val renderables: Set[Renderable]
//     def begin(): Unit
//     def render(): Unit
//     def end(): Unit
// }

// trait Renderable {
//     def render(shader: Shader): Unit
// }

// import org.joml.Vector3f
// import org.joml.Vector2f
// import java.nio.FloatBuffer
// trait Bufferable[T] {
//     def buffer_size(): Int
//     def buffer(t: T): FloatBuffer
// }

// case class MeshVertex (
//     position: Vector3f,
//     normal: Vector3f,
//     texture_coordinates: Vector2f
// ) 
// given Bufferable[MeshVertex] with
//     def buffer_size(): Int = 8
//     def buffer(vertex: MeshVertex): FloatBuffer = {
//         val floatBuffer = BufferUtils.createFloatBuffer(buffer_size())
//         vertex.position.get(0, floatBuffer)
//         vertex.normal.get(3, floatBuffer)
//         vertex.texture_coordinates.get(6, floatBuffer)
//         floatBuffer
//     }
// case class Texture (
//     id: Int,
//     group: String
// )

// import scala.collection.mutable.ListBuffer
// trait Mesh {
//     var vao: Int
//     var vbo: Int
//     var ebo: Int
//     val vertices: ListBuffer[MeshVertex] = ListBuffer.empty
//     val indices : ListBuffer[Int] = ListBuffer.empty
//     val textures: ListBuffer[Texture] = ListBuffer.empty

//     def bind(): Unit = {
//         vao = glGenVertexArrays()
//         vbo = glGenBuffers()
//         ebo = glGenBuffers()
//         glBindVertexArray(vao)
//         // Fill the VBO with Vertices
//         glBindBuffer(GL_ARRAY_BUFFER, vbo)
//         val bufferable = summon[Bufferable[MeshVertex]]
//         var offset = 0
//         for (v <- vertices) {
//             glBufferSubData(GL_ARRAY_BUFFER, offset, bufferable.buffer(v))
//             offset = offset + bufferable.buffer_size()
//         }
//         // Fill the EBO with Indices
//         glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
//         val intBuffer = BufferUtils.createIntBuffer(indices.size)
//         for (i <- indices) { intBuffer.put(i) }
//         intBuffer.flip()
//         glBufferData(GL_ELEMENT_ARRAY_BARRIER_BIT, intBuffer, GL_STATIC_DRAW)
//         // Configure the Attribute Pointers in the VAO 
//         // for the VBO
//         glEnableVertexAttribArray(0) // Vertex.Position
//         glVertexAttribPointer(0, 3, GL_FLOAT, false, bufferable.buffer_size(), 0)
//         glEnableVertexAttribArray(1) // Vertex.Normal
//         glVertexAttribPointer(0, 3, GL_FLOAT, false, bufferable.buffer_size(), 3)
//         glEnableVertexAttribArray(2) // Vertex.TextCoord
//         glVertexAttribPointer(0, 3, GL_FLOAT, false, bufferable.buffer_size(), 6)
//     }
    
//     def renderable(): Renderable
// }