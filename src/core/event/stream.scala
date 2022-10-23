package core
package event



type Handler[A] = PartialFunction[A, Unit]


class Stream[A] {
  private var current_key = Long.MinValue
  private val handlers = new MutMap[Long, Handler[A]]

  def sink(handler: Handler[A]): () => Unit = {
    val key = current_key
    current_key = current_key + 1
    val total = handler.orElse { case _ => () }
    handlers.put(key, total)
    () => handlers.remove(key)
  }

  def source(value: A): Unit = {
    handlers.values.foreach(_.apply(value))
  }
}
