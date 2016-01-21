package black.door.hate;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.net.URI;
import java.net.URL;

/**
 * Created by nfischer on 12/8/2015.
 */
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HalLink {
	@NonNull
	private URI href;

	private Boolean templated;
	private String type;
	private URL deprecation;
	private String name;
	private URI profile;
	private String title;
	private String hreflang;
}
