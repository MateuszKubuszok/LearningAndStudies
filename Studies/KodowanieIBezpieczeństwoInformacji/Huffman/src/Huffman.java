import java.io.*;
import java.util.ArrayList;

public class Huffman {
	public static void main(String args[]){
		if (args.length == 0)
			return;
		else
			for (String Filename : args)
				parseFile (Filename);
	}
	
	public static void parseFile (String filename) {
		try {
			ArrayList<PrimNode> finalArray = new ArrayList<PrimNode> ();
			double[] counting = new double[65535];
			double total = 0;
			FileReader in = new FileReader(filename);
			
			int test;
			while((test = in.read()) > 0) {
				counting [test]++;
				total++;
			}
			
			double z;
			int arraySize = 0;
			for(int i = 0; i < 65535; i++)
				if (counting [i] != 0) {
					z = counting [i] / total;
					PrimNode node = new PrimNode();
					finalArray.add (arraySize, node);
					finalArray.get (arraySize).key = z;
					arraySize++;
				}
			
			double entropy = 0;
			for (PrimNode Node : finalArray)
				entropy += (Node.key * ((Math.log (Node.key))/Math.log (2)));
			
			double newKey = 0;
			while (newKey <= 0.99) {
				double min1 = 1.1;
				double min2= 1.1;
				int used1 = 0;
				int used2 = 0;
				
				for (int i = 0; i < arraySize; i++) {
					if (finalArray.get (i).key < min1 && !finalArray.get (i).used) {
						min2 = min1;
						min1 = finalArray.get(i).key;
						used2 = used1;
						used1 = i;
					} else if (finalArray.get (i).key < min2 && !finalArray.get (i).used) {
						min2 = finalArray.get (i).key;
						used2 = i;
					}
				}
				
				PrimNode node = new PrimNode();
				finalArray.add (arraySize, node);
				finalArray.get (used1).used = true;
				finalArray.get (used2).used = true;
				newKey = min1 + min2;
				finalArray.get (arraySize).key = newKey;
				finalArray.get (arraySize).inside = true;
				arraySize++;
			}
			
			double HuffAver = 0;
			for (int i = 0; i < arraySize; i++)
				if (finalArray.get (i).inside == true)
					HuffAver = HuffAver + finalArray.get(i).key;
			
			System.out.println("Plik: "+filename);
			System.out.println("Entropia:                                   " + (entropy*(-1)) + " bitów");
			System.out.println("Współczynnik kompresji:                     " + ((HuffAver)/8*100 + "%"));
			System.out.println("Średnia długość kodu:                       " + HuffAver + " bitów");
			System.out.println("Wspólczynnik średniej długoœci do entropii: " + (entropy/HuffAver)*-100 + "%\n");
		} catch (Exception e) {
			System.out.println (e.getMessage ());
			return;
		}
	}
}
