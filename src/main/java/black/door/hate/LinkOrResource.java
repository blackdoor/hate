package black.door.hate;

import java.util.Optional;

/**
 * Created by nfischer on 1/31/2016.
 */
public interface LinkOrResource {

	HalLink asLink();

	default Optional<HalResource> asResource(){
		if(this instanceof HalResource){
			return Optional.of((HalResource) this);
		}else
			return Optional.empty();
	}
}
