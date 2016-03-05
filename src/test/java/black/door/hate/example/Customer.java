package black.door.hate.example;

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

}
