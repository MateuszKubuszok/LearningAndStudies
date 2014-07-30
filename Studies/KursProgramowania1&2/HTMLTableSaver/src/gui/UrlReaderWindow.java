package gui;

import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Event;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.KeyStroke;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;

import controller.Controller;

public class UrlReaderWindow {	
	private Controller Controller;
	
	private JFrame jFrame = null;
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JTextField Url = null;
	private JButton SaveButton = null;

	private JButton SaveFile = null;
	
	public UrlReaderWindow () {}
	
	public UrlReaderWindow (Controller ctrl) {
		Controller = ctrl;
	}
	
	public void lock () {
		getSaveButton ().setEnabled (false);
		getUrl ().setEnabled (false);
	}
	
	public void unlock () {
		getSaveButton ().setEnabled (true);
		getUrl ().setEnabled (true);
	}
	
	/**
	 * This method initializes jFrame
	 * 
	 * @return javax.swing.JFrame
	 */
	public JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setJMenuBar(getJJMenuBar());
			jFrame.setSize(300, 200);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("URL Table Saver");
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
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 3;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.insets = new Insets(10, 0, 0, 0);
			gridBagConstraints.gridx = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getUrl(), gridBagConstraints);
			jContentPane.add(getSaveButton(), gridBagConstraints1);
			jContentPane.add(getSaveFile(), gridBagConstraints2);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes Url	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getUrl() {
		if (Url == null) {
			Url = new JTextField();
		}
		return Url;
	}

	/**
	 * This method initializes SaveButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSaveButton() {
		if (SaveButton == null) {
			SaveButton = new JButton();
			SaveButton.setText("Save URL's tables");
			SaveButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (Controller != null)
						Controller.saveUrl (Url.getText ());
				}
			});
		}
		return SaveButton;
	}

	/**
	 * This method initializes SaveFile	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSaveFile() {
		if (SaveFile == null) {
			SaveFile = new JButton();
			SaveFile.setText("Save file's tables");
			SaveFile.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Controller.saveFile ();
				}
			});
		}
		return SaveFile;
	}

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UrlReaderWindow application = new UrlReaderWindow();
				application.getJFrame().setVisible(true);
			}
		});
	}

}
