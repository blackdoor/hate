package black.door.hate.example;

import black.door.hate.HalRepresentation;
import lombok.Getter;

/**
 * Created by nfischer on 12/8/2015.
 */
@Getter
public class Order extends Thing{

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


	@Override
	protected String resName() {
		return "orders";
	}
}
