# Http4s and Circe Encoding/Decoding

# to run application
`sbt run`

# available on 
`http://localhost:8080`

# Circe
https://circe.github.io/circe/

# Http4s
https://http4s.org/


# Optics 
we first define the traversal we want to make, then apply it to a JSON document
```
  val json: Json = parse("""
        {
          "order": {
            "customer": {
              "name": "Custy McCustomer",
              "phone": "0123-456-789"
            },
            "items": [{
              "id": 123,
              "description": "banana",
              "quantity": 1
            }],
            "total": 123.45
          }
        }
        """)
        
  val items: Option[List[Json]] = root.order.items.arr.getOption(fruitJson).map(_.toList)

```
`order` and `item` are fields of the json object
`arr` attempts to decode to `jsonArray`. `obt` attempts to decode to JsonObject.  `string`, `int`, `double` etc are also available

from here we can attempt to decode the json to a type
```
      val itemListOpt: Option[List[Fruit]] = for {
        vector <- items
        item = vector.map(json => json.as[Fruit]).collect{case Right(value) => value}
      } yield (item)
      
      //Some(List(Fruit(123,banana,1), Fruit(456,apple,2)))
      

      val phoneNumber: Option[String] = root.order.customer.contactDetails.phone.string.getOption(fruitJson)
      //Some(0123-456-789))
```

# HCursor
With cursors, we start with a JSON document, get a cursor from it, and then use that cursor to traverse the document.
https://circe.github.io/circe/api/io/circe/ACursor.html

```
  val json: Json = parse("""
        {
          "order": {
            "customer": {
              "name": "Custy McCustomer",
            },
            "items": [{
              "id": 123,
              "description": "banana",
              "quantity": 1
            }],
            "total": 123.45
          }
        }
        """)

  val cursor: HCursor = json.hcursor

  val firstIndexArray: Option[Fruit] =
    cursor.downField("order").downField("items").downN(1).as[Fruit].toOption
      //Some({
      //  "id" : 456,
      //  "description" : "apple",
      //  "quantity" : 2
      //})
        
  val optDouble =
    cursor.downField("order").downField("total").as[Double].toOption
    //Some(123.45)
```

# forProductN decoder
create custom decoders using productN with up to 22 fields
```  
implicit val decodeCar: Decoder[Car] =
    Decoder.forProduct3("name", "engine", "wheels")(Car.apply)
 ```


## Algebraic data types encoding and decoding

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