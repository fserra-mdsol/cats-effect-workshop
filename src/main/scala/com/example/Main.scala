package com.example

import cats.effect.IOApp
import cats.effect.IO
import cats.effect.kernel.Outcome

object Main extends IOApp.Simple {

  // This is your new "main"!
  def run: IO[Unit] =
    StupidFizzBuzz.runIO.guaranteeCase {
      case Outcome.Succeeded(fa) => fa
      case Outcome.Canceled() => IO.println("I was cancelled!")
      case Outcome.Errored(_) => IO.println("I errored!")
    }
}
