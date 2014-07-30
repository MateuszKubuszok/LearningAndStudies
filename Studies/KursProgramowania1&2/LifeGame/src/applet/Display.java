package applet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;

import javax.swing.JApplet;
import javax.swing.JOptionPane;

import life_threads.LifeField;
import life_threads.LifeThread;

/**
 * 
 * @author	Mateusz Kubuszok
 * 
 * @see		LifeField
 * @see		LifeThread
 */
@SuppressWarnings("serial")
public class Display extends JApplet {
	/**
	 * Klasa obs³uguj¹ca komórki.
	 */
	private LifeField Field;
	/**
	 * Czy wyœwietlanie ca³y obszar.
	 * 
	 * Jeœli tak, mo¿na odœwie¿aæ wy³¹cznie zmienione pola.
	 */
	private boolean InitiatedDisplay; 
	
	/**
	 * Liczba komórek w poziomie.
	 */
	public int n;
	/**
	 * Liczba komórek w pionie.
	 */
	public int m;
	/**
	 * Liczba milisekund miêdzy kolejnymi odœwie¿eniami komórki +/- 50%.
	 */
	public int k;
	/**
	 * Liczba ¿ywych komórek na starcie.
	 */
	public int cells;
	
	private boolean AlreadySet = false;
	
	/**
	 * U¿ywany podczas uruchamiania jako appletu.
	 */
	public Display () {
		m = n = k = cells = 0;
	}
	
	/**
	 * Konstruktor wywo³ywany przez aplikacjie
	 * 
	 * @see		window.DisplayWindow
	 * 
	 * @param	N		liczba komórek w poziomie
	 * @param	M		liczba mokórek w pionie
	 * @param	K		liczba okreœlaj¹ca czas miêdzy odœwie¿eniami komórek +/- 50%
	 * @param	Cells	liczba ¿ywych komórek na starcie
	 */
	public Display (int N, int M, int K, int Cells) {
		n = N;
		m = M;
		k = K;
		cells = Cells;
		AlreadySet = true;
	}
	
	/**
	 * Pobiera wartoœæi parametrów jeœli nie by³y ustawione wczeœniej.
	 * 
	 * Zamyka applet jeœli nie podano wszystkich wymaganych parametrów. 
	 */
	public void init () {
		this.getContentPane ().addHierarchyBoundsListener (new ResizeListener ());
		
		InitiatedDisplay = false;
		if (!AlreadySet) {
			try {
				n = 	Integer.parseInt (getParameter ("n"));
				m = 	Integer.parseInt (getParameter ("m"));
				k = 	Integer.parseInt (getParameter ("k"));
				cells = Integer.parseInt (getParameter ("cells"));
			} catch (Exception ex) {
				JOptionPane.showMessageDialog (this, "Not enough parameters passed!", "Error", 0);
				System.exit (0);
			}
		}
		try {
			Field = new LifeField (this, n, m, cells, k);
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog (this, "Invalid parameter was passed ("+ex.getMessage ()+")!", "Error", 0);
			System.exit (0);
		}
	}
	
	/**
	 * Uruchamia w¹tki. 
	 */
	public void start () {
		if (Field != null && !Field.isAlive ())
			Field.start ();
	}
	
