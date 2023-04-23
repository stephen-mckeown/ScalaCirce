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