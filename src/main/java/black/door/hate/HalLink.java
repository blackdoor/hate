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
	URI href;

	Boolean templated;
	String type;
	URL deprecation;
	String name;
	URI profile;
	String title;
	String hreflang;
}
