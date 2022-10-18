package object core {
    type Unsafe[+A] = cats.effect.IO[A]
    val Unsafe = cats.effect.IO

    type Writeback[A] = cats.effect.std.Queue[Unsafe, A]
    val Writeback = cats.effect.std.Queue

    // import org.lwjgl.*;
    // import org.lwjgl.opengl.*;
    // import org.lwjgl.system.*;
    // import java.io.File;
    // import java.nio.*;
    // import java.util.Scanner;
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
    // import org.lwjgl.glfw.*;
    // import org.lwjgl.glfw.Callbacks.*;
    // import org.lwjgl.glfw.GLFW.*;
}
