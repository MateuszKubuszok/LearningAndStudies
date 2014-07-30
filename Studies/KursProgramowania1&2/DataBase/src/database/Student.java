package database;

import java.util.Vector;

public final class Student implements DBTableRecord {
	public	String				Name;
	public	String				Surname;
	public	String				Address;
	private	transient String	Representant;
	
	public Student () {
		Address	= "";
		Name	= "";
		Surname	= "";
		Representant = "";
	}
	
	public Student (String name, String surname, String address) {
		Address	= address;
		Name	= name;
		Surname	= surname;
		Representant = "";
		Representant = getRepresentant ();
	}
	
	public String getAddress () {
		return Address;
	}
	
	public Vector<Object> getColumnsNames () {
		Vector<Object> ColumnNames = new Vector<Object> ();
		ColumnNames.add ("Surame");
		ColumnNames.add ("Name");
		ColumnNames.add ("Address");
		return ColumnNames;
	}
	
	public String getName () {
		return Name;
	}
	
	public String getRepresentant () {
		if (!Representant.equals (Surname + " " + Name))
			Representant = Surname + " " + Name;
		return Representant;
	}
	
	public Vector<Object> getRow () {
		Vector<Object> Row = new Vector<Object> ();
		Row.add (Surname);
		Row.add (Name);
		Row.add (Address);
		return Row;
	}
	
	public String getSurname () {
		return Surname;
	}
	
	public void setAddress (String address) {
		Address = address;
	}
	
	public void setName (String name) {
		Name = name;
	}
	
	public void setSurname (String surname) {
		Surname = surname;
	}

	public void setRow (Vector<Object> newRow) {
		try {
			Surname	= (String) newRow.get (0);
			Name	= (String) newRow.get (1);
			Address	= (String) newRow.get (2);
		} catch (Exception ex) {
			System.out.println ("some error");
		}
	}
}
