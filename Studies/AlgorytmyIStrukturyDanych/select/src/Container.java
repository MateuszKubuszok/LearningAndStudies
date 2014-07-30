public class Container extends Observable {
	private int			Delay;
	private	Element[]	Elements;
	private	Element		Followed;
	private boolean		LockFollowed;
	public	boolean		InformAll;
	private	String		Message;
	private int[]		Original;
	
	public Container () {
		super ();
		
		this.Elements = new Element[0];
		this.Original = new int[0];
		
		this.init ();
	}
	
	public Container (int[] originalArray) {
		super ();
		
		int i;
		
		this.Elements = new Element[originalArray.length];
		this.Original = originalArray;
		
		for (i = 0; i < originalArray.length; i++)
			this.Elements [i] = new Element (originalArray [i], i+1);
		
		this.init ();
	}
	
	public Container clone () {
		Container Clone = new Container (this.getArray ());
		if (this.Followed != null) {
			try {
				Clone.setFollowed (this.Followed.getCurrentIndex ());
			} catch (ContainerException ex) {}
		}
		return Clone;
	}
	
	public void init () {
		this.Followed = null;
		this.InformAll = true;
		this.LockFollowed = false;
		this.Message = new String ("");
		this.setDelay (100);
	}
	
	public void informAll () {
		if (this.Delay > 0)
			RunHelper.waitFor (this.Delay);
		super.informAll ();
	}
	
	public boolean equal (int key1, int key2)
	throws ContainerException {
        return (boolean) (this.get (key1).Value == this.get (key2).Value);
    }
	
	public boolean greater (int key1, int key2)
	throws ContainerException {
        return (boolean) (this.get (key1).Value > this.get (key2).Value);
    }

    public boolean greaterEqual (int key1, int key2)
	throws ContainerException {
        return (boolean) (this.get (key1).Value >= this.get (key2).Value);
    }

    public boolean lesser (int key1, int key2)
	throws ContainerException {
        return (boolean) (this.get (key1).Value < this.get (key2).Value);
    }

    public boolean lesserEqual (int key1, int key2)
	throws ContainerException  {
        return (boolean) (this.get (key1).Value <= this.get (key2).Value);
    }
	
	public Element get (int key)
	throws ContainerException {
		try {
			return this.Elements [key-1];
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ContainerException ("Key "+key+" is out of defined container range ["+(this.Elements.length > 0 ? "1" : "0")+","+this.Elements.length+"]!");
		}
	}
	
	public int[] getArray () {
		int[] Result = new int[this.size ()];
		
		for (int i = 1; i <= this.size (); i++) {
			try {
				Result [i-1] = this.get (i).Value;
			} catch (ContainerException ex) {}
		}
		
		return Result;
	}
	
	public Element getFollowed () {
		return this.Followed;
	}
	
	public String getMessage () {
		return this.Message;
	} 
	
	public void lockFollowed () {
		this.LockFollowed = true;
	}
	
	public void lockFollowed (boolean lock) {
		this.LockFollowed = lock;
	}
	
	public void set (int key, Element value)
	throws ContainerException {
		try {
			this.Elements [key-1] = value;
			this.Elements [key-1].setCurrentIndex (key);
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ContainerException ("Key "+key+" is out of defined container range ["+(this.Elements.length > 0 ? "1" : "0")+","+this.Elements.length+"]!");
		}
		
		if (this.InformAll)
			this.informAll ();
	}
	
	public void setDelay (int delay) {
		this.Delay = delay;
	} 
	
	public void setFollowed (Element value)
	throws ContainerException {
		if (this.LockFollowed)
			return;
		
		try {
			int Size = this.size (),
				i;
			
			for (i = 0; i < Size; i++)
				if (this.Elements [i] == value)
					break;
			
			if (i > Size && value != null)
				throw new ContainerException ("");
		} catch (ContainerException ex) {
			throw new ContainerException ("Given element is not this Container's item!");
		}
		
		this.Followed = value;
		this.informAll ();
	}
	
	public void setFollowed (int key)
	throws ContainerException {
		if (key < 1 || key > this.size ())
			throw new ContainerException ("Given element is not this Container's item!");
		
		this.Followed = this.get (key);
		this.informAll ();
	}
	
	public void setMessage (String message) {
		this.Message = message;
		this.informAll ();
	}
	
	public int size () {
		return this.Elements.length;
	}
	
	public void swap (int key1, int key2)
	throws ContainerException {
		boolean	Inform;
		Element Temp;
		
		Inform = this.InformAll;
		this.InformAll = false;
		try {
			Temp = this.get (key1);
			this.set (key1, this.get (key2));
			this.set (key2, Temp);
		} catch (ContainerException ex) {
			this.InformAll = Inform;
			throw ex;
		}
		this.InformAll = Inform;
		if (this.InformAll)
			this.informAll ();
	}
}
