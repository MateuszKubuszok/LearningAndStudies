package dcpm;

import java.util.Random;

public class DPCM {
	double[]	Input;
	int[]		Output;
	
	public DPCM () {
		generateInput (100000);
	}
	
	public DPCM (int size) {
		generateInput (size);
	}
	
	public void encode () {
		for (int i = 0; i < Input.length; i++)
			printResult (
				Input [i], // TODO zamieniæ na odejmnowanie
				getRange (getRangeNr (getDPCM (i))),
				getRange (getRangeNr (Input [i]))
			);
	}
	
	public void generateInput (int size) {
		Random Random = new Random ();
		Input = new double[size];
		
		Input [0] = Random.nextGaussian ();
		for (int i = 1; i < Input.length; i++)
			Input [i] = Input [i-1]*0.9 + Random.nextGaussian ();
	}
	
	public double getDPCM (int i) {
		return Input [i] - p (i);
	} 
	
	public int getRange (int rangeNr) {
		switch (rangeNr) {
			case 1:
				return -1;
			default:
			case 2:
				return 0;
			case 3:
				return 1;
		}
	}
	
	public int getRangeNr (double number) {
		if (number < -0.43)
			return 1;
		else if (number > 0.43)
			return 3;
		else
			return 2;
	}
	
	private double p (int i) {
		return 0.0;
	}
	
	public static void printResult (double input, int encodedDPCM, int encodedQuantizer) {
		int FirstTabLength	= 25 - Double.toString (input).length (),
			SecondTabLength	= 25 - Integer.toString (encodedDPCM).length ();
		
		String	FirstTab	= "",
				SecondTab	= "";
		
		for (int i = 0; i < FirstTabLength; i++)
			FirstTab += " ";
		for (int i = 0; i < SecondTabLength; i++)
			SecondTab += " ";
		
		System.out.println (input + FirstTab + encodedDPCM + SecondTab + encodedQuantizer);
	}
	
	public static void main(String[] args) {
		DPCM Test = new DPCM (10);
		Test.encode ();
	}
}
