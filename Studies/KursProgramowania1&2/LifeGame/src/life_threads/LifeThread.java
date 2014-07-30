package life_threads;

import java.util.Random;

/**
 * Pojedynczy w¹tek-¿ywa komórka
 * 
 * @author Mateusz Kubuszok
 */
public class LifeThread extends Thread {
	/**
	 * Obiekt zarz¹dzaj¹cy komórkami.
	 */
	private LifeField Owner;
	/**
	 * Pole komórek zawieraj¹ce ten w¹tek.
	 */
	private LifeThread[][] Field;
	/**
	 * Wspólrzêdna pozioma bie¿¹cego w¹tku.
	 */
	private int X;
	/**
	 * Wspólrzêdna pionowa bie¿¹cego w¹tku.
	 */
	private int Y;
	
	/**
	 * Tworzy now¹ komórkê.
	 * 
	 * @param	field	obiekt zarzadcy komórek
	 */
	public LifeThread (LifeField field) {
		Owner = field;
		Field = Owner.Lifes;
		Owner.LifesNumber++;
	}
	
	/**
	 * Treœæ w¹tki komórki.
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
	 * OpóŸnienie danej komórki.
	 */
	private synchronized void delay () {
		Random Rand = new Random ();
		
		try {
			wait ((int) ((((double) Owner.K) * (Rand.nextDouble() + 0.5))));
		} catch (Exception ex) {}
	}
	
	/**
	 * Ustawia komórkê na jej pozycjê.
	 * 
	 * @return	true jeœli w¹tek ma zostaæ zakoñczony, false jeœli ma kontynuowaæ
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
	 * Ustaiwa komórkê na wskazan¹ pozycjê.
	 * 
	 * @param 	x	wspólrzêdna pozioma 
	 * @param	y	wspó³rzêdna pioniowa
	 */
	public synchronized void setXY (int x, int y) {
		X = x;
		Y = y;
		Field [X][Y] = this;
		
		if (!(this.isAlive ()))
			this.start ();
	}
	
	/**
	 * Zlicza s¹siadów komorki.
	 * 
	 * @return	iloœæ s¹siadów
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
	 * Sprzwdza czy komorka jest ¿ywa.
	 * 
	 * @param	cell komórka w¹tku
	 * 
	 * @return	zwraca true jesli komorka jest ¿ywa
	 */
	public synchronized static boolean isAlive (LifeThread cell) {
		return (cell != null && cell.isAlive ());
	}
}
