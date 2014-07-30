package generators;

public class LinearCongruentialGenerator implements Generator {
	private final long a;
	private final long k;
	private final long m;
	private long x;
	
	public LinearCongruentialGenerator(long a, long k, long m, long seed) {
		this.a = a;
		this.k = k;
		this.m = m;
		this.x = seed;
	}
	
	public int getNext() {
		x = (a*x + k) % m;
		if (x < 0)
			x += m;
		return (int) x;
	}
}
