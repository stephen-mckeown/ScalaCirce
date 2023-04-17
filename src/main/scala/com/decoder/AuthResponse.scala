package com.decoder

import io.circe.{Decoder, Encoder, Json}
import io.circe.optics.JsonPath._
import io.circe.parser.parse
import cats.syntax.functor._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax._

sealed trait AuthResponse
case class Approved(name: String, operations: String) extends AuthResponse
case class Forbidden(reason: String, reasonCode: Int) extends AuthResponse
case class Error(errors: String) extends AuthResponse

object GenericDerivation {
  implicit val appEncoder: Encoder[Approved]  = deriveEncoder[Approved]
  implicit val appDecoder: Decoder[Approved]  = deriveDecoder[Approved]
  implicit val forbiddenEncoder: Encoder[Forbidden]  = deriveEncoder[Forbidden]
  implicit val forbiddenDecoder: Decoder[Forbidden]  = deriveDecoder[Forbidden]
  implicit val errEncoder: Encoder[Error]  = deriveEncoder[Error]
  implicit val errDecoder: Decoder[Error]  = deriveDecoder[Error]

  implicit val encodeAuth: Encoder[AuthResponse] = Encoder.instance {
    case approved @ Approved(_,_) => approved.asJson
    case forbidden @ Forbidden(_,_) => forbidden.asJson
    case error @ Error(_) => error.asJson
  }

  implicit val decodeAuth: Decoder[AuthResponse] =
    List[Decoder[AuthResponse]](
      Decoder[Approved].widen,
      Decoder[Forbidden].widen,
      Decoder[Error].widen,
    ).reduceLeft(_ or _)
}

trait AuthTable {
  def getAuth(request: String): Option[AuthResponse]
}

object AuthTable {
  import GenericDerivation._

  def apply(): AuthTable = new AuthTable {
    val authJson: Json = parse(
      """
        {"data":
          "approved": {
            "name": "Paul McGrath",
            "operations": "mutate"
          },
          "forbidden": {
            "reason": "unknown",
            "reasonCode": "1"
          },
           "error": {
            "errors": "server error"
          }
        }
        """).getOrElse(Json.Null)

     def getAuth(request: String): Option[AuthResponse] = {
       val asd = root.data.request
      val items: Option[Json] = asd.getOption(authJson)
      val itemListOpt: Option[AuthResponse] = items.map(a => a.as[AuthResponse]).flatMap(_.toOption)
       itemListOpt
    }
  }
}

/**
  * ADTs encoding and decoding

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