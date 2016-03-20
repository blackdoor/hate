package black.door.hate;

import static black.door.hate.HalRepresentation.HalRepresentationBuilder;

/**
 * Created by nfischer on 12/8/2015.
 */
public interface HalResource extends LocatableResource, LinkOrResource {

	default HalRepresentation asEmbedded(String... expand){
		HalRepresentationBuilder builder = representationBuilder();
		for(String e : expand){
			builder.expand(e);	
		}
		return builder.build();
	}

	HalRepresentationBuilder representationBuilder();

}
