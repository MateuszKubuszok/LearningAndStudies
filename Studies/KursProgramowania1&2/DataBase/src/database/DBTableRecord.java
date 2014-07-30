package database;

import java.util.Vector;

public interface DBTableRecord {
	public Vector<Object> getColumnsNames ();
	public Object getRepresentant ();
	public Vector<Object> getRow ();
	public void setRow (Vector<Object> newRow);
}
