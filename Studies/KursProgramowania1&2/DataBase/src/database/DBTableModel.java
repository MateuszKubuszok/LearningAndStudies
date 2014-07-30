package database;

import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class DBTableModel extends DefaultTableModel {
	private StorageTableBinder	Binder;
	private Vector<Object>		LatelyRemoved;
	
	public DBTableModel (StorageTableBinder binder, Vector<Object> columnsNames) {
		super (null, columnsNames);
		Binder = binder;
		LatelyRemoved = new Vector<Object> ();
		addTableModelListener (new DBTableListener ());
	}
	
	public void clearRemoved () {
		LatelyRemoved.clear (); 
	}
	
	public Vector<Object> getRemoved () {
		return LatelyRemoved;
	}
	
	public void removeRow (int row) {
		LatelyRemoved.add (getDataVector ().get (row));
		super.removeRow (row);
		System.out.print ("");
	}
	
	public void setValueAt (Object value, int row, int column) {
		try {
			super.setValueAt (value, row, column);
		} catch (Exception ex) {}
	}
	
	private class DBTableListener implements TableModelListener {
		public void tableChanged (TableModelEvent event) {
			switch (event.getType ()) {
				case TableModelEvent.DELETE:
					eventDelete (event);
					System.out.println ("delete");
				break;
				
				case TableModelEvent.INSERT:
					System.out.println ("insert");
				break;
				
				case TableModelEvent.UPDATE:
					eventUpdate (event);
					System.out.println ("modified");
				break;
			}
		}
		
		private void eventDelete (TableModelEvent event) {
			Binder.deleteRecordsForDeletedRows ();
		}
		
		private void eventUpdate (TableModelEvent event) {
			int i = event.getFirstRow ();
			
			if (i <= -1)
				return;
			
			while (i <= event.getLastRow ())
				Binder.updateRecordForRowIndex (i++);
		}
	}
}
