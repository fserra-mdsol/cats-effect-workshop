package com.example.exercises

import cats.effect.IO

object Exercise1 {

  import cats.syntax.all._

  /**
   * Exercises:
   *
   * The goal of these exercises is to get familiar with the combinators provided by
   * cats.effect.IO - please try and *NOT* use, wherever possible, .flatMap
   * In order to do it, use the suggestions by your IDE or read through the cats-effect docs.
   *
   * After implementing them, feel free to experiment with them and run them in the Main object
   * to prove that they work as expected.
   *
   * the cats.syntax.all._ import is already provided for you in order to use a natural syntax
   */


  /**
   * 1. Sequence 2 IOs and take the result of last only
   */
  def sequenceTakeLast[A, B](ioa: IO[A], iob: IO[B]): IO[B] = ???

  /**
   *   2. Sequence 2 IOs and take the result of first only
   */
  def sequenceTakeFirst[A, B](ioa: IO[A], iob: IO[B]): IO[A] = ???

  /**
   * 3. repeat an IO effect forever
   */
  def forever[A](io: IO[A]): IO[A] = ???

  /**
   * 4. convert IO to a different type
   */
  def convert[A, B](ioa: IO[A], value: B): IO[B] = ???

  /**
   * 5. discard a value from IO and just return Unit
   */
  def asUnit[A](ioa: IO[A]): IO[Unit] = ioa.void // ioa.map(_ => ())


  /**
   * 6. This implementation of a function returning a fibonacci series is very inefficient
   * and given enough time it will blow up the stack - try it for yourself!
   *
   * write a fibonacci function that doesn't crash on recursion
   */
  def fib(n: Int): BigInt =
    if (n < 2) 1
    else fib(n - 1) + fib(n - 2)

}
