package com.example

import cats.effect.{IO, IOApp}
import scala.concurrent.duration._
import cats.syntax.all._

object Cancelling extends IOApp.Simple {


  val chainOfIOs = IO("waiting") >> IO.canceled >> IO(42) >>= IO.println

  val specialPaymentSys = (IO("Payment running").flatMap(IO.println) >>
    IO.sleep(1.second) >>
    IO("payment completed").flatMap(IO.println)).onCancel(IO("Mega cancel of doom").flatMap(IO.println).void)

  val cancellationOfDoom = for {
    fib <- specialPaymentSys.start
    _ <- IO.sleep(500.millis) >> fib.cancel
    _ <- fib.join
  } yield ()

  val atomicPayment = IO.uncancelable(_ => specialPaymentSys) // masking

  val noCancelOfDoom = for {
    fib <- atomicPayment.start
    _ <- IO.sleep(500.millis) >> IO("Attempting cancellation").flatMap(IO.println) >> fib.cancel
    _ <- fib.join
  } yield ()


  val inputPass = IO("Input passwd") >> IO("Typing passwd") >> IO.sleep(2.second) >> IO("MyPassw0rd!")
  val verifyPass = (pw: String) => IO("Verifying...") >> IO.sleep(2.second) >> IO(pw == "MyPassw0rd!")

  val authFlow: IO[Unit] = IO.uncancelable { poll =>
    for {
      pw <- poll(inputPass).onCancel(IO("Auth time out. Try again later").void)
      verified <- verifyPass(pw)
      _ <- if (verified) IO("Auth successful")
           else IO("Auth failed")
    } yield ()
  }

  val authProgram = for {
    authFib <- authFlow.start
    _ <- IO.sleep(3.seconds) >> IO("Auth time out, attempting canecel") >> authFib.cancel
    _ <- authFib.join

  } yield ()
  
  override def run: IO[Unit] = cancellationOfDoom.void
}
