package black.door.hate;

/**
 * Created by nfischer on 1/31/2016.
 */
public class CannotEmbedLinkException extends RuntimeException {
	public CannotEmbedLinkException(String fieldName){
		super(fieldName + " is a link and cannot be embedded as a resource.");
	}
}
