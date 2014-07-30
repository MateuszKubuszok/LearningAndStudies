package database;

import java.util.Vector;

public final class Exam implements DBTableRecord {
	public	double	Mark;
	public	Course	Course;
	public	Student	Student;
	
	public Exam () {
		Course	= null;
		Mark	= 0.0;
		Student = null;
	}
	
	public Exam (Course course, Student student) {
		Course	= course;
		Mark	= 0.0;
		Student	= student;
	}
	
	public Exam (Course course, Student student, double mark) {
		Course	= course;
		Mark	= mark;
		Student	= student;
	}
	
	public Vector<Object> getColumnsNames () {
		Vector<Object> ColumnNames = new Vector<Object> ();
		ColumnNames.add ("Course");
		ColumnNames.add ("Student");
		ColumnNames.add ("Mark");
		return ColumnNames;
	}
	
	public Course getCourse () {
		return Course;
	}
	
	public double getMark () {
		return Mark;
	}
	
	public Vector<Object> getPossibleValues () {
		Vector<Object> ToReturn = new Vector<Object> ();
		
		ToReturn.add (0.0);
		ToReturn.add (2.0);
		ToReturn.add (3.0);
		ToReturn.add (3.5);
		ToReturn.add (4.0);
		ToReturn.add (4.5);
		ToReturn.add (5.0);
		ToReturn.add (5.5);
		
		return ToReturn;
	}
	
	public Object getRepresentant () {
		return "";
	}
	
	public Vector<Object> getRow () {
		Vector<Object> Row = new Vector<Object> ();
		Row.add (Course != null ? Course.getRepresentant () : null);
		Row.add (Student != null ? Student.getRepresentant () : null);
		Row.add (Mark);
		return Row;
	}
	
	public Student getStudent () {
		return Student;
	}
	
	public void setCourse (Course course) {
		Course = course;
	}
	
	public void setMark (double mark) {
		Mark = mark;
	}
	
	public void setStudent (Student student) {
		Student = student;
	}

	public void setRow (Vector<Object> newRow) {
		try {
			Course	= (Course) newRow.get (0);
		} catch (Exception ex) {
			System.out.println ("Course cast error");
			return;
		}
		try {
			Student	= (Student) newRow.get (1);
		} catch (Exception ex) {
			System.out.println ("Student cast error");
			return;
		}
		try {
			Mark	=  Double.valueOf ((Double) (newRow.get (2)));
		} catch (Exception ex) {
			System.out.println ("Mark cast error");
			return;
		}
	}
}
