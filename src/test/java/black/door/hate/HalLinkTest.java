package black.door.hate;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.Test;

import java.net.URI;
import java.net.URL;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by nfischer on 6/16/2016.
 */
public class HalLinkTest {

	private ObjectMapper mapper = new ObjectMapper()
			.findAndRegisterModules();

	@Test
	public void testSerialization() throws Exception {
		val link = HalLink.builder()
				.href(UriTemplate.fromTemplate("/~{username}"))
				.build();

		JsonNode node = mapper.valueToTree(link);
		assertTrue(node.get("templated").asBoolean());
		assertFalse(node.has("name"));
		System.out.println(mapper.writeValueAsString(link));

		val link2 = HalLink.builder()
				.href(URI.create("/path"))
				.deprecation(new URL("https://google.com"))
				.hreflang("eng")
				.name("link")
				.profile(URI.create("thing.black"))
				.title("title")
				.build();

		System.out.println(mapper.writeValueAsString(link2));
	}

	@Test
	public void testIsTemplated(){
		HalLink link = HalLink.builder()
				.href(URI.create("/path"))
				.build();
		assertFalse(link.isTemplated());

		link = HalLink.builder()
				.href(UriTemplate.fromTemplate("/~{username}"))
				.build();
		assertTrue(link.isTemplated());
	}

	@Test
	public void testGetHrefAsUri() throws Exception {
		HalLink link = HalLink.builder()
				.href(URI.create("/path"))
				.build();
		assertTrue(link.getHrefAsUri().isPresent());

		link = HalLink.builder()
				.href(UriTemplate.fromTemplate("/~{username}"))
				.build();
		assertFalse(link.getHrefAsUri().isPresent());
	}

	@Test
	public void testGetHrefAsTemplate() throws Exception {
		HalLink link = HalLink.builder()
				.href(UriTemplate.fromTemplate("/~{username}"))
				.build();
		assertTrue(link.getHrefAsTemplate().isPresent());

		link = HalLink.builder()
				.href(URI.create("/path"))
				.build();
		assertFalse(link.getHrefAsTemplate().isPresent());
	}
}