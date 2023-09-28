package com.example

import cats.Parallel
import cats.effect._
import cats.syntax.all._

object Combinators extends IOApp.Simple {

  // flatMap
  val ioFlatMapped = IO.println("Hello") flatMap(_ =>
    IO.println("World")
  )
  // alternative syntax
  val ioForComp = for {
    _ <- IO.println("Hello")
    _ <- IO.println("World")
  } yield ()
  // alternative syntax
  val ioArrow1 = IO.println("Hello") >> IO.println("World")
  val ioArrow2 = IO.println("Hello") *> IO.println("World")


  // mapN/parMapN
  val meaningOfLife: IO[Int] = IO(42)
  val favLang: IO[String] = IO("Scala")
  val goal = (meaningOfLife, favLang).mapN((n,s) => s"My goal is $n and $s")

  // parallel IO

  val parIO1: IO.Par[Int] = Parallel[IO].parallel(meaningOfLife)
  val parIO2: IO.Par[String] = Parallel[IO].parallel(favLang)

  import cats.effect.implicits._

  val goalPar = (parIO1, parIO2).mapN((n, s) => s"My goal is $n and $s")
  val goalParToSeq = Parallel[IO].sequential(goalPar)


  val goal3 = (meaningOfLife, favLang).parMapN((n, s) => s"My goal is $n and $s")

  // with failures
  val aFailure: IO[String] = IO(Thread.sleep(2000)) >>
    IO.raiseError(new RuntimeException("Error!"))

  val anotherFailure: IO[String] = IO.raiseError(new RuntimeException("Another error!"))

  val parWithFailure = (aFailure, favLang).parMapN(_ + _).handleErrorWith(_ => IO(43))
  // the first effect to fail is the one that gives the exception back

  override def run: IO[Unit] = ioFlatMapped.void
}
