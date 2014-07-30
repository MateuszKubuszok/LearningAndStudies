package bits;

import static java.lang.System.out;

import java.util.Random;

public class BitsOn {
	public static Random random = new Random();
	
	public static int zlicz(int n) {
		int l = 0;
		while (n > 0) {
			n &= n-1;
			l++;
		}
		return l;
	}
	
	public static void main(String[] args) {
		int trials = 10000;
		long sum = 0;
		
		for (int i = 0; i < trials;) {
			int n = random.nextInt();
			if (n < 0) continue;
			sum += zlicz(n);
			i++;
		}
		
		out.println("Medium time is " + ((double) sum / (double) trials));
	}
}
