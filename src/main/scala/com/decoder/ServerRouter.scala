package com.decoder

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder


object ServerRouter {
  import GenericDerivation._


  def jokeRoutes[F[_]: Sync](J: Jokes[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "joke" =>
        for {
          joke <- J.get
          resp <- Ok(joke)
        } yield resp
    }
  }

  def routes[F[_]: Sync](H: Service[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- H.hello(Service.Name(name))
          resp <- Ok(greeting)
        } yield resp
      case GET -> Root / "fruitArrayOptic"  =>
        for {
          fruitList <- H.getFruitArrayOptic()
          resp <- Ok(fruitList)
        } yield resp
      case GET -> Root / "getFruitArrayHCursor"  =>
        for {
          fruit <- H.getFruitArrayHCursor()
          resp <- Ok(fruit)
        } yield resp
      case GET -> Root / "getCar"  =>
        for {
          carList <- H.getCar()
          resp <- Ok(carList)
        } yield resp
      case GET -> Root / "getAuth"  =>
        for {
          auth <- H.getAuth("approved")
          resp <- Ok(auth)
        } yield resp
    }
  }
}