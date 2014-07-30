package life_threads;

import java.util.Random;

import javax.swing.JOptionPane;

import applet.Display;

/**
 * Zawiera kom�rki.
 * 
 * @author	Mateusz Kubuszok
 * 
 * @see		LifeThread
 */
public class LifeField extends Thread {
	/**
	 * Applet. 
	 */
	private Display Display;
	/**
	 * Pola kom�rek.
	 */
	public LifeThread[][] Lifes;
	/**
	 * Kt�re kom�rki wymagaj� od�wie�enia.
	 */
	public boolean[][] ToRefresh;
	/**
	 * Obecna liczba �ywych kom�rek
	 */
	public int LifesNumber;
	/**
	 * Liczba milisekund mi�dzy od�wie�eniami +/- 50%
	 */
	public int K;
	/**
	 * Czy uruchomno ju� w�tek.
	 */
	public boolean Started;
	
	/**
	 * Tworzy pole kom�rek.
	 * 
	 * @param	display	obiekt apletu na kt�rym wywo�ywana jest metoda update()
	 * @param	n		liczba komorek w poziomie
	 * @param	m		liczba kom�rek w pionie
	 * @param	nr		liczba �ywych kom�rek na starcie (musi by� nie wi�ksza ni� ilo�� dost.�pnych kom�rek)
	 * @param	k		liczba milisekund mi�dzy kolejnymi od�wie�eniami +/- 50% (misi by� wi�ksza od 0)
	 * 
	 * @throws	IllegalArgumentException	wyrzucany, gdy argumenty nie spe�niaj� zadanych im warunk�w
	 */
	public LifeField (Display display, int n, int m, int nr, int k)
	throws IllegalArgumentException {
		Display = display;
		Lifes = new LifeThread[n][m];
		ToRefresh = new boolean[n][m];
		LifesNumber = 0;
		K = k;
		Started = false;
		
		Random Rand = new Random ();
		
		if (nr > n*m || n < 1 || m < 1 || k <= 0)
			throw new IllegalArgumentException ();
		
		for (int c = 0; c < nr;) {
			int i = Rand.nextInt (n),
				j = Rand.nextInt (m);
			
			if (Lifes [i][j] == null) {
				Lifes [i][j] = new LifeThread (this);
				Lifes [i][j].setXY (i, j);
				c++;
			}
		}
	}
	
	/**
	 * Cia�o w�tki zaka�czaj�cego program, gdy wszystkie kom�rki umr�.
	 */
	public void run () {
		Started = true;
		synchronized (this) {
			this.notifyAll ();
		}
		
		while (true) {
			synchronized (Lifes) {
				try {
					this.wait (5);
				} catch (Exception ex) {}
			}
			if (LifesNumber <= 0) {
				JOptionPane.showMessageDialog (Display, "All cells has died!", "Info", 1);
				System.exit (0);
			}
		}
	}
	
	/**
	 * Aktualizuje obraz dla komorki o podanych wsp�rz�dnych.
	 * 
	 * @param	x	wsp�rz�dna pozioma aktualizowanej kom�rki
	 * @param	y	wsp�rz�dna pionowa aktualizowanej kom�rki
	 */
	public synchronized void update (int x, int y) {
		ToRefresh [x][y] = true;
		Display.repaint ();
	}
}
