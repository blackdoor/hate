package black.door.hate;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by nfischer on 12/8/2015.
 */
@Getter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonSerialize(using = HalLink.HalLinkSerializer.class)
public class HalLink implements LinkOrResource{

	private @NonNull final String href;

	@Getter(AccessLevel.NONE) @JsonProperty
	private final Boolean templated;
	private final Optional<String> type;
	private final Optional<URL> deprecation;
	private final Optional<String> name;
	private final Optional<URI> profile;
	private final Optional<String> title;
	private final Optional<String> hreflang;

	@java.beans.ConstructorProperties({"href", "templated", "type", "deprecation", "name", "profile", "title", "hreflang"})
	HalLink(String href, Boolean templated, String type, URL deprecation, String name, URI profile, String title, String hreflang) {
		this.href = href;
		this.templated = templated;
		this.type = Optional.ofNullable(type);
		this.deprecation = Optional.ofNullable(deprecation);
		this.name = Optional.ofNullable(name);
		this.profile = Optional.ofNullable(profile);
		this.title = Optional.ofNullable(title);
		this.hreflang = Optional.ofNullable(hreflang);
	}

	public static HalLinkBuilder builder() {
		return new HalLinkBuilder();
	}

	@JsonIgnore
	@SneakyThrows(URISyntaxException.class)
	public Optional<URI> getHrefAsUri(){
		if(!isTemplated())
			return Optional.of(new URI(href));
		return Optional.empty();
	}

	@JsonIgnore
	public Optional<UriTemplate> getHrefAsTemplate(){
		if(isTemplated())
			return Optional.of(UriTemplate.fromTemplate(href));
		return Optional.empty();
	}

	@JsonIgnore
	public boolean isTemplated(){
		return templated == null ? false : templated;
	}

	@Override
	public HalLink asLink() {
		return this;
	}

	@Override
	@SneakyThrows(JsonProcessingException.class)
	public String toString(){
		return HalRepresentation.getWriter().writeValueAsString(this);
	}

	@ToString
	@EqualsAndHashCode
	public static class HalLinkBuilder {
		private String href;
		private Boolean templated;
		private String type;
		private URL deprecation;
		private String name;
		private URI profile;
		private String title;
		private String hreflang;

		HalLinkBuilder() {
		}

		public HalLink.HalLinkBuilder href(UriTemplate href) {
			this.href = href.expandPartial();
			templated = true;
			return this;
		}

		public HalLink.HalLinkBuilder href(URI href) {
			this.href = href.toASCIIString();
			if(Objects.equals(templated, true))
				templated = null;
			return this;
		}

		public HalLink.HalLinkBuilder type(String type) {
			this.type = type;
			return this;
		}

		public HalLink.HalLinkBuilder deprecation(URL deprecation) {
			this.deprecation = deprecation;
			return this;
		}

		public HalLink.HalLinkBuilder name(String name) {
			this.name = name;
			return this;
		}

		public HalLink.HalLinkBuilder profile(URI profile) {
			this.profile = profile;
			return this;
		}

		public HalLink.HalLinkBuilder title(String title) {
			this.title = title;
			return this;
		}

		public HalLink.HalLinkBuilder hreflang(String hreflang) {
			this.hreflang = hreflang;
			return this;
		}

		public HalLink build() {
			return new HalLink(href, templated, type, deprecation, name, profile, title, hreflang);
		}

	}

	public static class HalLinkSerializer extends StdSerializer<HalLink> {

		protected HalLinkSerializer() {
			this(HalLink.class);
		}

		protected HalLinkSerializer(Class<HalLink> clazz) {
			super(clazz);
		}

		@Override
		public void serialize(HalLink value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			gen.writeStartObject();

			provider.defaultSerializeField("href", value.href, gen);
			if(value.templated != null)
				provider.defaultSerializeField("templated", value.templated, gen);
			if(value.type.isPresent())
				provider.defaultSerializeField("type", value.type, gen);
			if(value.deprecation.isPresent())
				provider.defaultSerializeField("deprecation", value.deprecation, gen);
			if(value.name.isPresent())
				provider.defaultSerializeField("name", value.name, gen);
			if(value.profile.isPresent())
				provider.defaultSerializeField("profile", value.profile, gen);
			if(value.title.isPresent())
				provider.defaultSerializeField("title", value.title, gen);
			if(value.hreflang.isPresent())
				provider.defaultSerializeField("hreflang", value.hreflang, gen);

			gen.writeEndObject();
		}
	}
}
