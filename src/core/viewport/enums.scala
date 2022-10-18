package core
package viewport

import org.lwjgl.glfw.GLFW.*

enum InputAction(code: Int) {
  case Press extends InputAction(GLFW_PRESS)
  case Repeat extends InputAction(GLFW_REPEAT)
  case Release extends InputAction(GLFW_RELEASE)
}
object InputAction {
  def from(code: Int): InputAction = {
    code match
      case GLFW_PRESS   => Press
      case GLFW_REPEAT  => Repeat
      case GLFW_RELEASE => Release
  }
}

enum KeyCode(val code: Int) {
  case UNKNOWN extends KeyCode(GLFW_KEY_UNKNOWN)
  case SPACE extends KeyCode(GLFW_KEY_SPACE)
  case APOSTROPHE extends KeyCode(GLFW_KEY_APOSTROPHE)
  case COMMA extends KeyCode(GLFW_KEY_COMMA)
  case MINUS extends KeyCode(GLFW_KEY_MINUS)
  case PERIOD extends KeyCode(GLFW_KEY_PERIOD)
  case SLASH extends KeyCode(GLFW_KEY_SLASH)
  case N0 extends KeyCode(GLFW_KEY_0)
  case N1 extends KeyCode(GLFW_KEY_1)
  case N2 extends KeyCode(GLFW_KEY_2)
  case N3 extends KeyCode(GLFW_KEY_3)
  case N4 extends KeyCode(GLFW_KEY_4)
  case N5 extends KeyCode(GLFW_KEY_5)
  case N6 extends KeyCode(GLFW_KEY_6)
  case N7 extends KeyCode(GLFW_KEY_7)
  case N8 extends KeyCode(GLFW_KEY_8)
  case N9 extends KeyCode(GLFW_KEY_9)
  case SEMICOLON extends KeyCode(GLFW_KEY_SEMICOLON)
  case EQUAL extends KeyCode(GLFW_KEY_EQUAL)
  case A extends KeyCode(GLFW_KEY_A)
  case B extends KeyCode(GLFW_KEY_B)
  case C extends KeyCode(GLFW_KEY_C)
  case D extends KeyCode(GLFW_KEY_D)
  case E extends KeyCode(GLFW_KEY_E)
  case F extends KeyCode(GLFW_KEY_F)
  case G extends KeyCode(GLFW_KEY_G)
  case H extends KeyCode(GLFW_KEY_H)
  case I extends KeyCode(GLFW_KEY_I)
  case J extends KeyCode(GLFW_KEY_J)
  case K extends KeyCode(GLFW_KEY_K)
  case L extends KeyCode(GLFW_KEY_L)
  case M extends KeyCode(GLFW_KEY_M)
  case N extends KeyCode(GLFW_KEY_N)
  case O extends KeyCode(GLFW_KEY_O)
  case P extends KeyCode(GLFW_KEY_P)
  case Q extends KeyCode(GLFW_KEY_Q)
  case R extends KeyCode(GLFW_KEY_R)
  case S extends KeyCode(GLFW_KEY_S)
  case T extends KeyCode(GLFW_KEY_T)
  case U extends KeyCode(GLFW_KEY_U)
  case V extends KeyCode(GLFW_KEY_V)
  case W extends KeyCode(GLFW_KEY_W)
  case X extends KeyCode(GLFW_KEY_X)
  case Y extends KeyCode(GLFW_KEY_Y)
  case Z extends KeyCode(GLFW_KEY_Z)
  case LEFT_BRACKET extends KeyCode(GLFW_KEY_LEFT_BRACKET)
  case BACKSLASH extends KeyCode(GLFW_KEY_BACKSLASH)
  case RIGHT_BRACKET extends KeyCode(GLFW_KEY_RIGHT_BRACKET)
  case GRAVE_ACCENT extends KeyCode(GLFW_KEY_GRAVE_ACCENT)
  case WORLD_1 extends KeyCode(GLFW_KEY_WORLD_1)
  case WORLD_2 extends KeyCode(GLFW_KEY_WORLD_2)
  case ESCAPE extends KeyCode(GLFW_KEY_ESCAPE)
  case ENTER extends KeyCode(GLFW_KEY_ENTER)
  case TAB extends KeyCode(GLFW_KEY_TAB)
  case BACKSPACE extends KeyCode(GLFW_KEY_BACKSPACE)
  case INSERT extends KeyCode(GLFW_KEY_INSERT)
  case DELETE extends KeyCode(GLFW_KEY_DELETE)
  case RIGHT extends KeyCode(GLFW_KEY_RIGHT)
  case LEFT extends KeyCode(GLFW_KEY_LEFT)
  case DOWN extends KeyCode(GLFW_KEY_DOWN)
  case UP extends KeyCode(GLFW_KEY_UP)
  case PAGE_UP extends KeyCode(GLFW_KEY_PAGE_UP)
  case PAGE_DOWN extends KeyCode(GLFW_KEY_PAGE_DOWN)
  case HOME extends KeyCode(GLFW_KEY_HOME)
  case END extends KeyCode(GLFW_KEY_END)
  case CAPS_LOCK extends KeyCode(GLFW_KEY_CAPS_LOCK)
  case SCROLL_LOCK extends KeyCode(GLFW_KEY_SCROLL_LOCK)
  case NUM_LOCK extends KeyCode(GLFW_KEY_NUM_LOCK)
  case PRINT_SCREEN extends KeyCode(GLFW_KEY_PRINT_SCREEN)
  case PAUSE extends KeyCode(GLFW_KEY_PAUSE)
  case F1 extends KeyCode(GLFW_KEY_F1)
  case F2 extends KeyCode(GLFW_KEY_F2)
  case F3 extends KeyCode(GLFW_KEY_F3)
  case F4 extends KeyCode(GLFW_KEY_F4)
  case F5 extends KeyCode(GLFW_KEY_F5)
  case F6 extends KeyCode(GLFW_KEY_F6)
  case F7 extends KeyCode(GLFW_KEY_F7)
  case F8 extends KeyCode(GLFW_KEY_F8)
  case F9 extends KeyCode(GLFW_KEY_F9)
  case F10 extends KeyCode(GLFW_KEY_F10)
  case F11 extends KeyCode(GLFW_KEY_F11)
  case F12 extends KeyCode(GLFW_KEY_F12)
  case F13 extends KeyCode(GLFW_KEY_F13)
  case F14 extends KeyCode(GLFW_KEY_F14)
  case F15 extends KeyCode(GLFW_KEY_F15)
  case F16 extends KeyCode(GLFW_KEY_F16)
  case F17 extends KeyCode(GLFW_KEY_F17)
  case F18 extends KeyCode(GLFW_KEY_F18)
  case F19 extends KeyCode(GLFW_KEY_F19)
  case F20 extends KeyCode(GLFW_KEY_F20)
  case F21 extends KeyCode(GLFW_KEY_F21)
  case F22 extends KeyCode(GLFW_KEY_F22)
  case F23 extends KeyCode(GLFW_KEY_F23)
  case F24 extends KeyCode(GLFW_KEY_F24)
  case F25 extends KeyCode(GLFW_KEY_F25)
  case KP_0 extends KeyCode(GLFW_KEY_KP_0)
  case KP_1 extends KeyCode(GLFW_KEY_KP_1)
  case KP_2 extends KeyCode(GLFW_KEY_KP_2)
  case KP_3 extends KeyCode(GLFW_KEY_KP_3)
  case KP_4 extends KeyCode(GLFW_KEY_KP_4)
  case KP_5 extends KeyCode(GLFW_KEY_KP_5)
  case KP_6 extends KeyCode(GLFW_KEY_KP_6)
  case KP_7 extends KeyCode(GLFW_KEY_KP_7)
  case KP_8 extends KeyCode(GLFW_KEY_KP_8)
  case KP_9 extends KeyCode(GLFW_KEY_KP_9)
  case KP_DECIMAL extends KeyCode(GLFW_KEY_KP_DECIMAL)
  case KP_DIVIDE extends KeyCode(GLFW_KEY_KP_DIVIDE)
  case KP_MULTIPLY extends KeyCode(GLFW_KEY_KP_MULTIPLY)
  case KP_SUBTRACT extends KeyCode(GLFW_KEY_KP_SUBTRACT)
  case KP_ADD extends KeyCode(GLFW_KEY_KP_ADD)
  case KP_ENTER extends KeyCode(GLFW_KEY_KP_ENTER)
  case KP_EQUAL extends KeyCode(GLFW_KEY_KP_EQUAL)
  case LEFT_SHIFT extends KeyCode(GLFW_KEY_LEFT_SHIFT)
  case LEFT_CONTROL extends KeyCode(GLFW_KEY_LEFT_CONTROL)
  case LEFT_ALT extends KeyCode(GLFW_KEY_LEFT_ALT)
  case LEFT_SUPER extends KeyCode(GLFW_KEY_LEFT_SUPER)
  case RIGHT_SHIFT extends KeyCode(GLFW_KEY_RIGHT_SHIFT)
  case RIGHT_CONTROL extends KeyCode(GLFW_KEY_RIGHT_CONTROL)
  case RIGHT_ALT extends KeyCode(GLFW_KEY_RIGHT_ALT)
  case RIGHT_SUPER extends KeyCode(GLFW_KEY_RIGHT_SUPER)
  case MENU extends KeyCode(GLFW_KEY_MENU)
  case LAST extends KeyCode(GLFW_KEY_LAST)
}
object KeyCode {
  // Since Scala's Enum.values method uses reflection,
  // it is very slow so we can just cache the values
  // to reduce access time by 4 orders of magnitude
  // (~200ms -> 0.02ms)
  private lazy val memo_values = KeyCode.values
  def from(glfwcode: Int): KeyCode =
    memo_values
      .find(_.code == glfwcode)
      .getOrElse(KeyCode.UNKNOWN)
}

