package life_threads;

import java.util.Random;

/**
 * Pojedynczy w�tek-�ywa kom�rka
 * 
 * @author Mateusz Kubuszok
 */
public class LifeThread extends Thread {
	/**
	 * Obiekt zarz�dzaj�cy kom�rkami.
	 */
	private LifeField Owner;
	/**
	 * Pole kom�rek zawieraj�ce ten w�tek.
	 */
	private LifeThread[][] Field;
	/**
	 * Wsp�lrz�dna pozioma bie��cego w�tku.
	 */
	private int X;
	/**
	 * Wsp�lrz�dna pionowa bie��cego w�tku.
	 */
	private int Y;
	
	/**
	 * Tworzy now� kom�rk�.
	 * 
	 * @param	field	obiekt zarzadcy kom�rek
	 */
	public LifeThread (LifeField field) {
		Owner = field;
		Field = Owner.Lifes;
		Owner.LifesNumber++;
	}
	
	/**
	 * Tre�� w�tki kom�rki.
	 */
	public void run () {
		if (!Owner.Started)
			synchronized (Owner) {
				try {
					Owner.wait ();
				} catch (Exception ex) {}
			}
	
		while (true)
		synchronized (this)
		{			
			yield ();
			delay ();
			
			synchronized (Owner) {
				if (setCell ())
					return;
			}
		}
	}
	
	/**
	 * Op�nienie danej kom�rki.
	 */
	private synchronized void delay () {
		Random Rand = new Random ();
		
		try {
			wait ((int) ((((double) Owner.K) * (Rand.nextDouble() + 0.5))));
		} catch (Exception ex) {}
	}
	
	/**
	 * Ustawia kom�rk� na jej pozycj�.
	 * 
	 * @return	true je�li w�tek ma zosta� zako�czony, false je�li ma kontynuowa�
	 */
	private synchronized boolean setCell () {
		int OriginalX = X,
			OriginalY = Y;
		
		switch (countNeighbours ()) {
		case 1:
			if (isAlive (getRight ()) && !isAlive (getLeft ()))
				setLeft (new LifeThread (Owner));
			else if (isAlive (getLeft ()) && !isAlive (getRight ()))
				setRight (new LifeThread (Owner));
			else if (isAlive (getBottom ()) && !isAlive (getTop ()))
				setTop (new LifeThread (Owner));
			else if (isAlive (getTop ()) && !isAlive (getBottom ()))
				setBottom (new LifeThread (Owner));
			Owner.update (OriginalX, OriginalY);
			return false;
			
		default:
		case 2:
			return false;
			
		case 3:
			if (!isAlive (getLeft ())) {
				Field [X][Y] = null;
				setLeft (this);
			} else if (!isAlive (getRight ())) {
				Field [X][Y] = null;
				setRight (this);
			} else if (!isAlive (getTop ())) {
				Field [X][Y] = null;
				setTop (this);
			} else if (!isAlive (getBottom ())) {
				Field [X][Y] = null;
				setBottom (this);
			}
			Owner.update (OriginalX, OriginalY);
			return false;
		
		case 4:
		case 0:
			Field [X][Y] = null;
			Owner.LifesNumber--;
			Owner.update (OriginalX, OriginalY);
			return true;
		}
	}
	
	/**\
	 * Ustaiwa kom�rk� na wskazan� pozycj�.
	 * 
	 * @param 	x	wsp�lrz�dna pozioma 
	 * @param	y	wsp�rz�dna pioniowa
	 */
	public synchronized void setXY (int x, int y) {
		X = x;
		Y = y;
		Field [X][Y] = this;
		
		if (!(this.isAlive ()))
			this.start ();
	}
	
	/**
	 * Zlicza s�siad�w komorki.
	 * 
	 * @return	ilo�� s�siad�w
	 */
	public synchronized int countNeighbours () {
		return	(
			(isAlive (getBottom ()) ? 1 : 0) +
			(isAlive (getLeft ()) ? 1 : 0) + 
			(isAlive (getRight ()) ? 1 : 0) +
			(isAlive (getTop ()) ? 1 : 0)
		);
	}
	
	public synchronized LifeThread getBottom () {
		int	X = this.X,
			Helper = this.Y-1,
			Y = Helper >= 0 ? Helper : Helper + Field [this.X].length;
		
		return Field [X][Y];
	}
	
	private synchronized LifeThread getLeft () {
		int Helper = this.X-1,
			X = Helper >= 0 ? Helper : Helper + Field.length,
			Y = this.Y;
		
		return Field [X][Y];
	}
	
	private synchronized LifeThread getRight () {
		int X = (this.X+1) % Field.length,
			Y = this.Y;
		
		return Field [X][Y];
	}
	
	private synchronized LifeThread getTop () {
		int	X = this.X,
			Y = (this.Y+1) % Field [this.X].length;
		
		return Field [X][Y];
	}
	
	private synchronized void setBottom (LifeThread cell) {
		int	X = this.X,
			Helper = this.Y-1,
			Y = Helper >= 0 ? Helper : Helper + Field [this.X].length;
		
		cell.setXY (X, Y);
	}
	
	private synchronized void setLeft (LifeThread cell) {
		int Helper = this.X-1,
			X = Helper >= 0 ? Helper : Helper + Field.length,
			Y = this.Y;
		
		cell.setXY (X, Y);
	}
	
	private synchronized void setRight (LifeThread cell) {
		int X = (this.X+1) % Field.length,
			Y = this.Y;
		
		cell.setXY (X, Y);
	}
	
	private synchronized void setTop (LifeThread cell) {
		int	X = this.X,
			Y = (this.Y+1) % Field [this.X].length;
		
		cell.setXY (X, Y);
	}
	
	/**
	 * Sprzwdza czy komorka jest �ywa.
	 * 
	 * @param	cell kom�rka w�tku
	 * 
	 * @return	zwraca true jesli komorka jest �ywa
	 */
	public synchronized static boolean isAlive (LifeThread cell) {
		return (cell != null && cell.isAlive ());
	}
}
