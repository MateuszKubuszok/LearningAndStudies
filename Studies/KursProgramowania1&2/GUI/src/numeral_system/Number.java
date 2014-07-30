package numeral_system;

public class Number {
	private static String[] Digits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	
	private int n;
	
	Number (int n) {
		this.n = n;
	}
	
	Number (String n, int basement) throws NumberException {
		if (basement > 16 || basement < 2)
			throw new NumberException ("Basement " + basement + " is out of allowed range [2,16]!");
		
		String  LastDigit,
		Copy = n;
		int     i,
		rank = 1,
		sign = 1;
		
		if (n.length () > 0 && n.substring (0, 1).equals ("-")) {
			sign = -1;
			n = n.substring (1);
		}
		
		while (n.length () > 0) {
			LastDigit = n.substring (n.length ()-1);
			
			for (i = 0; i < Number.Digits.length; i++)
				if (Number.Digits [i].equals (LastDigit)) {
					if (i >= basement)
						throw new NumberException (Copy + " isn't number from system based on " + basement + "!");
					
					this.n += rank * i;
					break;
				}
			if (i >= Number.Digits.length)
				throw new NumberException (Copy + " isn't number from system based on " + basement + "!");
			
			n = n.substring (0, n.length ()-1);
			rank *= basement;
		}
		
		this.n *= sign;
	}
	
	public String form (int basement) throws NumberException {
		if (basement > 16 || basement < 2)
			throw new NumberException ("basement " + basement + " jest poza dopuszczalnym zakresem [2,16]!");
		
		String result = new String ("");
		
		int sign = 1,
		Copy = this.n,
		helper;
		
		if (this.n < 0)
			sign = -1;
		
		Copy *= sign;
		
		if (Copy != 0) {
			while (Copy > 0) {
				helper = Copy % basement;
				Copy /= basement;
				
				if (helper > 0 || Copy > 0)
					result = Number.Digits [helper] + result;
			}
		} else
			result = new String ("0");
		
		if (sign < 0)
			result = "-" + result;
		
		return result;
	}
}
