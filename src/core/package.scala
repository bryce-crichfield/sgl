package object core {
    // type Unsafe[+A] = cats.effect.IO[A]
    // val Unsafe = cats.effect.IO
    type Unsafe[+A] = scala.util.Try[A]
    val Unsafe = scala.util.Try

    object Util {
        def throwOn(predicate: => Boolean)(debug: String = ""): Unsafe[Unit] = {
            if predicate 
            then scala.util.Failure(new RuntimeException(debug))
            else scala.util.Success(())
        }
    }
}
