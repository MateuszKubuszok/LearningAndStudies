package test;

import cache.CacheSource;

public class StringSource implements CacheSource<Integer,String>{
	public String getElement (Integer k) {
		int i = Math.abs (k.intValue ());
		int j = 0;
		String result = "";
		
		while (result.length () < i)
			result = result + ((j++)%10);
		
		return result;
	}
}
