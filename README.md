# hate
HATEOAS with HAL for Java

[![Build Status](https://travis-ci.org/blackdoor/hate.svg)](https://travis-ci.org/blackdoor/hate)  
[![Codacy Badge](https://api.codacy.com/project/badge/grade/7c1d6531e44941ed9e48b75435c9f1b8)](https://www.codacy.com/app/nfischer921/hate)  
[![Jitpack Badge](https://img.shields.io/badge/jitpack-available-blue.svg)](https://jitpack.io/#blackdoor/hate)

---
## Install with Maven

Get it with jitpack [HERE](https://jitpack.io/#blackdoor/hate)! (availability on maven central coming soon)

## Basic usage

Implement the `HalResource` interface in your model by implementing the `location()` and `asEmbedded()` methods.  
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
	public HalRepresentation asEmbedded() {
		return HalRepresentation.builder()
				.addProperty("total", total)
				.addProperty("currency", currency)
				.addProperty("status", status)
				.addLink("basket", basket)
				.addLink("customer", customer)
				.addLink("self", this)
				.build();
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

