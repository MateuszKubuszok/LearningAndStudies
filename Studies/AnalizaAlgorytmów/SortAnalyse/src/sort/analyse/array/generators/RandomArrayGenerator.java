package sort.analyse.array.generators;

import java.util.Random;

public class RandomArrayGenerator {
	private static final Random random = new Random();
	
	public Double[] doubleArray(Double[] array) {
		for (int i = 0; i < array.length; i++)
			array[i] = random.nextDouble();
		
		return array;
	}
	
	public Float[] floatArray(Float[] array) {
		for (int i = 0; i < array.length; i++)
			array[i] = random.nextFloat();
		
		return array;
	}
	
	public Integer[] integerArray(Integer[] array) {
		for (int i = 0; i < array.length; i++)
			array[i] = random.nextInt(array.length);
		
		return array;
	}
	
	public Long[] longArray(Long[] array) {
		for (int i = 0; i < array.length; i++)
			array[i] = random.nextLong();
		
		return array;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Number> T[] array(Class<T> clazz, T[] array) {
		if (Double.class.equals(clazz))
			return (T[]) doubleArray((Double[]) array);
		if (Float.class.equals(clazz))
			return (T[]) floatArray((Float[]) array);
		if (Integer.class.equals(clazz))
			return (T[]) integerArray((Integer[]) array);
		if (Long.class.equals(clazz))
			return (T[]) longArray((Long[]) array);
		throw new UnsupportedOperationException();
	}
}
