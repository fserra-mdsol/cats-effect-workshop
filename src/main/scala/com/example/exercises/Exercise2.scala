package com.example.exercises

import cats.syntax.all._
import cats.effect.IO

object Exercise2 {


  /** Exercise 1:
   * Write an IO based program that performs 10 calls to https://httpbin.org/get
   * following the quick example on the http4s website to build a IO based HTTP client: https://http4s.org/v0.23/docs/client.html.
   * Assign an identifier or index to each of the calls, and make it print something (hint: use IO.println(..))
   * In order to implement this, for example, you can create a List of 10 integers, and then call .parTraverse on it, in order to get
   * the 10 calls run in parallel.
   **/


  /** Exercise 2:
   * Using the implementation of the program from the previous exercise, implement some logic that picks one of the calls and cancels it
   * or creates a failed IO (hint: user either IO.canceled or IO.raiseError(..), and observe that when the failure happens, all the other IOs
   * within the same context are canceled (hint: use .onCancel(..) to print out a message that shows that each of the other IOs has been canceled)
   **/

}
