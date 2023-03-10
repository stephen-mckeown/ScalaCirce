package com.decoder

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  val run = WebSever.run[IO]
}
