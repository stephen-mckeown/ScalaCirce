package com.decoder

import io.circe.{Decoder, Encoder, Json}
import io.circe.literal._
import io.circe.optics.JsonPath._
import io.circe.parser.parse

case class Car(name: String, engine: String, wheels: Int)

object Car {
  //forProductN helper
  implicit val decodeCar: Decoder[Car] =
    Decoder.forProduct3("name", "engine", "wheels")(Car.apply)

  implicit val encodeCar: Encoder[Car] =
    Encoder.forProduct3("id", "first_name", "last_name")(c =>
      (c.name, c.engine, c.wheels)
    )
}

trait CarTable {
  def getCarProductNDecoder(): Option[List[Car]]
}

object CarTable {
  def apply(): CarTable = new CarTable {
    val carJson: Json = parse(
      """
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
              "name": "honda",
              "engine": "diesel",
              "wheels": 4
            }, {
               "name": "vw",
              "engine": "electric",
              "wheels": 4
            }]
          }
        }
        """).getOrElse(Json.Null)

     def getCarProductNDecoder(): Option[List[Car]] = {
      val items: Option[List[Json]] = root.order.items.arr.getOption(carJson).map(_.toList)

      val itemListOpt: Option[List[Car]] = for {
        vector <- items
        item = vector.map(_.as[Car]).collect { case Right(value) => value }
      } yield (item)
      itemListOpt
    }
  }
}


