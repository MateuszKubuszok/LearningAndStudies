package breakers;

import static java.lang.Math.abs;
import static java.lang.Math.signum;
import generators.Generator;

import java.math.BigInteger;

public abstract class GeneratorBreaker {
	public GeneratorBreaker(Generator generator, int predictions) {
		this(populate(generator, predictions));
	}
	
	public GeneratorBreaker(int[] series) {
		analyse(series);
	}
	
	public abstract int[] predictNext();
	
	protected abstract void analyse(int[] series);
	
	protected long[] eGCD(long a, long b) {
		long a0 = a;
		long b0 = b;
		long p = 1, q = 0;
		long r = 0, s = 1;
		
		a = abs(a);
		b = abs(b);
		
		while (b != 0) {
			long c = a % b;
			long quot = a/b;
			a = b;
			b = c;
			long new_r = p - quot * r;
			long new_s = q - quot * s;
			p = r; q = s;
			r = new_r;
			s = new_s;
		}
		
		return new long[] {
			p*abs(a0) + q*abs(b0),
			(long) (p*signum(a0)),
			(long) (q*signum(b0))
		};
	}
	
	protected int gcd(long a, long b) {
	    BigInteger b1 = BigInteger.valueOf(a);
	    BigInteger b2 = BigInteger.valueOf(b);
	    BigInteger gcd = b1.gcd(b2);
	    return gcd.intValue();
	}
	
	protected int gcd(long[] a) {
		int gcd = gcd(a[0], a[1]);
		for (int i = 2; i < a.length; i++)
			gcd(gcd, a[i]);
		return gcd;
	}
	
	private static int[] populate(Generator generator, int size) {
		int[] s = new int[size];
		for (int i = 0; i < size; i++)
			s[i] = generator.getNext();
		return s;
	}
	
	public static void test(Generator generator, GeneratorBreaker breaker, int tries) {
		int guessed = 0;
		for (int i = 0; i < tries; i++) {
			int actual = generator.getNext();
			int[] predicted = breaker.predictNext();
			
			StringBuilder builder = new StringBuilder();
			builder.append("actual:").append(actual).append("\tpredicted: ");
			for (int j = 0; j < predicted.length; j++) {
				builder.append("|").append(predicted[j]);
				if (predicted[j] == actual) {
					guessed++;
					builder.append("\t").append("guessed");
				} else {
					builder.append("\t").append("not guessed");
				}
			}
			System.out.println(builder);
		}
		System.out.println("guessed "+guessed+"/"+tries+" (" + ((double) guessed/tries) + ")");		
	}
}
