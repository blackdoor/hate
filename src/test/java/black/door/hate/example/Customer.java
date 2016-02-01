package black.door.hate.example;

import black.door.hate.HalRepresentation;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by nfischer on 12/8/2015.
 */
public class Customer extends Thing {

	public Customer(int i){
		super(i);
	}

	@Override
	protected String resName() {
		return "customers";
	}

	@Override
	public HalRepresentation.HalRepresentationBuilder representationBuilder() {
		throw new NotImplementedException();
	}
}
