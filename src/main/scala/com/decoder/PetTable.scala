package com.decoder

import io.circe.{Decoder, Encoder, Json}
import io.circe.literal._
import io.circe.optics.JsonPath._
import io.circe.parser.parse

case class Pet(name: String, engine: String, wheels: Int)
//sealed trait Animal
//
//case class Cat(i: String) extends Event
//case class Dog(s: String) extends Event
//case class Horse(c: String) extends Event
//case class Qux(values: List[String]) extends Event

object Pet {
  //forProductN helper
  implicit val decodeCar: Decoder[Pet] =
    Decoder.forProduct3("name", "engine", "wheels")(Pet.apply)

  implicit val encodeCar: Encoder[Pet] =
    Encoder.forProduct3("id", "first_name", "last_name")(c =>
      (c.name, c.engine, c.wheels)
    )
}

trait PetTable {
  def getPet(): Option[List[Pet]]
}

object PetTable {
  def apply(): PetTable = new PetTable {
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

     def getPet(): Option[List[Pet]] = {
      val items: Option[List[Json]] = root.order.items.arr.getOption(carJson).map(_.toList)

      val itemListOpt: Option[List[Pet]] = for {
        vector <- items
        item = vector.map(_.as[Pet]).collect { case Right(value) => value }
      } yield (item)
      itemListOpt
    }
  }
}


///import cats.syntax.functor._
//import io.circe.{ Decoder, Encoder }, io.circe.generic.auto._
//import io.circe.syntax._
//
//object GenericDerivation {
//  implicit val encodeEvent: Encoder[Event] = Encoder.instance {
//    case foo @ Foo(_) => foo.asJson
//    case bar @ Bar(_) => bar.asJson
//    case baz @ Baz(_) => baz.asJson
//    case qux @ Qux(_) => qux.asJson
//  }
//
//  implicit val decodeEvent: Decoder[Event] =
//    List[Decoder[Event]](
//      Decoder[Foo].widen,
//      Decoder[Bar].widen,
//      Decoder[Baz].widen,
//      Decoder[Qux].widen
//    ).reduceLeft(_ or _)
//}


/**
# The Product Type Pattern
Our first pattern is to model data that contains other data.
We might describe this as “A has a B and C”.

For example,
a Cat has a colour and a favourite food;
a Visitor has an id and a creation date; and so on.

The way we write this is to use a case class.
We’ve already done this many
times in exercises; now we’re formalising the pattern.

If A has a b (with type B) and a c (with type C) write
```
case class A(b: B, c: C)
 // or
trait A {
def b: B
def c: C
}
```

# The Sum Type Pattern
Our next pattern is to model data that is two or more distinct cases.
We might describe this as  “A is a B or C”.
For example,
    a Feline is a Cat, Lion, or Tiger;
    a Visitor is an Anonymous or User; and so on.

We write this using the sealed trait / final case class pattern.
Sum Type Pattern
If A is a B or C write
```
sealed trait A
final case class B() extends A
final case class C() extends A
```
  */