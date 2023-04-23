package com.decoder

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{ Decoder, Encoder, HCursor, Json}
import io.circe.literal._
import io.circe.optics.JsonPath._
import io.circe.parser.parse


case class Fruit(id: Int, description: String, quantity: Int)

object Fruit {
  //Semi-automatic Derivation
  implicit val fooDecoder: Decoder[Fruit] = deriveDecoder[Fruit]
  implicit val fooEncoder: Encoder[Fruit] = deriveEncoder[Fruit]
}

trait FruitTable {
  def getFruitArrayOptic(): Option[List[Fruit]]
  def getFruitItemOptic(): Option[String]
  def getFruitArrayHCursor(): Option[Fruit]
  def getFruitItemHCursor(): Option[Double]
}

object FruitTable {
  def apply(): FruitTable = new FruitTable {
    val fruitJson: Json = parse("""
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

    override def getFruitArrayOptic(): Option[List[Fruit]] = {
      /**
        * https://circe.github.io/circe/optics.html
        * Optics are a powerful tool for traversing and modifying JSON documents. They can reduce boilerplate considerably, especially if you are working with deeply nested JSON.
        * circe provides support for optics by integrating with Monocle. To use them, add a dependency on circe-optics to your build:
        *
        * With optics we first define the traversal we want to make, then apply it to a JSON document
        */

      val items: Option[List[Json]] = root.order.items.arr.getOption(fruitJson).map(_.toList)
//      println(items)
      //Some(List({
      //  "id" : 123,
      //  "description" : "banana",
      //  "quantity" : 1
      //}, {
      //  "id" : 456,
      //  "description" : "apple",
      //  "quantity" : 2
      //}))

      val itemListOpt: Option[List[Fruit]] = for {
        vector <- items
        item = vector.map(json => json.as[Fruit]).collect{case Right(value) => value}
      } yield (item)
//      println(itemListOpt)
      //Some(List(Fruit(123,banana,1), Fruit(456,apple,2)))
      itemListOpt
    }

    def getFruitItemOptic(): Option[String] = {
      val phoneNumber: Option[String] = root.order.customer.contactDetails.phone.string.getOption(fruitJson)
//      println(("phoneNumber", phoneNumber))
      phoneNumber
    }

    override def getFruitArrayHCursor(): Option[Fruit] = {
      /**
        * With cursors, we start with a JSON document, get a cursor from it, and then use that cursor to traverse the document.
        * https://circe.github.io/circe/api/io/circe/ACursor.html
        *
        * Cursor provides functionality for moving around a tree and making modifications
        * HCursor tracks the history of operations performed. This can be used to provide useful error messages when something goes wrong.
        * ACursor also tracks history, but represents the possibility of failure (e.g. calling downField on a field that doesn’t exist)
        */
      val cursor: HCursor = fruitJson.hcursor

      val firstIndexArray: Option[Fruit] =
        cursor.downField("order").downField("items").downN(1).as[Fruit].toOption
//    println(firstIndexArray)
      //Some({
      //  "id" : 456,
      //  "description" : "apple",
      //  "quantity" : 2
      //})
      firstIndexArray
    }

    override def getFruitItemHCursor(): Option[Double] = {

      val cursor: HCursor = fruitJson.hcursor

      val optJson: Option[Json] =
        cursor.downField("order").downField("total").focus
      println(("optJson", optJson))
      //(optJson,Some(123.45))

      val optDouble =
        cursor.downField("order").downField("total").as[Double].toOption
      println(("optDouble", optDouble))
      //(optJson,Some(123.45))

      optDouble
    }
  }
}


