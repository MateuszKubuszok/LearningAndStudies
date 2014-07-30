package main;

import static java.lang.System.out;
import static s.box.SBox.sBox5;

import java.util.Random;

public class SBoxMain {
	private static final Random random = new Random();
	
	private static final int xzRange = 1 << 6;
	private static final int yRange = 1 << 4;
	private static final int tries = xzRange * xzRange;
	
	public static void main(String[] args) {
		int allHits = 0;
		for (int y = 0; y < yRange; y++) {
			int hits = 0;
			for (int x = 0; x < xzRange; x++) {
				for (int try_ = 0; try_ < tries; try_++) {
					int z = random.nextInt(xzRange);
					if ((sBox5(z) ^ sBox5(z ^ x)) == y)
						hits++;
				}
			}
			allHits += hits;
			out.println("P[S5(z) xor S5(z xor x) = " + y +  "] = " + ((double) hits/(xzRange * tries)));
		}
		out.println((double) allHits/(yRange * xzRange * tries));
	}
}