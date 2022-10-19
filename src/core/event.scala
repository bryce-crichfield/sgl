package core

import scala.collection.mutable.{HashMap, ListBuffer}

type Registration = () => Unit

class Registry[T] {
  private var current_key = Long.MinValue
  private val data = new HashMap[Long, T]()

  private def next_key(): Long = {
    if (current_key + 1 == Long.MaxValue ) {
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

trait EventStream[T] {
  protected val registry = new Registry[PartialFunction[T, Unit]]()
  def publish(value: T): Unit
  def register(handler: PartialFunction[T, Unit]): Registration = {
    val complete = handler.orElse { 
      case _ => ()
    }
    registry.assign(complete)
  }

  // Pipes the values of one stream's publications into another's
  // For each event in A, publish that event into B
  def into[B](that: EventStream[B])(f: PartialFunction[T, B]): Registration = {
    this.register { value =>
      f.lift(value) match
        case None => ()
        case Some(value) =>
          that.publish(value)
      
    }
  }
}

class EagerEventStream[T] extends EventStream[T] {
  override def publish(value: T): Unit = {
    registry.values().foreach(handler => handler.apply(value))
  }
}

class DeferredEventStream[T] extends EventStream[T] {
  private val event_queue = ListBuffer.empty[T]
  override def publish(value: T): Unit = {
    event_queue.append(value)
  }
  def flush(): Unit = {
    // println(f"Size of Event Stream before Flush ${event_queue.length}")
    for {
      handler <- registry.values()
      event <- event_queue
    } yield handler.apply(event)
    event_queue.clear()
    // println(f"Size of Event Stream after Flush ${event_queue.length}")
  }
}


