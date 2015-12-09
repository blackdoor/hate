package black.door.hate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Getter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static black.door.util.Misc.require;

/**
 * Created by nfischer on 12/8/2015.
 */
@Getter
public class HalRepresentation {
	private static final ObjectWriter WRITER;
	static {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule mod = new SimpleModule();
		mod.addSerializer(HalRepresentation.class, new HalRepresentationSerializer());
		mapper.registerModule(mod);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		WRITER = mapper.writer();
	}

	private final Map<String, HalLink> links;
	private final Map<String, Collection<HalLink>> multiLinks;
	private final Map<String, HalRepresentation> embedded;
	private final Map<String, Collection<HalRepresentation>> multiEmbedded;
	private final Map<String, Object> properties;

	HalRepresentation(
			Map<String, HalLink> links,
	        Map<String, Collection<HalLink>> multiLinks,
	        Map<String, HalRepresentation> embedded,
	        Map<String, Collection<HalRepresentation>> multiEmbedded,
	        Map<String, Object> properties) {
		require(null != links);
		require(null != multiLinks);
		require(null != embedded);
		require(null != multiEmbedded);
		require(null != properties);

		this.links = links;
		this.multiLinks = multiLinks;
		this.embedded = embedded;
		this.multiEmbedded = multiEmbedded;
		this.properties = properties;
	}

	public String serialize() throws JsonProcessingException {
		return WRITER.writeValueAsString(this);
	}

	public static HalRepresentationBuilder paginated(
			String name, String self, Stream<? extends HalResource> stream,
			long pageNumber, long pageSize) throws URISyntaxException {
		long effectivePageNumber = pageNumber > 0 ? pageNumber-1 : pageNumber;
		long displayPageNumber = pageNumber <= 1 ? 1 : pageNumber;
		HalRepresentationBuilder builder = new HalRepresentationBuilder();
		builder
				.addEmbedded(name, stream
					.skip((effectivePageNumber) *pageSize)
					.limit(pageSize)
					.collect(Collectors.toList()))
				.addLink("next", new URI(self + "?page=" + (displayPageNumber + 1)))
				.addLink("self", new URI(self +
						(displayPageNumber > 1
							? "?page=" +displayPageNumber
							: "")
				));

		return builder;
	}

	public static HalRepresentationBuilder builder(){
		return new HalRepresentationBuilder();
	}

	public static class HalRepresentationSerializer
			extends JsonSerializer<HalRepresentation>{

		@Override
		public void serialize(HalRepresentation halRepresentation,
		                      JsonGenerator jsonGenerator,
		                      SerializerProvider serializerProvider)
				throws IOException, JsonProcessingException {
			jsonGenerator.writeStartObject();

			for(Map.Entry<String, Object> e :halRepresentation.properties.entrySet()){
				jsonGenerator.writeObjectField(e.getKey(), e.getValue());
			}

			Map<String, Object> links = new HashMap<>();
			links.putAll(halRepresentation.links);
			links.putAll(halRepresentation.multiLinks);
			if(!links.isEmpty())
				jsonGenerator.writeObjectField("_links", links);

			Map<String, Object> embedded = new HashMap<>();
			embedded.putAll(halRepresentation.embedded);
			embedded.putAll(halRepresentation.multiEmbedded);
			if(!embedded.isEmpty())
				jsonGenerator.writeObjectField("_embedded", embedded);

			jsonGenerator.writeEndObject();
		}
	}

	public static class HalRepresentationBuilder{
		private Map<String, HalLink> links;
		private Map<String, Collection<HalLink>> multiLinks;
		private Map<String, HalRepresentation> embedded;
		private Map<String, Collection<HalRepresentation>> multiEmbedded;
		private Map<String, Object> properties;

		public HalRepresentationBuilder() {
			links = new HashMap<>();
			multiLinks = new HashMap<>();
			embedded = new HashMap<>();
			multiEmbedded = new HashMap<>();
			properties = new HashMap<>();
		}

		public HalRepresentationBuilder addProperty(String name, Object prop){
			properties.put(name, prop);
			return this;
		}

		private <T> void add(String name, HalResource res, Map<String, T> rs,
		                     Map<String, Collection<T>> multiRs,
		                     Function<HalResource, T> trans){
			if(multiRs.containsKey(name)){
				multiRs.get(name).add(trans.apply(res));
			}else if(rs.containsKey(name)){
				List<T> ls = new LinkedList<>();
				ls.add(rs.remove(name));
				ls.add(trans.apply(res));
				multiRs.put(name, ls);
			}else{
				rs.put(name, trans.apply(res));
			}
		}

		private <T> void addMulti(String name, Collection<? extends HalResource> res,
		                          Map<String, Collection<T>> multiRs,
		                          Function<HalResource, T> trans){
			Collection<T> links = multiRs.get(name);
			Collection<T> ls = res.stream()
					.map(trans)
					.collect(Collectors.toList());
			if(links == null) {
				multiRs.put(name, ls);
			}else{
				links.addAll(ls);
			}
		}

		public HalRepresentationBuilder addEmbedded(String name, HalResource link){
			add(name, link, embedded, multiEmbedded, HalResource::asEmbedded);
			return this;
		}

		public HalRepresentationBuilder addEmbedded(String name, Collection<? extends HalResource> link){
			addMulti(name, link, multiEmbedded, HalResource::asEmbedded);
			return this;
		}

		public HalRepresentationBuilder addLink(String name, HalResource link){
			add(name, link, links, multiLinks, HalResource::asLink);
			return this;
		}

		public HalRepresentationBuilder addLink(String name, HalLink link){
				if(multiLinks.containsKey(name)){
					multiLinks.get(name).add(link);
				}else if(links.containsKey(name)){
					List<HalLink> ls = new LinkedList<>();
					ls.add(links.remove(name));
					ls.add(link);
					multiLinks.put(name, ls);
				}else{
					links.put(name, link);
				}
			return this;
		}

		public HalRepresentationBuilder addLink(String name, URI link){
			HalLink l = HalLink.builder().href(link).build();
			return addLink(name, l);
		}

		public HalRepresentationBuilder addLink(String name, Collection<? extends HalResource> link){
			addMulti(name, link, multiLinks, HalResource::asLink);
			return this;
		}

		public HalRepresentation build(){
			return new HalRepresentation(links, multiLinks, embedded,
					multiEmbedded, properties);
		}
	}

}
