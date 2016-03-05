package black.door.hate.example;

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

}
