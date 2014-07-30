package binary_system;

public class BinarySystem {
	public int PreviousResult;
	public int CurrentNumber;
	
	protected boolean Negative;
	protected boolean ActionDone;
	protected boolean DigitPressed;
	protected boolean PreviousWasAction;
	
	protected String CurrentDisplay;
	
	protected Action CurrentAction;
	
	public BinarySystem () {
		this.ActionDone = false;
		this.PreviousResult = 0;
		this.CurrentNumber = 0;
		this.DigitPressed = false;
		this.CurrentAction = Action.PLUS;
		this.Negative = false;
	}
	
	public String pressed0 () 
	throws DisplayException {
		this.CurrentNumber = CurrentNumber * 2;
		this.ActionDone = false;
		this.DigitPressed = true;
		this.Negative = false;
		return this.refresh ();
	}
	
	public String pressed1 ()
	throws DisplayException {
		this.CurrentNumber = this.CurrentNumber * 2 + ((this.CurrentNumber >= 0 && !this.Negative) ? 1 : -1);
		this.ActionDone = false;
		this.DigitPressed = true;
		this.Negative = false;
		return this.refresh ();
	}
	
	public String pressedDivide ()
	throws DisplayException {
		this.action ();
		this.CurrentAction = Action.DIVIDE;
		return CurrentDisplay;
	}
	
	public String pressedEquals ()
	throws DisplayException {
		this.action ();
		CurrentNumber = PreviousResult = 0;
		this.CurrentAction = Action.PLUS;
		return CurrentDisplay;
	}
	
	public String pressedPlus ()
	throws DisplayException {
		this.action ();
		this.CurrentAction = Action.PLUS;
		return CurrentDisplay;
	}
	
	public String pressedMinus ()
	throws DisplayException {
		if (this.CurrentAction != Action.NONE && !this.DigitPressed) {
			this.Negative = !this.Negative;
			return this.refresh ();
		} else {
			this.action ();
			this.CurrentAction = Action.MINUS;
			return CurrentDisplay;
		}
	}
	
	public String pressedModulo ()
	throws DisplayException {
		this.action ();
		this.CurrentAction = Action.MODULO;
		return CurrentDisplay;
	}
	
	public String pressedMultiply ()
	throws DisplayException {
		this.action ();
		this.CurrentAction = Action.MULTIPLY;
		return CurrentDisplay;
	}
	
	public String refresh ()
	throws DisplayException {
		int Copy = this.CurrentNumber >= 0 ? this.CurrentNumber : -1*this.CurrentNumber;
		String Result = "";
		
		while (Copy > 0) {
			Result = (Copy % 2) + Result;
			Copy /= 2;
		}
		
		if (Result.length () > 20) {
			this.CurrentNumber = 0;
			this.PreviousResult = 0;
			this.CurrentAction = Action.NONE;
			this.refresh ();
			throw new DisplayException ("Digits' number surpasses digit limit (20)!");
		}
		
		if (this.CurrentNumber == 0)
			Result = "0";
		
		if (this.CurrentNumber < 0 || this.Negative)
			Result = "-" + Result;
		
		this.CurrentDisplay = Result;
		System.out.println (this.CurrentNumber + ":" + this.PreviousResult + "|" + this.CurrentAction);
		return Result;
	}
	
	private String action ()
	throws DisplayException {
		System.out.println ("action pressed");
		
		this.DigitPressed = false;
		
		if (this.ActionDone)
			return this.CurrentDisplay;
		
		switch (this.CurrentAction) {
			case NONE:
				
			break;
		
			case PLUS:
				this.CurrentNumber += this.PreviousResult;
			break;
			
			case MINUS:
				this.CurrentNumber = this.PreviousResult-this.CurrentNumber;	 
			break;
			
			case MULTIPLY:
				this.CurrentNumber *= this.PreviousResult;
			break;
			
			case DIVIDE:
				if (this.CurrentNumber != 0)
					this.CurrentNumber = this.PreviousResult/this.CurrentNumber;
				else {
					this.CurrentNumber = 0;
					this.PreviousResult = 0;
					this.CurrentAction = Action.NONE;
					this.refresh ();
					throw new DisplayException ("Division by 0!");
				}
			break;
			
			case MODULO:
				int Helper = this.PreviousResult%this.CurrentNumber;
				this.CurrentNumber = Helper + (Helper >= 0 ? 0 : (this.CurrentNumber >= 0 ? this.CurrentNumber : -1*this.CurrentNumber));
			break;
		}
		
		String Result = refresh ();
		this.ActionDone = true;
		this.PreviousResult = this.CurrentNumber;
		this.CurrentNumber = 0;
		this.CurrentAction = Action.NONE;
		
		System.out.println ("action done");
		
		return Result;
	}
	
	public enum Action {
		NONE,
		PLUS,
		MINUS,
		MULTIPLY,
		DIVIDE,
		MODULO,
		EQUALS
	};
	
	public class DisplayException extends Exception {
		public DisplayException (String message) {
			super (message);
		}
	}
}
