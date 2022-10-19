package core

package object kernel {
  type Channel[A] = java.util.concurrent.LinkedBlockingQueue[A]
}
