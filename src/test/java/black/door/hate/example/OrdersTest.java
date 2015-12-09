package black.door.hate.example;

import static black.door.util.Misc.list;

import black.door.hate.HalRepresentation;
import lombok.val;
import org.junit.Test;


/**
 * Created by nfischer on 12/8/2015.
 */
public class OrdersTest {

	@Test
	public void test() throws Exception{
		val basket1 = new Basket(97212);
		val basket2 = new Basket(97213);
		val cust1 = new Customer(7809);
		val cust2 = new Customer(12369);

		val order1 = new Order(123, 30.0, "USD", "shipped", basket1, cust1);
		val order2 = new Order(124, 20, "USD", "processing", basket2, cust2);

		HalRepresentation orderz = HalRepresentation.paginated(
				"orders", "/orders", list(order1, order2).stream(), 0, 2)
				.addProperty("currentlyProcessing", 14)
				.addProperty("shippedToday", 20)
				.build();

		System.out.println(orderz.serialize());
	}

}