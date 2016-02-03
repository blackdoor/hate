package black.door.hate;

import black.door.hate.example.Basket;
import black.door.hate.example.Customer;
import black.door.hate.example.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.val;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.IntStream;

import static black.door.util.Misc.list;
import static java.util.Arrays.asList;
import static java.util.Arrays.fill;
import static org.junit.Assert.*;

/**
 * Created by nfischer on 12/13/15.
 */
public class HalRepresentationTest {

    private static final String RFC = "{\n" +
            "     \"_links\": {\n" +
            "       \"self\": { \"href\": \"/orders\" },\n" +
            "       \"next\": { \"href\": \"/orders?page=2\" }\n" +
            "     },\n" +
            "     \"_embedded\": {\n" +
            "       \"orders\": [{\n" +
            "           \"_links\": {\n" +
            "             \"self\": { \"href\": \"/orders/123\" },\n" +
            "             \"basket\": { \"href\": \"/baskets/98712\" },\n" +
            "             \"customer\": { \"href\": \"/customers/7809\" }\n" +
            "           },\n" +
            "           \"total\": 30.00,\n" +
            "           \"currency\": \"USD\",\n" +
            "           \"status\": \"shipped\"\n" +
            "         },{\n" +
            "           \"_links\": {\n" +
            "             \"self\": { \"href\": \"/orders/124\" },\n" +
            "             \"basket\": { \"href\": \"/baskets/97213\" },\n" +
            "             \"customer\": { \"href\": \"/customers/12369\" }\n" +
            "           },\n" +
            "           \"total\": 20.00,\n" +
            "           \"currency\": \"USD\",\n" +
            "           \"status\": \"processing\"\n" +
            "       }]\n" +
            "     },\n" +
            "     \"currentlyProcessing\": 14,\n" +
            "     \"shippedToday\": 20\n" +
            "   }";

    @Test
    public void testPagination() throws Exception{
        List<Order> orders = new LinkedList<>();

        IntStream.range(0, 100)
                .forEach(i ->
                    orders.add(new Order(i, i, "USD", "status",
                            new Basket(i), new Customer(i)))
                );

        val rep = HalRepresentation.paginated("orders", "/orders", orders.stream()
        , 2, 20)
                .build();

        assertEquals(20, rep.getMultiEmbedded().get("orders").size());
        assertEquals("/orders/20",
                rep.getMultiEmbedded().get("orders").get(0).asEmbedded().getLinks().get("self").asLink()
                        .getHref());
    }

    @Test
    public void testLinkToString() throws JsonProcessingException {
        Order o = new Order(1, 1, "USD", "status", new Basket(2), new Customer(3));

        HalLink link = o.asLink();
        System.out.println(link.toString());
        assertTrue(link.toString().length() > 0);
        assertTrue(o.asEmbedded().serialize().contains(link.toString()));
    }

    @Test
    public void testExpand() throws JsonProcessingException, URISyntaxException {
        Order o = new Order(1, 1, "USD", "status", new Basket(2), new Customer(3));

        val builder = o.representationBuilder();
        assertTrue(builder.build().getLinks().containsKey("basket"));
        builder.expand("basket");
        assertFalse(builder.build().getLinks().containsKey("basket"));
        assertTrue(o.asEmbedded("basket").getEmbedded().containsKey("basket"));
        System.out.println(o.asEmbedded("basket").serialize());
        
        builder.expand("basket");

        try{
            builder.expand("shoe");
            fail();
        }catch (NoSuchElementException e){}

        builder.addLink("cars", new URI("/cars"));

        try{
            builder.expand("cars");
            fail();
        }catch (CannotEmbedLinkException e){}
    }

    @Test
    public void testNulls() throws Exception{
        val basket1 = new Basket(98712);
        val cust2 = new Customer(12369);

        val order1 = new Order(123, 30.0, null, "shipped", basket1, null);
        val order2 = new Order(124, 20, "USD", "processing", null, cust2);

        val mapper = new ObjectMapper();

        val orderz = HalRepresentation.paginated(
                "orders", "/orders", list(order1, order2).stream(), 0, 2)
                .addProperty("currentlyProcessing", 14)
                .addProperty("shippedToday", 20)
                .build();

        System.out.println(orderz.serialize());
    }

