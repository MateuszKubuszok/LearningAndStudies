package connection_gui;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import controller.Controller;

public class ConnectionWindow extends JFrame {
	private Controller Controller;
	
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel MakeHost = null;
	private JPanel ConnectToHost = null;
	private JLabel MKHostPortLabel = null;
	private JTextField MKHostPortTextField = null;
	private JButton MKHostButton = null;
	private JLabel CNServerLabel = null;
	private JTextField CNServerTextField = null;
	private JLabel CNPortLabel = null;
	private JTextField CNPortTextField = null;
	private JButton CNButton = null;
	/**
	 * This is the default constructor
	 */
	public ConnectionWindow() {
		super();
		initialize();
	}
	
	public ConnectionWindow (Controller controller) {
		super();
		Controller = controller;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(286, 192);
		this.setContentPane(getJContentPane());
		this.setTitle("Connection");
		this.setResizable(false);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.ipadx = 103;
			gridBagConstraints3.ipady = -11;
			gridBagConstraints3.gridwidth = 3;
			gridBagConstraints3.gridy = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.ipadx = 79;
			gridBagConstraints2.ipady = 20;
			gridBagConstraints2.gridwidth = 2;
			gridBagConstraints2.gridy = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getMakeHost(), gridBagConstraints2);
			jContentPane.add(getConnectToHost(), gridBagConstraints3);
		}
		return jContentPane;
	}

	/**
	 * This method initializes MakeHost	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMakeHost() {
		if (MakeHost == null) {
			GridLayout gridLayout2 = new GridLayout();
			gridLayout2.setRows(2);
			gridLayout2.setVgap(5);
			gridLayout2.setHgap(5);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			MKHostPortLabel = new JLabel();
			MKHostPortLabel.setText("Open port #");
			MakeHost = new JPanel();
			MakeHost.setLayout(gridLayout2);
			MakeHost.add(MKHostPortLabel, null);
			MakeHost.add(getMKHostPortTextField(), null);
			MakeHost.add(getMKHostButton(), null);
		}
		return MakeHost;
	}

	/**
	 * This method initializes ConnectToHost	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getConnectToHost() {
		if (ConnectToHost == null) {
			CNPortLabel = new JLabel();
			CNPortLabel.setText("Port #");
			CNServerLabel = new JLabel();
			CNServerLabel.setText("Server name");
			GridLayout gridLayout1 = new GridLayout();
			gridLayout1.setRows(3);
			gridLayout1.setHgap(5);
			gridLayout1.setVgap(5);
			gridLayout1.setColumns(2);
			ConnectToHost = new JPanel();
			ConnectToHost.setLayout(gridLayout1);
			ConnectToHost.add(CNServerLabel, null);
			ConnectToHost.add(getCNServerTextField(), null);
			ConnectToHost.add(CNPortLabel, null);
			ConnectToHost.add(getCNPortTextField(), null);
			ConnectToHost.add(getCNButton(), null);
		}
		return ConnectToHost;
	}

	/**
	 * This method initializes MKHostPortTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getMKHostPortTextField() {
		if (MKHostPortTextField == null) {
			MKHostPortTextField = new JTextField();
		}
		return MKHostPortTextField;
	}

	/**
	 * This method initializes MKHostButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getMKHostButton() {
		if (MKHostButton == null) {
			MKHostButton = new JButton();
			MKHostButton.setText("Make host");
			MKHostButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("makeHost");
					if (Controller != null)
						Controller.createHost (MKHostPortTextField.getText ());
				}
			});
		}
		return MKHostButton;
	}

	/**
	 * This method initializes CNServerTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCNServerTextField() {
		if (CNServerTextField == null) {
			CNServerTextField = new JTextField();
		}
		return CNServerTextField;
	}

	/**
	 * This method initializes CNPortTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCNPortTextField() {
		if (CNPortTextField == null) {
			CNPortTextField = new JTextField();
		}
		return CNPortTextField;
	}

	/**
	 * This method initializes CNButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCNButton() {
		if (CNButton == null) {
			CNButton = new JButton();
			CNButton.setText("Connect");
			CNButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("connectTo");
					if (Controller != null)
						Controller.connectToHost (CNServerTextField.getText (), CNPortTextField.getText ());
				}
			});
		}
		return CNButton;
	}

}  //  @jve:decl-index=0:visual-constraint="136,42"