	/**
	 * Przemalowuje obraz.
	 */
	public void paint (Graphics gDC) {
		Insets Borders = this.getInsets ();
		
		int Width = this.size ().width - Borders.left - Borders.right,
			Height = this.size ().height - Borders.top - Borders.bottom,
			FieldSize = (Width/n < Height/m) ? (Width/n) : (Height/m),
			LeftMargin = Borders.left+(Width-FieldSize*n)/2,
			TopMargin = Borders.top+(Height-FieldSize*m)/2;
		
		if (!InitiatedDisplay) {
			for (int X = 0; X < n; X++)
				for (int Y = 0; Y < m; Y++)
					drawCell (gDC, LeftMargin, TopMargin, FieldSize, X, Y);
			InitiatedDisplay = true;
		} else {
			for (int X = 0; X < n; X++)
				for (int Y = 0; Y < m; Y++)
					if (Field.ToRefresh [X][Y]) {
						drawCell (gDC, LeftMargin, TopMargin, FieldSize, X, Y);
						drawCell (gDC, LeftMargin, TopMargin, FieldSize, (X+1) % n, Y);
						drawCell (gDC, LeftMargin, TopMargin, FieldSize, X, (Y+1) % m);
						drawCell (gDC, LeftMargin, TopMargin, FieldSize, ((X-1) >= 0 ? (X-1) : (n-1)), Y);
						drawCell (gDC, LeftMargin, TopMargin, FieldSize, X, ((Y-1) >= 0 ? (Y-1) : (m-1)));
						Field.ToRefresh [X][Y] = false;
					}
		}
	}
	
	/**
	 * Rysuje pojedyncz¹ komórkê tabeli.
	 * 
	 * @param	gDC			aktualizowany obiekt grafiki
	 * @param	leftMargin	lewy margines (gruboœæ lewej krawêdzi)
	 * @param	topMargin	górny margines (gruboœæ górnej krawêdzi)
	 * @param	size		d³ugoœæ krawêdzi pojedynczej komórki
	 * @param	x			wspó³rzêdna pozioma
	 * @param 	y			wspó³rzêdna pionowa
	 */
	private void drawCell (Graphics gDC, int leftMargin, int topMargin, int size, int x, int y) {
		gDC.setColor (Color.WHITE);
		gDC.fillRect (leftMargin+x*size, topMargin+y*size, size+1, size+1);
		
		gDC.setColor (Color.BLACK);
		if (LifeThread.isAlive (Field.Lifes [x][y]))
			drawFilledCell (gDC, leftMargin, topMargin, size, x, y);
		else
			drawEmptyCell (gDC, leftMargin, topMargin, size, x, y);
	}
	
	/**
	 * Rysuje pust¹ (martw¹) komórkê.
	 * 
	 * @param	gDC			aktualizowany obiekt grafiki
	 * @param	leftMargin	lewy margines (gruboœæ lewej krawêdzi)
	 * @param	topMargin	górny margines (gruboœæ górnej krawêdzi)
	 * @param	size		d³ugoœæ krawêdzi pojedynczej komórki
	 * @param	x			wspó³rzêdna pozioma
	 * @param 	y			wspó³rzêdna pionowa
	 */
	private void drawEmptyCell (Graphics gDC, int leftMargin, int topMargin, int size, int x, int y) {
		gDC.drawRect (leftMargin+x*size, topMargin+y*size, size, size);
	}
	
	/**
	 * Rysuje wype³nion¹ (¿yw¹) komórkê.
	 * 
	 * @param	gDC			aktualizowany obiekt grafiki
	 * @param	leftMargin	lewy margines (gruboœæ lewej krawêdzi)
	 * @param	topMargin	górny margines (gruboœæ górnej krawêdzi)
	 * @param	size		d³ugoœæ krawêdzi pojedynczej komórki
	 * @param	x			wspó³rzêdna pozioma
	 * @param 	y			wspó³rzêdna pionowa
	 */
	private void drawFilledCell (Graphics gDC, int leftMargin, int topMargin, int size, int x, int y) {
		gDC.fillRect (leftMargin+x*size, topMargin+y*size, size+1, size+1);
	}
	
	/**
	 * Dba o to aby w przypadku zmiany rozmiaru okna przemalowany zosta³ ca³y obszar, nie wy³¹cznie zmienione pola. 
	 */
	public class ResizeListener implements HierarchyBoundsListener {
		public void ancestorMoved (HierarchyEvent event) {}
		public void ancestorResized (HierarchyEvent event) {
			InitiatedDisplay = false;
		}
	}
}
