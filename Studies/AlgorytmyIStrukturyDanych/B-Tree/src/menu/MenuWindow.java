package menu;

import coordinate.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.text.NumberFormat;

import javax.swing.*;

public class MenuWindow implements Runnable {
	// Koordynuje dzia³ania poszczególnych modu³ów
	private Coordinator Controller;
	
	// Obiekty okna
	private NumberFormat	Format;
	private MenuListener	Listener;
	private	Container		Pane;
	public	JFrame 			Window;
	
	// Pola z których pobieramy wartoœci
	private	JFormattedTextField	Delay;
	private	JComboBox			Generator;
	private	JButton				Run;
	private	JFormattedTextField	Degree;
	private	JFormattedTextField	Seed;
	private	JFormattedTextField	Size;
	private	JComboBox			Step;
	
	// Pola statystyk
	public	JLabel MaxInsertionComparisions;
	public	JLabel MaxDeletionComparisions;
	public	JLabel AverageInsertionComparisions;
	public	JLabel AverageDeletionComparisions;
	public	JLabel MaxInsertionDiskAccess;
	public	JLabel MaxDeletionDiskAccess;
	public	JLabel AverageInsertionDiskAccess;
	public	JLabel AverageDeletionDiskAccess;
	
	/**
	 * Konstruktor.
	 */
	public MenuWindow (Coordinator controller) {
		Controller = controller;
		Listener	= new MenuListener ();
		Format		= NumberFormat.getIntegerInstance ();
		
		Window = new JFrame ("B-Tree");
		Window.addWindowListener (Listener);
		Window.setResizable (false);
		Window.setSize (640, 350);
		
		Pane = Window.getContentPane ();
		Pane.setBackground (Color.WHITE);
		
		createMenu ();
		
		Window.setVisible (true);
	}
	
	/**
	 * Tworzy i rozmieszcza pozycje menu, oraz statystyk.
	 */
	private void createMenu () {
		Pane.setLayout (new GridBagLayout ());
		
		Pane.add (
			getInputMenu (),
			getGridBagConstraints ( 0, 0, 1, 1 )
		);
		
		Pane.add (
			getBTreeMenu (),
			getGridBagConstraints ( 1, 0, 1, 1 )
		);
		
		Pane.add (
			getDisplayMenu (),
			getGridBagConstraints ( 2, 0, 1, 1 )
		);
		
		Pane.add (
			getStats (),
			getGridBagConstraints ( 0, 1, 3, 2 )
		);
	}
	
	/**
	 * Tworzy obiekt z ustawieniami pozycjonowania dla GrigBagLayout.
	 */
	private GridBagConstraints getGridBagConstraints (int x, int y, int width, int height) {
		GridBagConstraints GBC = new GridBagConstraints();
		
		GBC.anchor = GridBagConstraints.NORTHWEST;
		GBC.gridx = x;
		GBC.gridy = y;
		GBC.gridwidth = width;
		GBC.gridheight = height;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		
		return GBC;
	}
	
	/**
	 * Tworzy i zwraca obiekt menu z opcjami danych wejœciowych.
	 */
	private JPanel getInputMenu () {
		JPanel Menu = new JPanel ();
		
		Menu.setBackground (Color.WHITE);
		Menu.setBorder (BorderFactory.createTitledBorder ("Input settings"));
		Menu.setLayout (new GridLayout (3, 2, 10, 10));
		
		String[] PossibleInputs = {
			"random order",
			"ascending",
			"descending"
		};
		
		JLabel SizeL		= new JLabel ("Size of data");
		this.Size			= new JFormattedTextField (Format);
		JLabel GeneratorL 	= new JLabel ("Generating keys");
		this.Generator 		= new JComboBox (PossibleInputs);
		JLabel SeedL 		= new JLabel ("Seed (0=any)");
		this.Seed			= new JFormattedTextField (Format);
		
		this.Size.setValue (new Integer (30));
		this.Size.addPropertyChangeListener ("value", Listener);
		this.Generator.setSelectedIndex (0);
		this.Seed.setValue (new Integer (0));
		this.Seed.addPropertyChangeListener ("value", Listener);
		
		Menu.add (SizeL);
		Menu.add (Size);
		Menu.add (GeneratorL);
		Menu.add (Generator);
		Menu.add (SeedL);
		Menu.add (Seed);
		
		return Menu;
	}
	
	/**
	 * Tworzy i zwraca obiekt menu z opcjami wyœwietlania.
	 */
	private JPanel getDisplayMenu () {
		JPanel Menu = new JPanel ();
		
		Menu.setBackground (Color.WHITE);
		Menu.setBorder (BorderFactory.createTitledBorder ("Display settings"));
		Menu.setLayout (new GridLayout (3, 2, 10, 10));
		
		String[] PossibleSteps = {
			"each action",
			"each operation"
		};
		
		JLabel StepL	= new JLabel ("Single step");
		Step			= new JComboBox (PossibleSteps);
		JLabel DelayL	= new JLabel ("Delay (0=step mode)");
		Delay			= new JFormattedTextField (Format);
		JLabel RunL 	= new JLabel ("Startup");
		Run				= new JButton ("Run");
		
		Step.setSelectedIndex (1);
		Delay.setValue (new Integer (750));
		Delay.addPropertyChangeListener ("value", Listener);
		Run.addActionListener (Listener);
		
		Menu.add (StepL);
		Menu.add (Step);
		Menu.add (DelayL);
		Menu.add (Delay);
		Menu.add (RunL);
		Menu.add (Run);
		
		return Menu;
	}
	