enum MouseCode(val code: Int) {
  case UNKNOWN extends MouseCode(-1)
  case N1 extends MouseCode(GLFW_MOUSE_BUTTON_1)
  case N2 extends MouseCode(GLFW_MOUSE_BUTTON_2)
  case N3 extends MouseCode(GLFW_MOUSE_BUTTON_3)
  case N4 extends MouseCode(GLFW_MOUSE_BUTTON_4)
  case N5 extends MouseCode(GLFW_MOUSE_BUTTON_5)
  case N6 extends MouseCode(GLFW_MOUSE_BUTTON_6)
  case N7 extends MouseCode(GLFW_MOUSE_BUTTON_7)
  case N8 extends MouseCode(GLFW_MOUSE_BUTTON_8)
  case LAST extends MouseCode(GLFW_MOUSE_BUTTON_LAST)
  case LEFT extends MouseCode(GLFW_MOUSE_BUTTON_LEFT)
  case RIGHT extends MouseCode(GLFW_MOUSE_BUTTON_RIGHT)
  case MIDDLE extends MouseCode(GLFW_MOUSE_BUTTON_MIDDLE)
}
object MouseCode {
  private lazy val memo_values = MouseCode.values
  def from(glfwcode: Int): MouseCode =
    memo_values
      .find(_.code == glfwcode)
      .getOrElse(MouseCode.UNKNOWN)
}
