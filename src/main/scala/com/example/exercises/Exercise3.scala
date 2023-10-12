package com.example.exercises

import cats.effect._

object Exercise3 extends IOApp.Simple {

  /**
   * Exercise: write an application that
   * 1. acquires as a Resource some arbitrary file1
   * 2. acquires as a Resource another arbitrary file2
   * 3. prints on the terminal the content of these two files, combined together
   * 4. prints on the terminal, right after the content of the previous files, a "." (a dot) every second forever
   * 5. when the application is stopped (either with Ctrl+C or other means for stopping the execution) it prints "Stopped ${file path}!"
   *  for each of the files you have acquired, and then the application exits
   *
   * Hint: use the skeleton of the implementation below as a starting point
   */

  override def run: IO[Unit] = (for {
        fileContent1 <- readFile("file1")
        filecontent2 <- readFile("file2")
      } yield (fileContent1, filecontent2)
    ).use { case (f1, f2) =>
      IO.println("" + /* print the combined contents*/ "") *>
        IO.unit // change this IO.unit with what you need to print the "." every 1 second
  }
}
