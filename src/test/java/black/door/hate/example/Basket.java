package black.door.hate.example;

import black.door.hate.HalRepresentation;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by nfischer on 12/8/2015.
 */
public class Basket extends Thing {

	public Basket(int id){
		super(id);
	}

	@Override
	protected String resName() {
		return "baskets";
	}

	@Override
	public HalRepresentation asEmbedded() {
		throw new NotImplementedException();
	}
}
