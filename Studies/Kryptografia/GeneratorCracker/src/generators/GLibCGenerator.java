package generators;

import java.util.ArrayList;
import java.util.List;

public class GLibCGenerator implements Generator {
	private List<Integer> r = new ArrayList<Integer>();
	
	public GLibCGenerator(int seed) {	
		r.add(seed);
		for (int i = 1; i < 31; i++) {
			int tmp = (int) ((16807L * r.get(i-1)) % 2147483647);
			if (tmp < 0)
				tmp += 2147483647;
			r.add(tmp);
		}
		for (int i = 31; i < 34; i++)
			r.add(r.get(i-31));
		for (int i = 34; i < 344; i++)
			r.add(r.get(i-31) + r.get(i-3));
	}
	
	public GLibCGenerator() {
		this(1);
	}
	
	public int getNext() {
		int i = r.size();
		int tmp = r.get(i-31) + r.get(i-3);
		r.add(tmp);
		long tmp2 = ((long) tmp) +
				(tmp >= 0
				? 0L
				: 2*2147483648L);
		return (int) (tmp2 >> 1);
	}
	
	public static void main(String[] args) {
		GLibCGenerator generator = new GLibCGenerator();
		for (int i = 0; i < 47; i++)
			System.out.println(generator.getNext());
	}
}
