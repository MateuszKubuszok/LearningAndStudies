package roman_arabic;

public class RomanArabic {
	 private static String[] Digits = { "I", "V", "X", "L", "C", "D", "M" };

	 public static int roman2arabicic (String roman) throws RomanArabicException {
		 int i,
		 current,
		 howMuchToCut,
		 rank,
		 result = 0;
		 
		 String copy = roman;
		 
		 for (i = 3; (i >= 0) && (roman.length () > 0); i--) {
			 current = 0;
			 howMuchToCut = 0;
			 rank = (int) Math.pow ((double) 10, (double) i);
			 
			 try {
				 if (roman.startsWith (RomanArabic.Digits [2*i], 0)) {
					 // zaczyna sie od jednosci w danym rzedzie wielkosci: np. I, II, III, IV, IX
					 current = rank;
					 howMuchToCut = 1;
					 
					 if (roman.startsWith (RomanArabic.Digits [2*i] + RomanArabic.Digits [2*i], 1)) {
						 // jest to trojka w danym rzedzie wielkosci
						 current = rank * 3;
						 howMuchToCut = 3;
					 } else if (roman.startsWith (RomanArabic.Digits [2*i], 1)) {
						 // jest to dwojka w danym rzedzie wielkosci
						 current = rank * 2;
						 howMuchToCut = 2;
					 } else try {
						 if (roman.startsWith (RomanArabic.Digits [2*i+1], 1)) {
							 // jest to czworka w danym rzedzie wielkosci
							 current = rank * 4;
							 howMuchToCut = 2;
						 } else if (roman.startsWith (RomanArabic.Digits [2*i+2], 1)) {
							 // jest to dziewiatka w danym rzedzie wielkosci
							 current = rank * 9;
							 howMuchToCut = 2;
						 }
					 } catch (ArrayIndexOutOfBoundsException ex2) { /* nic nie rob */ }
				 } else if (roman.startsWith (RomanArabic.Digits [2*i+1], 0)) {
					 // zaczyna sie od piatki w danym rzedzie wielkosci: np. V, VI, VII, VIII
					 current = rank * 5;
					 howMuchToCut = 1;
					 
					 if (roman.startsWith (RomanArabic.Digits [2*i] + RomanArabic.Digits [2*i] + RomanArabic.Digits [2*i], 1)) {
						 // jest to osemka w danym rzedzie wielkosci
						 current = rank * 8;
						 howMuchToCut = 4;
					 } else if (roman.startsWith (RomanArabic.Digits [2*i] + RomanArabic.Digits [2*i], 1)) {
						 // jest to siodemka w danym rzedzie wielkosci
						 current = rank * 7;
						 howMuchToCut = 3;
					 } else if (roman.startsWith (RomanArabic.Digits [2*i], 1)) {
						 // jest to szostka w danym rzedzie wielkosci
						 current = rank * 6;
						 howMuchToCut = 2;
					 }
				 }
			 } catch (ArrayIndexOutOfBoundsException ex) { /* nic nie rob */ }
			 
			 result += current;
			 roman = roman.substring (howMuchToCut);
		 }
		 
		 if (!roman.equals (""))
			 throw new RomanArabicException ("\"" + copy + "\" is neither roman nor arabic number!");
		 
		 return result;
	 }
	 
	 public static String arabic2roman (int arabic) throws RomanArabicException {
		 int i, j, rank, rankValue;
		 
		 String result = "";
		 
		 if (arabic < 1 || arabic > 3999)
			 throw new RomanArabicException ("Given number "+arabic+" is out of range [1, 3999]!");
		 
		 for (i = 3; i >= 0; i--) {
			 rank = (int) Math.pow ((double) 10, (double) i);
			 
			 rankValue = arabic/rank;
			 
			 if (rankValue == 9) {
				 // cyfry IX, XC, CM
				 
				 result += RomanArabic.Digits [2*i] + RomanArabic.Digits [2*i+2];
			 }  else if (rankValue > 5) {
				 // cyfry VI, VII, VIII, LX, LXX, ...
				 
				 result += RomanArabic.Digits [2*i+1];
				 for (j = 0; j < rankValue-5; j++)
					 result += RomanArabic.Digits [2*i];
			 } else if (rankValue == 5) {
				 // cyfry V, L D
				 
				 result += RomanArabic.Digits [2*i+1];
			 } else if (rankValue == 4) {
				 // cyfry IV, XL, CD
				 
				 result += RomanArabic.Digits [2*i] + RomanArabic.Digits [2*i+1];
			 } else if (rankValue > 0) {
				 // cyfry I, II, III, X, XX, XXX, ...
				 
				 for (j = 0; j < rankValue; j++)
					 result += RomanArabic.Digits [2*i];
			 }
			 
			 arabic -= rankValue*rank;
		 }
		 
		 return result;
	 }
}
