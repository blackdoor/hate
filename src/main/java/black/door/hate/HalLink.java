package black.door.hate;

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
