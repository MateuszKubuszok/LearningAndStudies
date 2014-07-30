package life_threads;

import java.util.Random;

import javax.swing.JOptionPane;

import applet.Display;

/**
 * Zawiera komórki.
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
	 * Pola komórek.
	 */
	public LifeThread[][] Lifes;
	/**
	 * Które komórki wymagaj¹ odœwie¿enia.
	 */
	public boolean[][] ToRefresh;
	/**
	 * Obecna liczba ¿ywych komórek
	 */
	public int LifesNumber;
	/**
	 * Liczba milisekund miêdzy odœwie¿eniami +/- 50%
	 */
	public int K;
	/**
	 * Czy uruchomno ju¿ w¹tek.
	 */
	public boolean Started;
	
	/**
	 * Tworzy pole komórek.
	 * 
	 * @param	display	obiekt apletu na którym wywo³ywana jest metoda update()
	 * @param	n		liczba komorek w poziomie
	 * @param	m		liczba komórek w pionie
	 * @param	nr		liczba ¿ywych komórek na starcie (musi byæ nie wiêksza ni¿ iloœæ dost.êpnych komórek)
	 * @param	k		liczba milisekund miêdzy kolejnymi odœwie¿eniami +/- 50% (misi byæ wiêksza od 0)
	 * 
	 * @throws	IllegalArgumentException	wyrzucany, gdy argumenty nie spe³niaj¹ zadanych im warunków
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
	 * Cia³o w¹tki zakañczaj¹cego program, gdy wszystkie komórki umr¹.
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
	 * Aktualizuje obraz dla komorki o podanych wspó³rzêdnych.
	 * 
	 * @param	x	wspó³rzêdna pozioma aktualizowanej komórki
	 * @param	y	wspó³rzêdna pionowa aktualizowanej komórki
	 */
	public synchronized void update (int x, int y) {
		ToRefresh [x][y] = true;
		Display.repaint ();
	}
}
