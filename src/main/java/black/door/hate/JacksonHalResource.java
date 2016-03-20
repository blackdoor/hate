package black.door.hate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

import static black.door.hate.HalRepresentation.HalRepresentationBuilder;

/**
 * Created by nfischer on 3/19/2016.
 */
public interface JacksonHalResource extends HalResource {

	default HalRepresentation asEmbedded(String... expand){
		return asEmbedded(defaultMapper(), expand);
	}

	default HalRepresentation asEmbedded(ObjectMapper mapper, String... expand){
		HalRepresentationBuilder builder = representationBuilder(mapper);
		for(String e : expand){
			builder.expand(e);
		}
		return builder.build();
	}

	/**
	 * implement this method
	 * @param mapper
	 * @return
	 */
	default HalRepresentationBuilder representationBuilder(ObjectMapper mapper){
		val builder = HalRepresentation.builder()
				.addProperties(mapper.valueToTree(this));

		val location = this.location();
		if(location != null){
			builder.addLink("self", location);
		}

		return builder;
	}

	/**
	 * don't implement this method
	 * @return
	 */
	default HalRepresentationBuilder representationBuilder(){
		return representationBuilder(defaultMapper());
	}

	default ObjectMapper defaultMapper(){
		return HalRepresentation.getMapper();
	}
}
