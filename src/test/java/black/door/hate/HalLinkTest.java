package black.door.hate;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.*;

/**
 * Created by nfischer on 6/16/2016.
 */
public class HalLinkTest {

	@Test
	public void testSerialization(){
		val link = HalLink.builder()
				.href(UriTemplate.fromTemplate("/~{username}"))
				.build();

		assertTrue(new ObjectMapper().valueToTree(link).get("templated").asBoolean());
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