import java.math.BigInteger;
import java.util.Random;

public class FermatTest {
    private final static Random rand = new Random();

    private static BigInteger RandomFermatBase(BigInteger n) {
        while (true) {
            final BigInteger a = new BigInteger (n.bitLength(), rand);
            if (BigInteger.ONE.compareTo(a) <= 0 && a.compareTo(n) < 0)
                return a;
        }
    }

    public static boolean checkPrime(BigInteger n, int maxIterations) {
        if (n.equals(BigInteger.ONE))
            return false;

        for (int i = 0; i < maxIterations; i++) {
            BigInteger a = RandomFermatBase(n);
            a = a.modPow(n.subtract(BigInteger.ONE), n);

            if (!a.equals(BigInteger.ONE))
                return false;
        }

        return true;
    }

    public static void main(String[] args) {
        if (args.length > 0)
        	for (String arg : args) {
        		try {
        			int i = Integer.parseInt (arg);
        			
        			if (checkPrime(BigInteger.valueOf (i),3))
        				System.out.println("Liczba " + i + " jest prawdopodobnie pierwsza.");
        			else
        				System.out.println("Liczba " + i + " nie jest pierwsza.");        			
        		} catch (Exception e) {
        			System.out.println (arg + " nie jest liczb¹ ca³kowit¹.");
        			continue;
        		}
        	}
    }
}