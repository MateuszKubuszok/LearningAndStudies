package database;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public class StorageTableBinder implements Observer {
	private	Storage					Database;
	private DBTableModel			Model;
	private Vector<RecordRowPair>	Pairs;
	private Vector<Object>			Representants;
	private RecordType				Type;
	
	// U¿ywane do synchronizacji z innymi Binderami
	private StorageTableBinder		Courses;
	private StorageTableBinder		Exams;
	private StorageTableBinder		Students;
	
	public StorageTableBinder (Storage database, RecordType type) {
		Database 		= database;
		Type			= type;
		Pairs			= new Vector <RecordRowPair> ();
		if (Type != RecordType.Exams)
			Representants	= new Vector <Object> ();
		
		Database.addObserver (this);
		
		switch (Type) {
			case Courses:
				Model = new DBTableModel (this, new Course ().getColumnsNames ());
			break;
	
			case Exams:
				Model = new DBTableModel (this, new Exam ().getColumnsNames ());
			break;
			
			case Students:
				Model = new DBTableModel (this, new Student ().getColumnsNames ());				
			break;
			
			default:
				throw new IllegalArgumentException ("No type given!");
		}
	}
	
	public synchronized void addRecord () {
		DBTableRecord Record;
		switch (Type) {
			case Courses:
				Record = new Course ();
			break;

			case Exams:
				Record = new Exam ();	
			break;
			
			case Students:
				Record = new Student ();				
			break;
			
			default:
				return;
		}
		addRecord (Record);
	}
	
	public synchronized void addRecord (Vector<Object> row) {
		DBTableRecord Record;
		switch (Type) {
			case Courses:
				Record = new Course ();
				Record.setRow (row);
			break;

			case Exams:
				Record = new Exam ();	
				Record.setRow (makeSetForExam (row));
			break;
			
			case Students:
				Record = new Student ();				
				Record.setRow (row);
			break;
			
			default:
				return;
		}
		
		addRecord (Record, row);
	}
	
	public synchronized void addRecord (DBTableRecord course) {
		addRecord (course, course.getRow ());
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void addRecord (DBTableRecord record, Vector<Object> row) {
		switch (Type) {
			case Courses:
				Database.addCourse ((Course) record);
			break;

			case Exams:
				Database.addExam ((Exam) record);
			break;
			
			case Students:
				Database.addStudent ((Student) record);				
			break;
			
			default:
				return;
		}
		Model.addRow (row);
		Pairs.add (new RecordRowPair (record, (Vector<Object>) Model.getDataVector ().lastElement ()));
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void deleteRecordsForDeletedRows () {
		Vector<RecordRowPair> Copy = (Vector<RecordRowPair>) Pairs.clone ();
		
		switch (Type) {
			case Courses:
				for (Object Removed : Model.getRemoved ())
					for (RecordRowPair Pair : Copy)
						if (Pair.Row == Removed) {
							for (Exam Exam : (HashSet<Exam>) Database.Exams.clone ())
								if (Exam.Course == Pair.Record)
									Database.deleteExam (Exam);
							
							Database.deleteCourse ((Course) Pair.Record);
							Representants.remove (Pair.Representant);
							Pairs.remove (Pair);
						}
				Model.clearRemoved ();
				
				for (Exam Exam : (Vector<Exam>) Database.getRemovedExams ().clone ()) {
					RecordRowPair	Pair = Exams.getPairForRecord (Exam);
					Vector<Object>	Rows = (Vector<Object>) Exams.getModel ().getDataVector ().clone ();
					for (int i = Rows.size ()-1; i >= 0; i--) {
						if (Pair.Row == Rows.get (i))
							Exams.getModel ().removeRow (i);
					}
				}
				Database.clearRemovedExams ();
			break;
			
			case Students:
				for (Object Removed : Model.getRemoved ())
					for (RecordRowPair Pair : Copy)
						if (Pair.Row == Removed) {
							for (Exam Exam : (HashSet<Exam>) Database.Exams.clone ())
								if (Exam.Student == Pair.Record)
									Database.deleteExam (Exam);
							
							Database.deleteStudent ((Student) Pair.Record);
							Representants.remove (Pair.Representant);
							Pairs.remove (Pair);
						}
				Model.clearRemoved ();

				for (Exam Exam : (Vector<Exam>) Database.getRemovedExams ().clone ()) {
					RecordRowPair	Pair = Exams.getPairForRecord (Exam);
					Vector<Object>	Rows = (Vector<Object>) Exams.getModel ().getDataVector ().clone ();
					for (int i = Rows.size ()-1; i >= 0; i--) {
						if (Pair.Row == Rows.get (i))
							Exams.getModel ().removeRow (i);
					}
				}
				Database.clearRemovedExams ();
			break;
			
			case Exams:
				for (Object Removed : Model.getRemoved ())
					for (RecordRowPair Pair : Copy)
						if (Pair.Row == Removed) {
							Database.deleteExam ((Exam) Pair.Record);
							Pairs.remove (Pair);
						}
				Model.clearRemoved ();
			break;
			
			default:
				return;
		}
	}
	
	public DBTableModel getModel () {
		return Model;
	}
	
	private Vector<RecordRowPair> getPairs () {
		return Pairs;
	}
	
	public Vector<Object> getRepresentants () {
		return Representants;
	}
	
	private RecordRowPair getPairForRecord (DBTableRecord record) {
		for (RecordRowPair Pair : Pairs)
			if (Pair.Record == record)
				return Pair;
		return null;
	}
	
	private RecordRowPair getPairForRepresentant (String representant) {
		for (RecordRowPair Pair : Pairs)
			if (Pair.Representant == representant)
				return Pair;
		return null;
	}
	
	private RecordRowPair getPairForRow (Vector<Object> row) {
		for (RecordRowPair Pair : Pairs)
			if (Pair.Row == row)
				return Pair;
		return null;
	}

	private synchronized RecordRowPair getPairForRowIndex (int index) {
		if (index <= -1)
			return null;
		
		Object Row = Model.getDataVector ().get (index);
		for (RecordRowPair Pair : Pairs)
			if (Pair.Row == Row)
				return Pair;
		return null;
	}
	
	public Vector<Object> searchRowsForRecords (Vector<Object> records) {
		Vector<Object> FoundRows = new Vector<Object> ();
		
		if (records == null || records.size () == 0)
			return FoundRows;
		
		for (RecordRowPair Pair : Pairs)
			for (Object Record : records)
				if (Pair.Record == Record)
					FoundRows.add (Pair.Row);
		
		return FoundRows;
	}
	
	public void setCoursesStorage (StorageTableBinder courses) {
		Courses = courses;
	}
	
	public void setExamsStorage (StorageTableBinder exams) {
		Exams = exams;
	}
	
	public void setStudentsStorage (StorageTableBinder students) {
		Students = students;
	}
	
	public synchronized void updateRecord (Vector<Object> row) {
		try {
			RecordRowPair Pair = getPairForRow (row);
			if (Type == RecordType.Exams) {
				Pair.Record.setRow (makeSetForExam (row));
			} else {
				Pair.Record.setRow (row);
				updateRepresentantForPair (Pair);
			}
		} catch (NullPointerException ex) {}
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void updateRecordForRowIndex (int index) {
		try {
			RecordRowPair Pair = getPairForRowIndex (index);
			if (Type == RecordType.Exams) {
				Pair.Record.setRow (makeSetForExam ((Vector<Object>) Model.getDataVector ().get (index)));
			} else {
				Pair.Record.setRow ((Vector<Object>) (Model.getDataVector ().get (index)));
				updateRepresentantForPair (Pair);
			}
		} catch (NullPointerException ex) {}
	}
	
	public synchronized void updateRow (DBTableRecord record) {
		try {
			RecordRowPair Pair = getPairForRecord (record);
			Pair.Row.clear ();
			Pair.Row.addAll (record.getRow ());
			if (Type != RecordType.Exams)
				updateRepresentantForPair (Pair);
		} catch (NullPointerException ex) {}
	}
	
	/*
	 * Used at reading database from file
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public synchronized void update (Observable object, Object updated) {
		// used at reading from file
		Pairs.clear ();
		if (Type != RecordType.Exams)
			Representants.clear ();
		
		Vector<Object> NewContent = new Vector<Object> ();
		Vector<Object> NewHeaders = null;
		switch (Type) {
			case Courses:
				for (Course Course : Database.Courses) {
					NewContent.add (Course.getRow ());
					Pairs.add (
						new RecordRowPair (
							Course,
							(Vector<Object>) (NewContent.lastElement ())
						)
					);
				}
				NewHeaders = new Course ().getColumnsNames ();
				Model.setDataVector (NewContent, NewHeaders);
			break;
			
			case Exams:
				Model.getDataVector ().clear ();
				for (Exam Exam : Database.Exams) {
					Vector<Object> Row = Exam.getRow ();
					Pairs.add (
						new RecordRowPair (
							Exam,
							Row
						)
					);
					Model.getDataVector ().add (Row);
				}
				NewHeaders = new Exam ().getColumnsNames ();
			break;
				
			case Students:
				for (Student Student : Database.Students) {
					NewContent.add (Student.getRow ());
					Pairs.add (
							new RecordRowPair (
								Student,
								(Vector<Object>) (NewContent.lastElement ())
							)
						);
				}
				NewHeaders = new Student ().getColumnsNames ();
				Model.setDataVector (NewContent, NewHeaders);
			break;
			
			default:
				return;
		}
		System.out.println ("Read data into GUI");
	}
	
	private void updateRepresentantForPair (RecordRowPair pair) {
		if (Type == RecordType.Exams)
			return;
		
		int		Index = Representants.indexOf (pair.Representant);
		Object	NewRepresentant = pair.Record.getRepresentant ();		
				
		if (Index > -1) {
			String					CurrentRepresentant;
			Vector<RecordRowPair>	Pairs = Exams.getPairs ();
			
			switch (Type) {
				case Courses:	
					for (RecordRowPair Pair : Pairs) {
						CurrentRepresentant = (String) (Pair.Row.get (0)); 
						if (pair.Representant == CurrentRepresentant && !"".equals (CurrentRepresentant))
							Pair.Row.set (0, NewRepresentant);
					}
				break;
					
				case Students:
					for (RecordRowPair Pair : Pairs) {
						CurrentRepresentant = (String) (Pair.Row.get (1)); 
						if (pair.Representant == CurrentRepresentant && !"".equals (CurrentRepresentant))
							Pair.Row.set (1, NewRepresentant);
					}
				break;
			}
			
			Representants.set (
				Index,
				NewRepresentant
			);
		}
		
		pair.Representant = NewRepresentant;
	}
	
	private Vector<Object> makeSetForExam (Vector<Object> row) {
		Vector<Object> NewData = new Vector<Object> ();
		
		RecordRowPair NewCoursePair = Courses.getPairForRepresentant ((String) row.get (0)); 
		RecordRowPair NewStudentPair = Students.getPairForRepresentant ((String) row.get (1));
		
		NewData.add (NewCoursePair != null ? NewCoursePair.Record : null);
		NewData.add (NewStudentPair != null ? NewStudentPair.Record : null);
		NewData.add (row.get (2));
		
		return NewData;
	}
	
	public class RecordRowPair {
		DBTableRecord	Record;
		Vector<Object>	Row;
		Object			Representant;
		
		public RecordRowPair (DBTableRecord record, Vector<Object> row) {
			Record			= record;
			Row				= row;
			Representant	= Record.getRepresentant ();
			
			if (Type != RecordType.Exams)
				Representants.add (Representant);
		}
	}
	
	public enum RecordType {
		Courses,
		Exams,
		Students
	}
}
