package black.door.hate;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.*;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HalLink implements LinkOrResource{

	private @NonNull final String href;

	@Getter(AccessLevel.NONE) @JsonProperty
	private final Boolean templated;

	private final String type;
	private final URL deprecation;
	private final String name;
	private final URI profile;
	private final String title;
	private final String hreflang;

	@java.beans.ConstructorProperties({"href", "templated", "type", "deprecation", "name", "profile", "title", "hreflang"})
	HalLink(String href, Boolean templated, String type, URL deprecation, String name, URI profile, String title, String hreflang) {
		this.href = href;
		this.templated = templated;
		this.type = type;
		this.deprecation = deprecation;
		this.name = name;
		this.profile = profile;
		this.title = title;
		this.hreflang = hreflang;
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
}
