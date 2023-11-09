package com.example

import cats.effect.kernel.Deferred
import cats.effect.{IO, IOApp}
import cats.syntax.all._

import scala.concurrent.duration._

object Deferreds extends IOApp.Simple {

  val aDeferred: IO[Deferred[IO, Int]] = Deferred[IO, Int]

  val reader: IO[Int] = aDeferred.flatMap { signal =>
    signal.get // blocks the fiber until another effect completes it
  }

  val writer: IO[Boolean] = aDeferred.flatMap { signal =>
    signal.complete(42)
  }

  def demoDeferred: IO[Unit] = {
    def consumer(s: Deferred[IO, Int]) = for {
      _ <- IO("[consumer] waiting for result...").flatTap(IO.println)
      mol <- s.get //semantically block
      _ <- IO(s"[consumer] got the result: $mol").flatTap(IO.println)
    } yield ()

    def producer(s: Deferred[IO, Int]) = for {
      _ <- IO(s"[producer] pretending to do some long computation...").flatTap(IO.println)
      //_ <- IO.sleep(5.second) //semantically sleep
      _ <- IO(s"[producer] finished long computation").flatTap(IO.println)
      mol <- IO(42)
      _ <- s.complete(mol)
    } yield ()

    for {
      signal <- Deferred[IO, Int]
      fibCons <- consumer(signal).start
      fibProd <- producer(signal).start
      _ <- fibCons.join
      _ <- fibProd.join
    } yield ()
  }


  // doc example

  def start(d: Deferred[IO, Int]): IO[Unit] = {
    val attemptCompletion: Int => IO[Unit] = n => d.complete(n).void

    val firstIo: IO[Either[Unit, Unit]] = IO.race(attemptCompletion(1), attemptCompletion(2))
    val secondIo = d.get.flatMap { n => IO(println(show"Result: $n")) }

    List(
      secondIo,
      firstIo
    ).parSequence.void
  }

  val program: IO[Unit] =
    for {
      d <- Deferred[IO, Int]
      _ <- start(d)
    } yield ()



  override def run: IO[Unit] = demoDeferred

}
