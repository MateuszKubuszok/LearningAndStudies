package gui;

import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import controller.Controller;
import javax.swing.JScrollPane;

public class BotWindow {
	private Controller Controller;
	
	private JFrame jFrame = null;
	private JPanel jContentPane = null;
	private JLabel jLabel = null;
	private JTextField Address = null;
	private JLabel jLabel1 = null;
	private JTextField Depth = null;
	private JButton Search = null;
	private JTextArea Results = null;
	private JLabel jLabel2 = null;
	private JTextField Pattern = null;

	private JScrollPane jScrollPane = null;

	private JButton Stop = null;
	
	public BotWindow () {}
	
	public BotWindow (Controller ctrl) {
		Controller = ctrl;
	}
	
	public void locked (boolean locked) {
		getAddress ().setEnabled (!locked);
		getPattern ().setEnabled (!locked);
		getDepth ().setEnabled (!locked);
		getSearch ().setEnabled (!locked);
		getStop ().setEnabled (locked);
	}
	
	public Printer getPrinter () {
		return new Printer (Results);
	}
	
	/**
	 * This method initializes jFrame
	 * 
	 * @return javax.swing.JFrame
	 */
	public JFrame getJFrame() {
		if (jFrame == null) {			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setSize(640, 480);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("Internet Wanderer");
		}
		return jFrame;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 3;
			gridBagConstraints11.gridy = 2;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.BOTH;
			gridBagConstraints6.gridy = 3;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.weighty = 1.0;
			gridBagConstraints6.gridwidth = 5;
			gridBagConstraints6.gridx = 0;
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.fill = GridBagConstraints.BOTH;
			gridBagConstraints51.gridy = 1;
			gridBagConstraints51.weightx = 1.0;
			gridBagConstraints51.gridwidth = 3;
			gridBagConstraints51.gridx = 1;
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.gridx = 0;
			gridBagConstraints41.gridy = 1;
			jLabel2 = new JLabel();
			jLabel2.setText("Pattern");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 2;
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridy = 2;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.gridy = 2;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 2;
			jLabel1 = new JLabel();
			jLabel1.setText("Depth");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.gridwidth = 3;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Address");
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(jLabel, gridBagConstraints);
			jContentPane.add(getAddress(), gridBagConstraints1);
			jContentPane.add(jLabel1, gridBagConstraints2);
			jContentPane.add(getDepth(), gridBagConstraints3);
			jContentPane.add(getSearch(), gridBagConstraints4);
			jContentPane.add(jLabel2, gridBagConstraints41);
			jContentPane.add(getPattern(), gridBagConstraints51);
			jContentPane.add(getJScrollPane(), gridBagConstraints6);
			jContentPane.add(getStop(), gridBagConstraints11);
		}
		return jContentPane;
	}

	/**
	 * This method initializes Address	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getAddress() {
		if (Address == null) {
			Address = new JTextField();
		}
		return Address;
	}

	/**
	 * This method initializes Depth	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDepth() {
		if (Depth == null) {
			Depth = new JTextField();
		}
		return Depth;
	}

	/**
	 * This method initializes Search	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSearch() {
		if (Search == null) {
			Search = new JButton();
			Search.setText("Search");
			Search.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (Controller != null)
						Controller.search (
							getAddress ().getText (),
							getPattern ().getText (),
							getDepth ().getText ()
						);
				}
			});
		}
		return Search;
	}

	/**
	 * This method initializes Results	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getResults() {
		if (Results == null) {
			Results = new JTextArea();
			Results.setEditable (false);
		}
		return Results;
	}

	/**
	 * This method initializes Pattern	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getPattern() {
		if (Pattern == null) {
			Pattern = new JTextField();
		}
		return Pattern;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getResults());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes Stop	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getStop() {
		if (Stop == null) {
			Stop = new JButton();
			Stop.setText("Stop");
			Stop.setEnabled(false);
			Stop.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Controller.stop ();
				}
			});
		}
		return Stop;
	}

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				BotWindow application = new BotWindow();
				application.getJFrame().setVisible(true);
			}
		});
	}

}
