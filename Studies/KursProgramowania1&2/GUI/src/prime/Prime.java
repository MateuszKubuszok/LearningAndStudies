package prime;

public class Prime {
	public static boolean prime (int n) {
		if (n < 2)
			return false;
		
		if (n == 2)
			return true;
		
		int Max = (int) Math.sqrt ((double) n) + 1;
		
		for (int i = 2; i <= Max; i++)
			if ((n % i) == 0 && n != i)
				return false;
		
		return true;
	}
}
