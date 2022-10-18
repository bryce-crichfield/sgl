package core
package model

import java.nio.*;
import org.joml.{Vector2f, Vector4f}

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

trait Renderable {
  def render(shader: Shader): Unit
}



case class GuiVertex (
  position: Vector2f,
  color: Vector4f
) {
  def render(): Unit = {
    glVertex2f(position.x, position.y)
    glColor4f(color.x, color.y, color.z, color.w)
  }
}


case class Rectangle(a: GuiVertex, b: GuiVertex, c: GuiVertex, d: GuiVertex) {
  def render(): Unit = {
    glBegin(GL_QUAD_STRIP)
    a.render()
    b.render()
    c.render()
    d.render()
    glEnd()
  }
}