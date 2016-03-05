package black.door.hate.example;

import black.door.hate.HalResource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by nfischer on 12/8/2015.
 */
@AllArgsConstructor
@Getter
public abstract class Thing implements HalResource {
	protected long id;

	protected abstract String resName();

	@SneakyThrows(URISyntaxException.class)
	public URI location(){
		return new URI('/' + resName() +"/" + id);
	}

}
