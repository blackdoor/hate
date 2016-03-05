package black.door.hate;

/**
 * Created by nfischer on 12/8/2015.
 */
public interface HalResource extends LocatableResource, LinkOrResource {

	default HalRepresentation asEmbedded(String... expand){
		HalRepresentation.HalRepresentationBuilder builder = representationBuilder();
		for(String e : expand){
			builder.expand(e);	
		}
		return builder.build();
	}

	default HalRepresentation.HalRepresentationBuilder representationBuilder(){
		return HalRepresentation.builder()
				.addLink("self", this)
				.addProperties(HalRepresentation.getMapper().valueToTree(this));
	}

}
