package core

import java.io.{File}
import java.util.Scanner
import cats.effect.{IO, Resource}

import cats.{Contravariant, Functor}
import cats.implicits._
import cats.effect._
import cats.effect.std.{Queue, QueueSource, QueueSink}
import cats.effect.unsafe.implicits.global

// def covariant(list: List[Int]): IO[List[Long]] = (
//   for {
//     q <- Queue.bounded[IO, Int](10)
//     qOfLongs: QueueSource[IO, Long] = Functor[QueueSource[IO, *]].map(q)(_.toLong)
//     _ <- list.traverse(q.offer(_))
//     l <- List.fill(list.length)(()).traverse(_ => qOfLongs.take)
//   } yield l
// )


// def contravariant[+A, +B](list: List[A]): IO[List[B]] = (
//   for {
//     q <- Queue.bounded[IO, Int](10)
//     qOfBools: QueueSink[IO, B] =
//       Contravariant[QueueSink[IO, [B] =>> B]].contramap(q)(b => if (b) 1 else 0)
//     _ <- list.traverse(qOfBools.offer(_))
//     l <- List.fill(list.length)(()).traverse(_ => q.take)
//   } yield l
// )




object Util {
    def scanner(path: String): Resource[Unsafe, Scanner] =
        Resource.make {
            for { 
                file <- Unsafe(new File(path))
            } yield new Scanner(file)
        } { scan => Unsafe(scan.close()) }

    def read(path: String): Unsafe[String] = {
        scanner(path).use {reader =>
            val buffer = new StringBuilder()
            while(reader.hasNextLine()) {
                buffer.append(reader.nextLine() + "\n")
            }
            Unsafe(buffer.toString())
        }
    }


    def throwOn(predicate: => Boolean)(debug: String = ""): Unsafe[Unit] = {
        if predicate 
        then Unsafe.raiseError(new RuntimeException(debug))
        else Unsafe(())
    }

    def logtime[A](f: => A): A = {
        val now = System.nanoTime()
        val out = f
        val time = System.nanoTime() - now
        println(f"Chrono: ${time.toDouble/1e6}ms")
        out
    }

}
