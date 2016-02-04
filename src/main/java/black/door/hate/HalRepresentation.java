package black.door.hate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Stream;

import static black.door.hate.Constants._embedded;
import static black.door.hate.Constants._links;
import static black.door.util.Misc.require;
import static java.util.Map.Entry;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Created by nfischer on 12/8/2015.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonSerialize(using = HalRepresentation.HalRepresentationSerializer.class)
public class HalRepresentation implements java.io.Serializable {
	private static final ObjectWriter WRITER;
	static {
		ObjectMapper mapper = new ObjectMapper();
		WRITER = mapper.writer();
	}

	private final Map<String, LinkOrResource> links;
	private final Map<String, List<LinkOrResource>> multiLinks;
	private final Map<String, HalResource> embedded;
	private final Map<String, List<HalResource>> multiEmbedded;
	private final Map<String, Object> properties;

	HalRepresentation(
			Map<String, LinkOrResource> links,
			Map<String, List<LinkOrResource>> multiLinks,
			Map<String, HalResource> embedded,
			Map<String, List<HalResource>> multiEmbedded,
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

	@SneakyThrows(JsonProcessingException.class)
	public String toString(){
		return serialize();
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
					.collect(toList()))
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
				throws IOException{
			jsonGenerator.writeStartObject();

			//write all properties to json
			for(Entry<String, Object> e :halRepresentation.properties.entrySet()){
				jsonGenerator.writeObjectField(e.getKey(), e.getValue());
			}

			//map links from LinkOrResources to HalLinks
			Map<String, HalLink> linkz = halRepresentation.getLinks().entrySet()
					.stream()
					.collect(toMap(Entry::getKey, e -> e.getValue().asLink()));
			Map<String, Collection<HalLink>> multiLinkz = halRepresentation.getMultiLinks().entrySet()
					.stream()
					.collect(toMap(Entry::getKey, e -> e.getValue()
							.stream()
							.map(LinkOrResource::asLink)
							.collect(toList())
					));

			//put all links and collections of links together in one object
			Map<String, Object> links = new HashMap<>();
			links.putAll(linkz);
			links.putAll(multiLinkz);
			if(!links.isEmpty())
				jsonGenerator.writeObjectField(_links, links);

			//map all HalResources to HalRepresentations
			Map<String, HalRepresentation> embeddz = halRepresentation.getEmbedded().entrySet()
					.stream()
					.collect(toMap(Entry::getKey, e -> e.getValue().asEmbedded()));
			Map<String, Collection<HalRepresentation>> multiEmbeddz = halRepresentation.getMultiEmbedded().entrySet()
					.stream()
					.collect(toMap(Entry::getKey, e -> e.getValue()
									.stream()
									.map(HalResource::asEmbedded)
									.collect(toList())
					));

			//put all embedded resources and collections of embedded resources into one object
			Map<String, Object> embedded = new HashMap<>();
			embedded.putAll(embeddz);
			embedded.putAll(multiEmbeddz);
			if(!embedded.isEmpty())
				jsonGenerator.writeObjectField(_embedded, embedded);

			jsonGenerator.writeEndObject();
		}
	}

	public static class HalRepresentationBuilder{
		private Map<String, LinkOrResource> links;
		private Map<String, List<LinkOrResource>> multiLinks;
		private Map<String, HalResource> embedded;
		private Map<String, List<HalResource>> multiEmbedded;
		private Map<String, Object> properties;
		private boolean ignoreNullProperties = false;
		private boolean ignoreNullResources = false;

		public HalRepresentationBuilder() {
			links = new HashMap<>();
			multiLinks = new HashMap<>();
			embedded = new HashMap<>();
			multiEmbedded = new HashMap<>();
			properties = new HashMap<>();
		}

		public void expand(String fieldName){
			if(links.containsKey(fieldName)){
				addEmbedded(fieldName, links.remove(fieldName).asResource().orElseThrow(
						() -> new CannotEmbedLinkException(fieldName)
				));
			} else if(multiLinks.containsKey(fieldName)){
				try {
					addEmbedded(fieldName, multiLinks.remove(fieldName)
							.stream()
							.map(e -> e.asResource().get())
							.collect(toList())
					);
				}catch (NoSuchElementException e){
					throw new CannotEmbedLinkException(fieldName);
				}
			} else if (!(embedded.containsKey(fieldName) || multiEmbedded.containsKey(fieldName))) {
				throw new NoSuchElementException("There is no linked or embedded resource with the field name " +fieldName);
			}
		}

		/**
		 * Causes any properties with null values added to this builder after this call to be ignored.
		 * Properties with null values added before this call will still be included.
		 * Null properties are included by default.
		 * @param active
		 * @return this builder
		 */
		public HalRepresentationBuilder ignoreNullProperties(boolean active){
			this.ignoreNullProperties = active;
			return this;
		}

		public boolean ignoreNullProperties(){
			return ignoreNullProperties;
		}

		public boolean isIgnoreNullResources(){
			return ignoreNullResources;
		}

		public HalRepresentationBuilder ignoreNullResources(boolean active){
			this.ignoreNullResources = active;
			return this;
		}

		public HalRepresentationBuilder addProperty(String name, Object prop){
			if(!ignoreNullProperties || prop != null)
				properties.put(name, prop);
			return this;
		}

		public HalRepresentationBuilder addProperties(JsonNode jax){
			require(jax.isObject());
			jax.fields().forEachRemaining(e -> addProperty(e.getKey(), e.getValue()));
			return this;
		}

		/**
		 *
		 * @param <T> either a HalResource or a HalLink
		 * @param name the field name
		 * @param res the resource to add to the representation
		 * @param rs
		 * @param multiRs
		 */
		private <T extends LinkOrResource> void add(String name, T res, Map<String, T> rs,
		                                            Map<String, List<T>> multiRs){

			if(res == null && ignoreNullResources)
				return;

			require(res != null , "Cannot add a null linked or embedded resource");
			if(multiRs.containsKey(name)){
				multiRs.get(name).add(res);
			}else if(rs.containsKey(name)){
				List<T> ls = new LinkedList<>();
				ls.add(rs.remove(name));
				ls.add(res);
				multiRs.put(name, ls);
			}else{
				rs.put(name, res);
			}
		}

		private <T extends LinkOrResource> void addMulti(String name, Collection<? extends T> res, Map<String, T> rs, Map<String, List<T>> multiRs){
			List<T> resource = res == null ? new LinkedList<>() : new LinkedList<>(res);
			if(rs.containsKey(name))
				resource.add(rs.get(name));
			List<T> links = multiRs.get(name);
			if(links == null) {
				multiRs.put(name, resource);
			}else{
				links.addAll(resource);
			}
		}

		public HalRepresentationBuilder addEmbedded(String name, HalResource link){
			add(name, link, embedded, multiEmbedded);
			return this;
		}

		public HalRepresentationBuilder addEmbedded(String name, Collection<? extends HalResource> link){
			addMulti(name, link, embedded, multiEmbedded);
			return this;
		}

		public HalRepresentationBuilder addLink(String name, LinkOrResource link){
			add(name, link, links, multiLinks);
			return this;
		}

		public HalRepresentationBuilder addLink(String name, URI link){
			HalLink l = HalLink.builder().href(link).build();
			return addLink(name, l);
		}

		public HalRepresentationBuilder addLink(String name, Collection<? extends LinkOrResource> link){
			addMulti(name, link, links, multiLinks);
			return this;
		}

		public HalRepresentation build(){
			return new HalRepresentation(links, multiLinks, embedded,
					multiEmbedded, properties);
		}
	}

}
