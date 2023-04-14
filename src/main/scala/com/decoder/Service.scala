package com.decoder

import cats.Applicative
import cats.implicits._

trait Service[F[_]]{
  def hello(n: Service.Name): F[Greeting]
  def getFruitArrayOptic(): F[Option[List[Fruit]]]
  def getFruitArrayHCursor(): F[Option[Fruit]]
}

object Service {
  final case class Name(name: String) extends AnyVal


  def impl[F[_]: Applicative]: Service[F] = new Service[F]{
    def hello(n: Service.Name): F[Greeting] =
        Greeting("Hello, " + n.name).pure[F]
    def getFruitArrayOptic(): F[Option[List[Fruit]]] = {
      FruitTable.apply().getFruitArrayOptic().pure[F]
    }
    def getFruitArrayHCursor(): F[Option[Fruit]] = {
      FruitTable.apply().getFruitArrayHCursor().pure[F]
    }
  }
}