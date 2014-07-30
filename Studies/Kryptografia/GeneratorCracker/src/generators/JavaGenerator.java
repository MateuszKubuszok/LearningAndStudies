package generators;

import java.util.Random;

public class JavaGenerator implements Generator {
	private Random random = new Random();
	
	@Override
	public int getNext() {
		return random.nextInt();
	}
}
