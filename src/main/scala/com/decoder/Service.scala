package com.decoder

import cats.Applicative
import cats.implicits._

trait Service[F[_]]{
  def hello(n: Service.Name): F[Greeting]
  def getFruitArrayOptic(): F[Option[List[Fruit]]]
  def getFruitItemOptic(): F[Option[String]]
  def getFruitArrayHCursor(): F[Option[Fruit]]
  def getFruitItemHCursor(): F[Option[Double]]
  def getCarProductNDecoder(): F[Option[List[Car]]]
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
    def getFruitItemOptic(): F[Option[String]] = {
      FruitTable.apply().getFruitItemOptic().pure[F]
    }
    def getFruitArrayHCursor(): F[Option[Fruit]] = {
      FruitTable.apply().getFruitArrayHCursor().pure[F]
    }
    def getFruitItemHCursor(): F[Option[Double]] = {
      FruitTable.apply().getFruitItemHCursor().pure[F]
    }
    def getCarProductNDecoder(): F[Option[List[Car]]] = {
      CarTable.apply().getCarProductNDecoder().pure[F]
    }
    def getAuth(request: String): F[Option[AuthResponse]] = {
      AuthTable.apply().getAuth(request: String).pure[F]
    }
  }
}