class Solution {
  public int binary_period ( int N ) {
    int lengthN = (int) Math.ceil(Math.log(N)/Math.log(2));
    
    System.out.println (lengthN+"tested: "+Integer.toBinaryString(N));
    for (int i = 1; i <= lengthN/2; i++) {
      int shift = lengthN % i;
      int head = N >>> shift;
      int tail = N % i;
      int prefix = N >>> lengthN-((int)Math.ceil(Math.log(tail)/Math.log(2)));
      int period = N >>> lengthN-i;
      
      System.out.println ("iter "+Integer.toBinaryString (head) + 
    		  " ; "+Integer.toBinaryString(tail)+" ; "+
    		  Integer.toBinaryString(period)+";"+Integer.toBinaryString(prefix));
      
      int test = 0;
      for (int j = 0; j < lengthN/i; j++) {
    	  test = test << i;
    	  test += period;
      }
      if (test == head && prefix == tail)
    	  return i;
      System.out.println (Integer.toBinaryString (test));
    }
    
    return -1;
  }
}