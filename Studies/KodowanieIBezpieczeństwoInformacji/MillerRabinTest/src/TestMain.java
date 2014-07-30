import java.math.BigInteger;
import java.util.Random;

public class TestMain 
{
 
    public static final BigInteger BIG_TWO = BigInteger.valueOf(2);
    public static Random rnd = new Random();

    private static boolean doMillerRabinPrimeTest(BigInteger Number,BigInteger a)
    {
        if (Number.equals(BigInteger.valueOf(2)) || Number.equals(BigInteger.ZERO))
        return true;             
        if (Number.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO) || Number.equals(BigInteger.ONE))
        return true;

        BigInteger NumberMinusOne = Number.subtract(BigInteger.ONE);

        //elementy z a^(k*2^i) mod Number == Number-1.
        BigInteger k;
        BigInteger r;
        BigInteger i;
        BigInteger twoPowI;

        k = NumberMinusOne.divide(BIG_TWO);
        r = BigInteger.ONE;

        while(k.mod(BIG_TWO).equals(BigInteger.ZERO))
        {
        k=k.divide(BIG_TWO);
        r=r.add(BigInteger.ONE);
        }
        //System.out.println(k + " asd " + r);
        //System.out.println(a);

        if(a.modPow(k,Number).equals(BigInteger.ONE))
        return true;

        i=BigInteger.ZERO;
        twoPowI=BIG_TWO.pow(0); 
        while(i.compareTo(r.subtract(BigInteger.ONE)) < 1)
        {
        if(a.modPow(k.multiply(twoPowI),Number).equals(NumberMinusOne))
        return true;

        i=i.add(BigInteger.ONE);
        twoPowI=twoPowI.multiply(BIG_TWO);
        }
        return false;
    }


    public static boolean PrimeCheck(BigInteger Number)
    {
        if (Number.equals(BigInteger.valueOf(2)) || Number.equals(BigInteger.ZERO))
        return true;
                
        if (Number.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO) || Number.equals(BigInteger.ONE))
        return false;
        
        int k = 3;
        BigInteger temp = Number.subtract(BIG_TWO);

        BigInteger b;
        for (int repeat = 0; repeat < k; repeat++) 
            {

            do
            {
                b = new BigInteger(temp.bitLength(),rnd);
                b = b.mod(Number); 
                //System.out.println(b);
            } while(b.equals(BigInteger.ZERO));

                if (!doMillerRabinPrimeTest(Number,b))
                return false;
            }
        return true;
    }
    
    public static void main(String[] args)
    {
        if (args.length > 0)
        	for (String arg : args) {
        		try {
        			int i = Integer.parseInt (arg);
        			
        			if (PrimeCheck(BigInteger.valueOf (i)))
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