	/**
	 * Tworzy i zwraca obiekt menu przyciskiem uruchamiaj¹cym animacjê.
	 */
	private JPanel getBTreeMenu () {
		JPanel BTreeMenu = new JPanel ();
		
		BTreeMenu.setBackground (Color.WHITE);
		BTreeMenu.setBorder (BorderFactory.createTitledBorder ("Degree"));
		BTreeMenu.setLayout (new GridLayout (1, 2, 10, 10));
		
		JLabel DegreeL	= new JLabel ("Tree degree");
		Degree			= new JFormattedTextField (Format);
		Degree.setValue (new Integer (2));
		Degree.addPropertyChangeListener ("value", Listener);
		
		BTreeMenu.add (DegreeL);
		BTreeMenu.add (Degree);
		
		return BTreeMenu;
	}
	
	/**
	 * Tworzy i zwraca obiekt wyœwietlania statystyk.
	 */
	private JPanel getStats () {
		JPanel Stats = new JPanel ();
		
		Stats.setBackground (Color.WHITE);
		Stats.setBorder (BorderFactory.createTitledBorder ("Statistics"));
		Stats.setLayout (new GridLayout (5, 4, 20, 20));
		
		Stats.add (new JLabel ());
		Stats.add (new JLabel ("Maximum"));
		Stats.add (new JLabel ("Average"));
		
		Stats.add (new JLabel ("Comparisions during insertion:"));
		MaxInsertionComparisions = new JLabel ("---");
		Stats.add (MaxInsertionComparisions);
		AverageInsertionComparisions = new JLabel ("---");
		Stats.add (AverageInsertionComparisions);
		
		Stats.add (new JLabel ("Comparisions during deletion:"));
		MaxDeletionComparisions = new JLabel ("---");
		Stats.add (MaxDeletionComparisions);
		AverageDeletionComparisions = new JLabel ("---");
		Stats.add (AverageDeletionComparisions);
		
		Stats.add (new JLabel ("Disc access during insertion:"));
		MaxInsertionDiskAccess = new JLabel ("---");
		Stats.add (MaxInsertionDiskAccess);
		AverageInsertionDiskAccess = new JLabel ("---");
		Stats.add (AverageInsertionDiskAccess);
		
		Stats.add (new JLabel ("Disk access during deletion:"));
		MaxDeletionDiskAccess = new JLabel ("---");
		Stats.add (MaxDeletionDiskAccess);
		AverageDeletionDiskAccess = new JLabel ("---");
		Stats.add (AverageDeletionDiskAccess);
		
		return Stats;
	}
	
	/**
	 * Blokuje/odblokowuje wszystkie pola (np. na czas wykonywania animacji).
	 */
	public synchronized void lockFields (boolean lock) {
		Run.setEnabled (!lock);
		Delay.setEnabled (!lock);
		Generator.setEnabled (!lock);
		Seed.setEnabled (!lock);
		Size.setEnabled (!lock);
		Step.setEnabled (!lock);
	}
	
	/**
	 * Uruchamia okno animacji grafu, traktuj¹c okno g³ówne jako osobny w¹tek.
	 */
	public void run () {
		startup ();
	}
	
	/**
	 * Tworzy okno z animacj¹ lub wyœwietla okno dialogowe z komunikatem o b³êdzie. 
	 */
	private void startup () {
		lockFields (true);
		
		Controller.Degree	= ((Number) Degree.getValue ()).intValue ();
		Controller.Delay	= ((Number) Delay.getValue ()).intValue ();
		Controller.Seed		= ((Number) Seed.getValue ()).intValue ();
		Controller.Size		= ((Number) Size.getValue ()).intValue ();
		
		String Method = (String) Generator.getSelectedItem ();
		if (Method.equals ("ascending"))
			Controller.GenerateMethod = 1;
		else if (Method.equals ("descending"))
			Controller.GenerateMethod = 2;
		else
			Controller.GenerateMethod = 0;
		
		String Step = (String) this.Step.getSelectedItem ();
		if (Step.equals ("each action"))
			Controller.Step = 1;
		else
			Controller.Step = 0;
		
		Controller.startAnimation ();
	}
	
	/**
	 * Klasa wewnêtrzna obs³uguj¹ca zdarzenia.
	 */
	private class MenuListener extends WindowAdapter implements ActionListener, PropertyChangeListener {
		public void actionPerformed (ActionEvent event) {
			if (event.getSource () == Run)
				Controller.startup ();
		}
		
		public void propertyChange (PropertyChangeEvent event) {
			Object Source = event.getSource ();
			
			try {
				int Value;
				if (Source == Degree) {
					Value = ((Number) Degree.getValue ()).intValue ();
					if (Value < 0)
						Value *= -1;
					if (Value < 2)
						Value = 2;
					Degree.setValue (new Integer (Value));
					Controller.Degree = Value;
				} else if (Source == Delay) {
					Value = ((Number) Delay.getValue ()).intValue ();
					if (Value < 0)
						Value *= -1;						
					Delay.setValue (new Integer (Value));
					Controller.Delay = Value;
				} else if (Source == Seed)
					Controller.Seed = ((Number) Seed.getValue ()).intValue ();
				else if (Source == Size) {
					Value = ((Number) Size.getValue ()).intValue ();
					if (Value < 0)
						Value *= -1;
					else if (Value == 0)
						Value = 1;
					Size.setValue (new Integer (Value));
					Controller.Size = Value;
				}
			} catch (Exception ex) {}
		}
		
		public void windowClosing (WindowEvent event) {
			System.exit(0);
		}
	}
}
