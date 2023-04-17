package com.decoder

import cats.Applicative
import cats.implicits._

trait Service[F[_]]{
  def hello(n: Service.Name): F[Greeting]
  def getFruitArrayOptic(): F[Option[List[Fruit]]]
  def getFruitArrayHCursor(): F[Option[Fruit]]
  def getCar(): F[Option[List[Car]]]
  def getAuth(request: String): F[Option[AuthResponse]]
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
    def getCar(): F[Option[List[Car]]] = {
      CarTable.apply().getCar().pure[F]
    }
    def getAuth(request: String): F[Option[AuthResponse]] = {
      AuthTable.apply().getAuth(request: String).pure[F]
    }
  }
}