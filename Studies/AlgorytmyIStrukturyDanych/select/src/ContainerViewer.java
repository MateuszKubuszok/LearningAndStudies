
public class ContainerViewer implements Observer {
	private Container Array;
	
	boolean ShowFull;
	
	public ContainerViewer (Container toWatch) {
		this.Array = toWatch;
		this.Array.addObserver (this);
		this.ShowFull = true;
	}
	
	public ContainerViewer (Container toWatch, boolean showFull) {
		this.Array = toWatch;
		this.Array.addObserver (this);
		this.ShowFull = showFull;
	}
	
	public void display () {
		int i, j, Max;

		System.out.println (this.Array.getMessage ());
		try {
			if (this.ShowFull) {
				Max = 0;
				for (i = 1; i <= this.Array.size (); i++)
					if (this.Array.get (i).Value > Max)
						Max = this.Array.get (i).Value;
		
				for (j = Max; j > 0; j--) {
					for (i = 1; i <= this.Array.size (); i++) {
						if (this.Array.get (i).Value >= j) {
							if (this.Array.get (i) == this.Array.getFollowed ())
								System.out.print ("#");
							else
								System.out.print ("@");
						} else
							System.out.print (" ");
					}
					System.out.print ("\n");
				}
				for (i = 1; i <= this.Array.size (); i++)
					System.out.print ("-");
				System.out.print ("\n\n");
			} else {
				for (i = 1; i <= this.Array.size (); i++)
					System.out.println (i + ": " + this.Array.get(i).Value + (this.Array.get (i) == this.Array.getFollowed () ? " <-----" : ""));
				System.out.println ();
			}
		} catch (ContainerException ex) {
			System.out.println (ex.getMessage ());
		}
	}
	
	public void refresh () {
		this.display ();
	}
}
