package gui;

import java.util.Observable;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import bank.Bank; 

public class BankTableModel extends DefaultTableModel {
	private static final long serialVersionUID = 3397951502672441277L;

	private Bank Bank;
	
	public BankTableModel (Bank bank) {
		Bank = bank;
	}
	
	public BankTableModel (Bank bank, Object[] objects) {
		super (null, objects);
		Bank = bank;
	}
	
	public void addUser (String username, boolean add) {
		Vector<Object> Row = new Vector<Object> ();
		
		new DefaultTableModel ();
		
		Row.add (username);
		Row.add (add ? "yes" : "no");
		Row.add ("");
		
		if (add)
			Bank.addObserver (new BankObserver (Row));
		
		addRow (Row);
	}
	
	public class BankObserver implements java.util.Observer {
		public Vector<Object> Row;
		
		BankObserver (Vector<Object> row) {
			Row = row;
		}
		
		public void update (Observable observable, Object changedProperty) {
			Row.set (2, changedProperty);
		}
	}
}
