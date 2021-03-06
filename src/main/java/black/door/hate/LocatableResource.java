package black.door.hate;

import java.net.URI;

import static black.door.hate.HalLink.HalLinkBuilder;

/**
 * Created by nfischer on 12/8/2015.
 */
public interface LocatableResource extends LinkOrResource{
	URI location();

	default HalLink asLink(){
		return linkBuilder().build();
	}

	default HalLinkBuilder linkBuilder(){
		return HalLink.builder()
				.href(this.location());
	}
}
