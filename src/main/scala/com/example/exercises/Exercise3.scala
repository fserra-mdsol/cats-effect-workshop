package com.example.exercises

import cats.effect._

import java.io.BufferedReader
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.concurrent.duration._
import scala.jdk.CollectionConverters._
import scala.language._

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

  private def readFile(path: String): Resource[IO, BufferedReader] =
    Resource.fromAutoCloseable(IO.blocking {
            Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)
          })
          .onFinalize { IO.println("Stopped " + path + "!") }

  override def run: IO[Unit] = (for {
        fileContent1 <- readFile("./src/main/scala/com/example/Cancelling.scala")
        fileContent2 <- readFile("./src/main/scala/com/example/Combinators.scala")
      } yield (fileContent1, fileContent2)
    ).use { case (f1, f2) =>
      IO.println(f1.lines().toList.asScala.mkString("\n") ++ f2.lines().toList.asScala.mkString("\n")) *>
        (IO.print(".") >> IO.sleep(1 second)).foreverM
  }
}
