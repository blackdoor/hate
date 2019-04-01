# hate
HATEOAS with HAL for Java. Create hypermedia APIs by easily serializing your Java models into [HAL](http://stateless.co/hal_specification.html) JSON.  
More info in the [wiki](https://github.com/blackdoor/hate/wiki).

[![Gitter](https://img.shields.io/gitter/room/nwjs/nw.js.svg)](https://gitter.im/blackdoor/hate)  
[![Build Status](https://travis-ci.org/blackdoor/hate.svg)](https://travis-ci.org/blackdoor/hate)  
[![Codacy Badge](https://api.codacy.com/project/badge/grade/7c1d6531e44941ed9e48b75435c9f1b8)](https://www.codacy.com/app/blackdoor/hate)  
[![Codecov](https://img.shields.io/codecov/c/github/blackdoor/hate.svg)](https://codecov.io/github/blackdoor/hate)  
[![Javadocs](https://www.javadoc.io/badge/black.door/hate.svg?color=blue)](https://www.javadoc.io/doc/black.door/hate)  
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/black.door/hate/badge.svg)](http://mvnrepository.com/artifact/black.door/hate)

---
## Install with Maven

```xml
<dependencies>
  <dependency>
    <groupId>black.door</groupId>
    <artifactId>hate</artifactId>
    <version>v1r4t3</version>
  </dependency>
</dependencies>
```

## Basic usage

Implement the `HalResource` interface in your model by implementing the `location()` and `representationBuilder()` methods.
For example:

```java
public class Order implements HalResource{

	private int id;
	private double total;
	private String currency;
	private String status;
	//note: Basket and Customer implement HalResource
	private Basket basket;
	private Customer customer;

	...

	@Override
	public URI location(){
		return new URI("/orders/" + id);
	}
	
	@Override
	public HalRepresentationBuilder representationBuilder() {
		return HalRepresentation.builder()
				.addProperty("total", total)
				.addProperty("currency", currency)
				.addProperty("status", status)
				.addLink("basket", basket)
				.addLink("customer", customer)
				.addLink("self", this);
	}
}	
```

Now to get the `HalRepresentation` of an `Order` object, simply do `HalRepresentation hal = myOrder.asEmbedded()`. You can serialize `hal` using `hal.serialize()` or with Jackson (eg. `new ObjectMapper().writeValueAsString(hal)`)

The result would look like this:

```json
{
  "total": 30,
  "currency": "USD",
  "status": "shipped",
  "_links": {
    "basket": {
      "href": "/baskets/97212"
    },
    "self": {
      "href": "/orders/123"
    },
    "customer": {
      "href": "/customers/7809"
    }
  }
}
```

## Paginated Collections

To get a paginated HAL representation of a REST collection, simply use `HalRepresentation.paginated(String, String, Stream, long, long)`.  
For example:

```java
Collection<Order> orders;

HalRepresentation hal = HalRepresentation.paginated(
	"orders", // the name of the resource collection
	"/orders", // the path the resource collection can be found at
	orders.stream(), // the resources
	pageNumber,
	pageSize // the number of resources per page
	).build();
```

Would give you something like this:

```json
{
  "_links": {
    "next": {
      "href": "/orders?page=2"
    },
    "self": {
      "href": "/orders"
    }
  },
  "_embedded": {
    "orders": [
      {
        "total": 30,
        "currency": "USD",
        "status": "shipped",
        "_links": {
          "basket": {
            "href": "/baskets/97212"
          },
          "self": {
            "href": "/orders/123"
          },
          "customer": {
            "href": "/customers/7809"
          }
        }
      },
      {
        "total": 20,
        "currency": "USD",
        "status": "processing",
        "_links": {
          "basket": {
            "href": "/baskets/97213"
          },
          "self": {
            "href": "/orders/124"
          },
          "customer": {
            "href": "/customers/12369"
          }
        }
      }
    ]
  }
}
```

---