    @Test
    public void testIgnoreNullProp(){
        HalRepresentation rep = HalRepresentation.builder()
                .ignoreNullProperties(true)
                .addProperty("thing", "value")
                .addProperty("nullthing", null)
                .build();
        assertFalse(rep.getProperties().containsKey("nullthing"));
        assertTrue(rep.getProperties().containsKey("thing"));

        HalRepresentation rep2 = HalRepresentation.builder()
                .addProperty("thing", "value")
                .addProperty("nullthing", null)
                .build();
        assertTrue(rep2.getProperties().containsKey("nullthing"));
        assertTrue(rep2.getProperties().containsKey("thing"));
    }


    @Test
    public void testSerialize() throws Exception {
        val basket1 = new Basket(98712);
        val basket2 = new Basket(97213);
        val cust1 = new Customer(7809);
        val cust2 = new Customer(12369);

        val order1 = new Order(123, 30.0, "USD", "shipped", basket1, cust1);
        val order2 = new Order(124, 20, "USD", "processing", basket2, cust2);

        val mapper = new ObjectMapper();

        val orderz = HalRepresentation.paginated(
                "orders", "/orders", list(order1, order2).stream(), 0, 2)
                .addProperty("currentlyProcessing", 14)
                .addProperty("shippedToday", 20)
                .build();

        val mine = (ObjectNode) mapper.readTree(orderz.serialize());
        val theirs = (ObjectNode) mapper.readTree(RFC);

        assertEquals(theirs, mine);
    }

    @Test
    public void testNoLinks() throws IOException {
        val basket1 = new Basket(98712);
        val cust1 = new Customer(7809);

        val order1 = new Order(123, 30.0, "USD", "shipped", basket1, cust1);

        HalRepresentation rep = HalRepresentation.builder()
                .addEmbedded("order1", order1)
                .addEmbedded("order1", asList(order1))
                .addProperty("prop", 5)
                .build();

        assertFalse(new ObjectMapper().readTree(rep.serialize()).has("_links"));
    }

    @Test
    public void testNoEmbed() throws Exception {
        val basket1 = new Basket(98712);
        val cust1 = new Customer(7809);

        val order1 = new Order(123, 30.0, "USD", "shipped", basket1, cust1);

        HalRepresentation rep = HalRepresentation.builder()
                .addLink("order1", order1)
                .addLink("order1", asList(order1))
                .addProperty("prop", 5)
                .build();

        assertFalse(new ObjectMapper().readTree(rep.serialize()).has("_embedded"));
    }

    @Test
    public void testAddMulti(){
        List<Order> orders = new LinkedList<>();
        Order straggler = new Order(999, 999, "adf", "asdf", new Basket(999), new Customer(999));
        Order straggler2 = new Order(998, 998, "adf", "asdf", new Basket(998), new Customer(998));

        IntStream.range(0, 100)
                .forEach(i ->
                                orders.add(new Order(i, i, "USD", "status",
                                        new Basket(i), new Customer(i)))
                );

        val rep = HalRepresentation.builder()
                .addEmbedded("orders", straggler)
                .addEmbedded("orders", orders)
                .addEmbedded("orders", straggler2)
                .addLink("orders", straggler)
                .addLink("orders", orders)
                .addLink("orders", straggler2)
                .build();

        assertEquals(orders.size() +2, rep.getMultiEmbedded().get("orders").size());
        assertEquals(orders.size() + 2, rep.getMultiLinks().get("orders").size());
    }

    @Test
    public void testNoProperties() throws Exception{
        val basket1 = new Basket(98712);
        val cust1 = new Customer(7809);

        val order1 = new Order(123, 30.0, "USD", "shipped", basket1, cust1);

        HalRepresentation rep = HalRepresentation.builder()
                .addLink("order1", order1)
                .addLink("order1", asList(order1))
                .addEmbedded("order2", order1)
                .build();
        val node = new JSONObject(rep.serialize());
        assertEquals(2, node.keySet().size());
        assertTrue(node.has("_embedded"));
        assertTrue(node.has("_links"));
    }

}