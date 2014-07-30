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
	 * Klasa obs�uguj�ca kom�rki.
	 */
	private LifeField Field;
	/**
	 * Czy wy�wietlanie ca�y obszar.
	 * 
	 * Je�li tak, mo�na od�wie�a� wy��cznie zmienione pola.
	 */
	private boolean InitiatedDisplay; 
	
	/**
	 * Liczba kom�rek w poziomie.
	 */
	public int n;
	/**
	 * Liczba kom�rek w pionie.
	 */
	public int m;
	/**
	 * Liczba milisekund mi�dzy kolejnymi od�wie�eniami kom�rki +/- 50%.
	 */
	public int k;
	/**
	 * Liczba �ywych kom�rek na starcie.
	 */
	public int cells;
	
	private boolean AlreadySet = false;
	
	/**
	 * U�ywany podczas uruchamiania jako appletu.
	 */
	public Display () {
		m = n = k = cells = 0;
	}
	
	/**
	 * Konstruktor wywo�ywany przez aplikacjie
	 * 
	 * @see		window.DisplayWindow
	 * 
	 * @param	N		liczba kom�rek w poziomie
	 * @param	M		liczba mok�rek w pionie
	 * @param	K		liczba okre�laj�ca czas mi�dzy od�wie�eniami kom�rek +/- 50%
	 * @param	Cells	liczba �ywych kom�rek na starcie
	 */
	public Display (int N, int M, int K, int Cells) {
		n = N;
		m = M;
		k = K;
		cells = Cells;
		AlreadySet = true;
	}
	
	/**
	 * Pobiera warto��i parametr�w je�li nie by�y ustawione wcze�niej.
	 * 
	 * Zamyka applet je�li nie podano wszystkich wymaganych parametr�w. 
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
	 * Uruchamia w�tki. 
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
	 * Rysuje pojedyncz� kom�rk� tabeli.
	 * 
	 * @param	gDC			aktualizowany obiekt grafiki
	 * @param	leftMargin	lewy margines (grubo�� lewej kraw�dzi)
	 * @param	topMargin	g�rny margines (grubo�� g�rnej kraw�dzi)
	 * @param	size		d�ugo�� kraw�dzi pojedynczej kom�rki
	 * @param	x			wsp�rz�dna pozioma
	 * @param 	y			wsp�rz�dna pionowa
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
	 * Rysuje pust� (martw�) kom�rk�.
	 * 
	 * @param	gDC			aktualizowany obiekt grafiki
	 * @param	leftMargin	lewy margines (grubo�� lewej kraw�dzi)
	 * @param	topMargin	g�rny margines (grubo�� g�rnej kraw�dzi)
	 * @param	size		d�ugo�� kraw�dzi pojedynczej kom�rki
	 * @param	x			wsp�rz�dna pozioma
	 * @param 	y			wsp�rz�dna pionowa
	 */
	private void drawEmptyCell (Graphics gDC, int leftMargin, int topMargin, int size, int x, int y) {
		gDC.drawRect (leftMargin+x*size, topMargin+y*size, size, size);
	}
	
	/**
	 * Rysuje wype�nion� (�yw�) kom�rk�.
	 * 
	 * @param	gDC			aktualizowany obiekt grafiki
	 * @param	leftMargin	lewy margines (grubo�� lewej kraw�dzi)
	 * @param	topMargin	g�rny margines (grubo�� g�rnej kraw�dzi)
	 * @param	size		d�ugo�� kraw�dzi pojedynczej kom�rki
	 * @param	x			wsp�rz�dna pozioma
	 * @param 	y			wsp�rz�dna pionowa
	 */
	private void drawFilledCell (Graphics gDC, int leftMargin, int topMargin, int size, int x, int y) {
		gDC.fillRect (leftMargin+x*size, topMargin+y*size, size+1, size+1);
	}
	
	/**
	 * Dba o to aby w przypadku zmiany rozmiaru okna przemalowany zosta� ca�y obszar, nie wy��cznie zmienione pola. 
	 */
	public class ResizeListener implements HierarchyBoundsListener {
		public void ancestorMoved (HierarchyEvent event) {}
		public void ancestorResized (HierarchyEvent event) {
			InitiatedDisplay = false;
		}
	}
}
