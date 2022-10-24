package core.renderer

import org.lwjgl.stb.STBImage.*
import java.nio.ByteBuffer
import org.lwjgl.system.MemoryStack.*
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
import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream
case class Texture(
    gl_pointer: Long, width: Long, height: Long,
    data: ShortBuffer
) {
    def dispose(): Unit = {
        stbi_image_free(data)
    }
}

object Texture {
    def load(path: String): core.Unsafe[Texture] = {
        core.Unsafe {
            val stack = stackPush()
            val width = stack.mallocInt(1)
            val height = stack.mallocInt(1)
            val avChannels = stack.mallocInt(1)
            val decoded = stbi_load_16(path, width, height, avChannels, 0)
            val pointer = glGenTextures()
            val width_tex = width.get()
            val height_tex = height.get()
            glBindTexture(GL_TEXTURE_2D, pointer)
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width_tex, height_tex, 0, GL_RGBA, GL_UNSIGNED_BYTE, decoded)
            glGenerateMipmap(GL_TEXTURE_2D);
            // glBindTexture(GL_TEXTURE_2D, 0)
            Texture(pointer, width_tex, height_tex, decoded)
        }
    }
}





