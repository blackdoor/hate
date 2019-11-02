package black.door.hate.example;

import black.door.hate.HalRepresentation;
import black.door.hate.HalResource;
import black.door.hate.JacksonHalResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

/**
 * Created by nfischer on 12/8/2015.
 */
@Getter
public class Order extends Thing implements JacksonHalResource{

	private double total;
	private String currency;
	private String status;
	private Basket basket;
	private Customer customer;

	public Order(long id, double total, String currency, String status,
				 Basket basket, Customer customer) {
		super(id);
		this.total = total;
		this.currency = currency;
		this.status = status;
		this.basket = basket;
		this.customer = customer;
	}


	@Override
	public HalRepresentation.HalRepresentationBuilder representationBuilder(ObjectMapper mapper) {
		return super.representationBuilder(mapper)
				.removeProperty("basket")
				.removeProperty("id")
				.removeProperty("customer")
				.addLink("basket", basket)
				.addLink("customer", customer);
	}


	@Override
	protected String resName() {
		return "orders";
	}
}
