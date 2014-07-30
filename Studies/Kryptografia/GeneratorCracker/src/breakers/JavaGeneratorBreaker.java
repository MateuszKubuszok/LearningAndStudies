package breakers;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import generators.JavaGenerator;

public class JavaGeneratorBreaker extends GeneratorBreaker {
	private static final long multiplier = 0x5DEECE66DL;
    private static final long addend = 0xBL;
    private static final long mask = (1L << 48) - 1;
    
    private long seed;
	
	public JavaGeneratorBreaker(JavaGenerator generator, int predictions) {
		super(generator, predictions);
	}

	public JavaGeneratorBreaker(int[] series) {
		super(series);
	}

	@Override
	protected void analyse(int[] series) {
		Set<Long> possibleValues = new TreeSet<Long>();
		
		long checked = ((long) series[0]) << 16;
		for (long ending = 0; ending < (1<<16)-1; ending++) {
			long checkedWithEnding = checked + ending; 
			if (calculateNextInt(checkedWithEnding) == series[1])
				possibleValues.add(checkedWithEnding);
		}
		
		for (int i = 1; i < series.length; i++) {
			Set<Long> possibleNextValues = new TreeSet<Long>();
			for (long possibleValue : possibleValues) {
				if (calculateNextInt(possibleValue) == series[i])
					possibleNextValues.add(calculateNextLong(possibleValue));
			}
			possibleValues = possibleNextValues;
		}
		
		if (possibleValues.size() != 1)
			throw new IllegalArgumentException("Wrong input!");
		
		seed = (long) possibleValues.toArray()[0];
	}
	
	private int calculateNextInt(long checked) {
		return (int) (calculateNextLong(checked) >>> 16);
	}
	
	private long calculateNextLong(long checked) {
		long oldseed, nextseed;
        AtomicLong seed = new AtomicLong(checked);
        do {
            oldseed = seed.get();
            nextseed = (oldseed * multiplier + addend) & mask;
        } while (!seed.compareAndSet(oldseed, nextseed));
		return nextseed;
	}

	@Override
	public int[] predictNext() {
		long next = calculateNextLong(seed);
		int result = calculateNextInt(seed);
		seed = next;
		return new int[] {result};
	}
	
	public static void main(String[] arg) {
		JavaGenerator generator = new JavaGenerator();
		JavaGeneratorBreaker breaker = new JavaGeneratorBreaker(generator, 10);
		GeneratorBreaker.test(generator, breaker, 30);
	}
}
