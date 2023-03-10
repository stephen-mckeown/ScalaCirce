package com.decoder

import cats.Applicative
import cats.implicits._

trait Service[F[_]]{
  def hello(n: Service.Name): F[Greeting]
  def work(w: Service.Work): F[Greeting]
  def getFruitsDecoder(): F[Option[List[Fruit]]]
}

object Service {
  final case class Name(name: String) extends AnyVal
  final case class Work(work: String) extends AnyVal


  def impl[F[_]: Applicative]: Service[F] = new Service[F]{
    def hello(n: Service.Name): F[Greeting] =
        Greeting("Hello, " + n.name).pure[F]
    def work(w: Service.Work): F[Greeting] =
      Greeting("Work, " + w.work).pure[F]
    def getFruitsDecoder(): F[Option[List[Fruit]]] = {
      FruitTable.apply().getFruits().pure[F]
    }
  }
}


//Encoder[A] instance provides a function that will convert any A to a Json
//Decoder[A] takes a Json value to either an exception or an A