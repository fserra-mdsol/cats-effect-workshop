package com.example

import cats.effect.{IO, IOApp, Ref}
import cats.syntax.all._
import scala.concurrent.duration._

object Refs extends IOApp.Simple {

  val program: IO[Unit] = {
    def printTimeAndUpdateRef(ref: Ref[IO, Int]): IO[Unit] = for {
      _ <- IO.sleep(1.second)
      ms <- IO(System.currentTimeMillis())
      _ <- IO.println(ms)
      _ <- ref.update(_ + 1)
      _ <- printTimeAndUpdateRef(ref)
    } yield ()

    def countUpdates(ref: Ref[IO, Int]): IO[Unit] = for {
      _ <- IO.sleep(5.seconds)
      updates <- ref.get
      updates <- IO(s"Ref updates: $updates")
      _ <- IO.println(updates)
      _ <- countUpdates(ref)
    } yield ()

    for {
      ref <- IO.ref(0) // Ref.of[IO, Int](0)
      _ <- (printTimeAndUpdateRef(ref), countUpdates(ref)).parTupled
    } yield ()
  }


  override def run: IO[Unit] = program
}
