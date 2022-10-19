package core.kernel

import scala.collection.mutable.{HashMap, ListBuffer}

type Registration = () => Unit

class SynchronousRegistry[T] {
  private var current_key = Long.MinValue
  private val data = new HashMap[Long, T]()

  private def next_key(): Long = {
    if (current_key + 1 == Long.MaxValue) {
      throw new RuntimeException("Registry[T].next_key() : Key Overflow")
    }
    current_key = current_key + 1
    current_key - 1
  }

  def assign(value: T): Registration = {
    val key = next_key()
    data.put(key, value)
    () => data.remove(key)
  }

  def values(): List[T] = {
    data.values.toList
  }
}

class SynchronousStream[T] {
  protected val registry = new SynchronousRegistry[PartialFunction[T, Unit]]()

  def publish(value: T): Unit = {
    registry.values().foreach(handler => handler.apply(value))
  }

  def register(handler: PartialFunction[T, Unit]): Registration = {
    val complete = handler.orElse { case _ =>
      ()
    }
    registry.assign(complete)
  }
}
