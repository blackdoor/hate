package black.door.hate;

/**
 * Created by nfischer on 12/8/2015.
 */
public interface HalResource extends LocatableResource {
	default HalLink asLink(){
		return HalLink.builder()
				.href(this.location())
				.build();
	}

	HalRepresentation asEmbedded();
}
