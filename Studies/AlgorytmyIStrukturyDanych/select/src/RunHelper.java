import java.util.Random;

public class RunHelper {
	public static int I;
	
	public static int[] Array;
	
	public static void makeDescending (int[] array) {
        for (int i = 0; i < array.length; i++)
            array [i] = array.length-i-1;
    }

    public static void makeRandom (int[] array) {
        int i, j;
        boolean ValueAppeared;
        Random Rand = new Random();

        for (i = 0; i < array.length; i++)
        	array [i] = Rand.nextInt (array.length);
    }
    
    public static void makeRandomDiff (int[] array) {
        int i, j;
        boolean ValueAppeared;
        Random Rand = new Random();

        for (i = 0; i < array.length; i++) {
            do {
                array [i] = Rand.nextInt (array.length);
                ValueAppeared = false;
                for (j = i-1; j >= 0; j--)
                    if (array [i] == array [j])
                        ValueAppeared = true;
            } while (ValueAppeared);
        }
    }
    
    public static void parseArgs (String[] args) {
    	if (args.length < 2)
    		throw new IllegalArgumentException ("Program must obtain at least 2 parameters: [I-th statistic] [array size]");
    	
    	try {
    		RunHelper.I = Integer.parseInt (args [0]);
    		if (RunHelper.I < 1)
    			throw new Exception ();
    	} catch (Exception ex) {
    		throw new IllegalArgumentException ("First parameter must be I-th statistic (positive integer)");
    	}
    
    	if (args.length == 2) {
    		int ArraySize;
    		try {
    			ArraySize = Integer.parseInt (args [1]);
    			if (ArraySize == 0)
    				throw new Exception ();
    		} catch (Exception ex) {
    	    	throw new IllegalArgumentException ("Second parameter must be array size (positive for random, negative for descending)");
    	    }
    		if (ArraySize > 0) {
    			RunHelper.Array = new int[ArraySize];
    			RunHelper.makeRandom (RunHelper.Array);
    		} else {
    			RunHelper.Array = new int[ArraySize*(-1)];
    			RunHelper.makeRandomDiff (RunHelper.Array);
    		}
    	} else {
    		RunHelper.Array = new int[args.length-1];
    		for (int i = 0; i < args.length-1; i++) {
    			try {
    				RunHelper.Array [i] = Integer.parseInt (args [i+1]);
        		} catch (Exception ex) {
        	    	throw new IllegalArgumentException (args [i] + " is not a integer!");
        	    }
        		if (RunHelper.Array [i] < 0)
        			RunHelper.Array [i] *= -1;
    		}
    	}
    	
    	if (RunHelper.I < 1 || RunHelper.I > RunHelper.Array.length)
    		throw new IllegalArgumentException ("I-th statistic ("+RunHelper.I+") is out of array range ["+(RunHelper.Array.length > 0 ? 1 : 0)+","+RunHelper.Array.length+"]!");
    }
	
	public static void waitFor (int ms) {
		try {
			Thread.sleep (ms);
		} catch(InterruptedException ie) {}
	}
}
