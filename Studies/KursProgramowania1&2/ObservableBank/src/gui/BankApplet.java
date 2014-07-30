package gui;

import javax.swing.JPanel;
import javax.swing.JApplet;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import bank.Bank;

public class BankApplet extends JApplet {
	private static final long serialVersionUID = 481242481365255072L;
	
	private Bank			Bank;
	private BankTableModel	TableModel;
	
	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JTextField NewUser = null;
	private JScrollPane UsersList = null;
	private JTable Users = null;
	private JButton Add = null;
	private JTextField NewCommand = null;
	private JButton Send = null;
	private JCheckBox Inform = null;
	private JLabel InformLabel = null;
	private JLabel NewUserLabel = null;
	private JLabel InfomationLabel = null;

	/**
	 * This is the xxx default constructor
	 */
	public BankApplet() {
		super();
		Bank = new Bank ();
		TableModel = new BankTableModel (Bank, new Object[] {"Username", "Inform?", "Last information"});
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void init() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 1;
			InfomationLabel = new JLabel();
			InfomationLabel.setText("New info");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 0;
			NewUserLabel = new JLabel();
			NewUserLabel.setText("New user");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 2;
			gridBagConstraints5.gridy = 0;
			InformLabel = new JLabel();
			InformLabel.setText("Inform?");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 3;
			gridBagConstraints4.gridy = 0;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 4;
			gridBagConstraints31.gridy = 1;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = GridBagConstraints.BOTH;
			gridBagConstraints21.gridy = 1;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.gridwidth = 3;
			gridBagConstraints21.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 4;
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 2;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.gridwidth = 5;
			gridBagConstraints2.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridheight = -1;
			gridBagConstraints1.gridx = 3;
			gridBagConstraints1.gridy = 3;
			gridBagConstraints1.gridwidth = -1;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getJPanel(), gridBagConstraints1);
			jContentPane.add(getNewUser(), gridBagConstraints);
			jContentPane.add(getUsersList(), gridBagConstraints2);
			jContentPane.add(getAdd(), gridBagConstraints3);
			jContentPane.add(getNewCommand(), gridBagConstraints21);
			jContentPane.add(getSend(), gridBagConstraints31);
			jContentPane.add(getInform(), gridBagConstraints4);
			jContentPane.add(InformLabel, gridBagConstraints5);
			jContentPane.add(NewUserLabel, gridBagConstraints6);
			jContentPane.add(InfomationLabel, gridBagConstraints7);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
		}
		return jPanel;
	}

	/**
	 * This method initializes NewUser	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNewUser() {
		if (NewUser == null) {
			NewUser = new JTextField();
		}
		return NewUser;
	}

	/**
	 * This method initializes UsersList	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getUsersList() {
		if (UsersList == null) {
			UsersList = new JScrollPane();
			UsersList.setViewportView(getUsers());
		}
		return UsersList;
	}

	/**
	 * This method initializes Users	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getUsers() {
		if (Users == null) {
			Users = new JTable(TableModel);
			Users.setEnabled (false);
		}
		return Users;
	}

	/**
	 * This method initializes Add	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAdd() {
		if (Add == null) {
			Add = new JButton();
			Add.setText("Add");
			Add.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					TableModel.addUser (NewUser.getText (), Inform.isSelected ());
					//System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
				}
			});
		}
		return Add;
	}

	/**
	 * This method initializes NewCommand	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNewCommand() {
		if (NewCommand == null) {
			NewCommand = new JTextField();
		}
		return NewCommand;
	}

	/**
	 * This method initializes Send	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSend() {
		if (Send == null) {
			Send = new JButton();
			Send.setText("Send");
			Send.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Bank.newCommand ((String) NewCommand.getText ());
					Users.repaint ();
				}
			});
		}
		return Send;
	}

	/**
	 * This method initializes Inform	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getInform() {
		if (Inform == null) {
			Inform = new JCheckBox();
		}
		return Inform;
	}

}
