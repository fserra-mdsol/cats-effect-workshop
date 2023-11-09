package com.example.exercises

import cats.effect.{Deferred, IO, IOApp, Ref}
import cats.syntax.all._

import scala.concurrent.duration._

object Exercise4 extends IOApp.Simple {

  /* Exercise 1:
  * Assuming 'sprinter' is a function that "registers and prints" when one of the fibers run by 'parSequence' finishes,
  * a. just by looking at the code below, can you identify what's potentially wrong with its implementation?
  * b. fix the code so that it's guaranteed to do the right thing
  * */

  private def sprinter(name: String, finishLine: Ref[IO, Int]): IO[Unit] =
    for {
      pos <- finishLine.updateAndGet(_ + 1)
      _ <- IO(println(s"$name arrived at position $pos"))
    } yield ()

  private def sprint(f: (String, Ref[IO, Int]) => IO[Unit]): IO[List[Unit]] = Ref[IO].of(0) flatMap { finishLine =>
    List(
      f("A", finishLine),
      f("B", finishLine),
      f("C", finishLine)
    ).parSequence
  }


/*
  override def run: IO[Unit] = sprint(sprinter).void
*/


  /*
  * Exercise 2:
  * Given the Consumer and Producer types described below, can you build a consumer/producer system
  * in which the messages produced are never lost? (hint: use Deferred to do it)
  * Note: the Consumer and Producer classes, and their relative companion object can't be changed for this exercise.
  * Make changes only to 'def consumer', 'def producer' and 'def program'
  * */

  class Consumer(ref: Ref[IO, String]) {
    def read: IO[String] = ref.get
  }

  private object Consumer {
    def setup(ref: Ref[IO, String]): IO[Consumer] = IO.sleep(500.millis) *> IO(new Consumer(ref))
  }

  class Producer(ref: Ref[IO, String]) {
    def write: String => IO[Unit] = ns => ref.update(_ => ns)
  }

  private object Producer {
    def setup(ref: Ref[IO, String]): IO[Producer] = IO.sleep(1500.millis) *> IO(new Producer(ref))
  }


  // impl 1
  def consumer(ref: Ref[IO, String]): IO[Unit] = for {
    c <- Consumer.setup(ref)
    msg <- c.read
    _ <- IO.println(s"Received $msg")
  } yield ()

  def producer(ref: Ref[IO, String]): IO[Unit] = for {
    p <- Producer.setup(ref)
    msg = "Msg A"
    _ <- p.write(msg)
    _ <- IO.println(s"Sent $msg")
  } yield ()


  // impl 2
  def consumer2(ref: Ref[IO, String], d: Deferred[IO, Unit]): IO[Unit] = for {
    c <- Consumer.setup(ref)
    _ <- d.get
    msg <- c.read
    _ <- IO.println(s"Received $msg")
  } yield ()


  def producer2(ref: Ref[IO, String], d: Deferred[IO, Unit]): IO[Unit] = for {
    p <- Producer.setup(ref)
    msg = "Msg A"
    _ <- p.write(msg)
    _ <- d.complete(())
    _ <- IO.println(s"Sent $msg")
  } yield ()


  def program: IO[Unit] = for {
    ref <- IO.ref("message lost!")
    deferred <- Deferred[IO, Unit]
    _ <- (consumer2(ref, deferred), producer2(ref, deferred)).parTupled
  } yield ()

  override def run: IO[Unit] = program

}
