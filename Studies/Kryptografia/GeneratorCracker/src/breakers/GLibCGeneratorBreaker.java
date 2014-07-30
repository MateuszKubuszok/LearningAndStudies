package breakers;

import java.util.ArrayList;


import generators.GLibCGenerator;

public class GLibCGeneratorBreaker extends GeneratorBreaker {
	private ArrayList<Integer> series;
	
	public GLibCGeneratorBreaker(GLibCGenerator generator, int predictions) {
		super(generator, predictions+344);
	}

	public GLibCGeneratorBreaker(int[] series) {
		super(series);
	}

	@Override
	protected void analyse(int[] series) {
		this.series = new ArrayList<Integer>();
		for (int i = 0; i < series.length; i++)
			this.series.add(series[i]);
	}
	
	@Override
	public int[] predictNext() {
		int lastIndex = series.size();
		int p1 = (int) ((series.get(lastIndex-31) + series.get(lastIndex-3)) % 2147483648L);
		int p2 = (int) ((series.get(lastIndex-31) + series.get(lastIndex-3) + 1) % 2147483648L);
		series.add(p1);
		return new int[] { p1, p2 };
	}
	
	public static void main(String[] arg) {
		GLibCGenerator generator = new GLibCGenerator();
		GLibCGeneratorBreaker breaker = new GLibCGeneratorBreaker(generator, 10);
		GeneratorBreaker.test(generator, breaker, 10);
	}
}
