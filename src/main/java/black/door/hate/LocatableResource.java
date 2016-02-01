package black.door.hate;

import java.net.URI;

/**
 * Created by nfischer on 12/8/2015.
 */
public interface LocatableResource extends LinkOrResource{
	URI location();

	default HalLink asLink(){
		return HalLink.builder()
				.href(this.location())
				.build();
	}
}
