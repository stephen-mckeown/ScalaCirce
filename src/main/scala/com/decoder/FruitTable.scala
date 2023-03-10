package com.decoder

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, Json}
import io.circe.literal._
import io.circe.optics.JsonPath._
import io.circe.parser.parse

case class Fruit(id: Int, description: String, quantity: Int)

object Fruit {
  implicit val fooDecoder: Decoder[Fruit] = deriveDecoder[Fruit]
  implicit val fooEncoder: Encoder[Fruit] = deriveEncoder[Fruit]
}

trait FruitTable {
  def getFruits(): Option[List[Fruit]]
}

object FruitTable {
  def apply(): FruitTable = new FruitTable {
    override def getFruits(): Option[List[Fruit]] = {

      val response: Json = parse("""
        {
          "order": {
            "customer": {
              "name": "Custy McCustomer",
              "contactDetails": {
                "address": "1 Fake Street, London, England",
                "phone": "0123-456-789"
              }
            },
            "items": [{
              "id": 123,
              "description": "banana",
              "quantity": 1
            }, {
              "id": 456,
              "description": "apple",
              "quantity": 2
            }],
            "total": 123.45
          }
        }
        """).getOrElse(Json.Null)

      /**
        * https://circe.github.io/circe/optics.html
        * Optics are a powerful tool for traversing and modifying JSON documents. They can reduce boilerplate considerably, especially if you are working with deeply nested JSON.
        * circe provides support for optics by integrating with Monocle. To use them, add a dependency on circe-optics to your build:
        */

      val items: Option[List[Json]] = root.order.items.arr.getOption(response).map(_.toList)

      val itemListOpt: Option[List[Fruit]] = for {
        vector <- items
        item = vector.map(_.as[Fruit]).collect{case Right(value) => value}
      } yield (item)
      itemListOpt
    }
  }
}






//  final case class Greeting(greeting: String) extends AnyVal
//  object Greeting {
//    implicit val greetingEncoder: Encoder[Greeting] = new Encoder[Greeting] {
//      final def apply(a: Greeting): Json = Json.obj(
//        ("message", Json.fromString(a.greeting)),
//      )
//    }
//    implicit def greetingEntityEncoder[F[_]]: EntityEncoder[F, Greeting] =
//      jsonEncoderOf[F, Greeting]
//  }