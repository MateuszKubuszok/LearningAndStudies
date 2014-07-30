package database;

import java.util.Vector;

public final class Course implements DBTableRecord  {
	public				String	Name;
	private	transient	String	Representant;
	
	public Course () {
		Name = "";
		Representant = "";
	}
	
	public Course (String name) {
		Name = name;
		Representant = "";
	}
	
	public Vector<Object> getColumnsNames () {
		Vector<Object> ColumnNames = new Vector<Object> ();
		ColumnNames.add ("Name");
		return ColumnNames;
	}
	
	public String getName () {
		return Name;
	}
	
	public String getRepresentant () {
		if (!Representant.equals (Name))
			Representant = Name;
		return Representant;
	}
	
	public Vector<Object> getRow () {
		Vector<Object> Row = new Vector<Object> ();
		Row.add (Name);
		return Row;
	}
	
	public void setName (String name) {
		Name = name;
	}
	
	public void setRow (Vector<Object> newRow) {
		try {
			Name = (String) newRow.get (0);
		} catch (Exception ex) {
			System.out.println ("some error");
		}
	}
}
