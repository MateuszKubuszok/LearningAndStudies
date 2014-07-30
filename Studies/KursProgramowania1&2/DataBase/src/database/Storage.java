package database;

import java.beans.*;
import java.io.*;
import java.util.HashSet;
import java.util.Observable;
import java.util.Vector;

public class Storage extends Observable {
	public HashSet<Course>	Courses;
	public HashSet<Student>	Students;
	public HashSet<Exam>	Exams;
	private Vector<Exam>	RemovedExams;
	
	public Storage () {
		Courses			= new HashSet<Course> ();
		Students		= new HashSet<Student> ();
		Exams			= new HashSet<Exam> ();
		RemovedExams	= new Vector<Exam> ();
	}
	
	public void addCourse (Course course) {
		Courses.add (course);
	}
	
	public void addExam (Exam exam) {
		Exams.add (exam);
	}
	
	public void addStudent (Student student) {
		Students.add (student);
	}
	
	public void clearRemovedExams () {
		RemovedExams.clear ();
	}
	
	public void deleteCourse (Course course) {
		Courses.remove (course);
	}
	
	public void deleteExam (Exam exam) {
		RemovedExams.add (exam);
		Exams.remove (exam);
	}
	
	public void deleteStudent (Student student) {
		Students.remove (student);
	}
	
	public Vector<Exam> getRemovedExams () {
		return RemovedExams;
	}
	
	@SuppressWarnings("unchecked")
	public void readData (String filename)
	throws IOException, BadDatabaseFileException {
		XMLDecoder Decoder =
			new XMLDecoder (
				new BufferedInputStream (
					new FileInputStream (filename)
				)
			);
		
		try {
			Courses		= (HashSet<Course>)		(Decoder.readObject ());
			Students	= (HashSet<Student>)	(Decoder.readObject ());
			Exams		= (HashSet<Exam>)		(Decoder.readObject ());
		} catch (ClassCastException ex) {
			throw new BadDatabaseFileException ();
		}
		
		setChanged ();
		notifyObservers ();
	}
	
	public void saveData (String filename) throws IOException {
		XMLEncoder Encoder =
			new XMLEncoder (
				new BufferedOutputStream (
					new FileOutputStream (filename)
				)
			);
		
		try {
			BeanInfo Info = Introspector.getBeanInfo (Course.class);
			PropertyDescriptor[] PropertyDescriptors = Info.getPropertyDescriptors ();
			for (int i = 0; i < PropertyDescriptors.length; i++) {
			    PropertyDescriptor pd = PropertyDescriptors [i];
			    if (pd.getName ().equals ("row"))
			        pd.setValue ("transient", Boolean.TRUE);
			}
		} catch (Exception ex) {}
		Encoder.writeObject (Courses);
		
		try {
			BeanInfo Info = Introspector.getBeanInfo (Student.class);
			PropertyDescriptor[] PropertyDescriptors = Info.getPropertyDescriptors ();
			for (int i = 0; i < PropertyDescriptors.length; i++) {
			    PropertyDescriptor pd = PropertyDescriptors [i];
			    if (pd.getName ().equals ("row"))
			        pd.setValue ("transient", Boolean.TRUE);
			}
		} catch (Exception ex) {}
		Encoder.writeObject (Students);
		
		try {
			BeanInfo Info = Introspector.getBeanInfo (Exam.class);
			PropertyDescriptor[] PropertyDescriptors = Info.getPropertyDescriptors ();
			for (int i = 0; i < PropertyDescriptors.length; i++) {
			    PropertyDescriptor pd = PropertyDescriptors [i];
			    if (pd.getName ().equals ("row"))
			        pd.setValue ("transient", Boolean.TRUE);
			}
		} catch (Exception ex) {}
		Encoder.writeObject (Exams);
		
		Encoder.close ();
	}
	
	public Vector<Object> searchCourses (String name) {
		Vector<Object> FoundRecords = new Vector<Object> ();
		
		if (name == null || name.length () == 0)
			return FoundRecords;
			
		for (Course Course : Courses)
			if (Course.Name.indexOf (name) > -1)
				FoundRecords.add (Course);
		
		return FoundRecords;
	}

	@SuppressWarnings("unchecked")
	public Vector<Object> searchExams (String courseName, String studentName, String studentAddress, double mark) {
		Vector<Object> Temp = new Vector<Object> ();
		
		if (courseName != null && courseName.length () > 0) {
			Vector<Object> FoundRecordsForCourse = new Vector<Object> ();
			for (Exam Exam : Exams)
				if (Exam.Course != null && Exam.Course.Name.indexOf (courseName) > -1)
					FoundRecordsForCourse.add (Exam);
			Temp.add (FoundRecordsForCourse);
		}
		
		if (studentName != null && studentName.length () > 0) {
			Vector<Object> FoundRecordsForStudent = new Vector<Object> ();
			for (Exam Exam : Exams)
				if (Exam.Student != null && Exam.Student.getRepresentant ().indexOf (studentName) > -1)
					FoundRecordsForStudent.add (Exam);
			Temp.add (FoundRecordsForStudent);
		}
		
		if (studentAddress != null && studentAddress.length () > 0) {
			Vector<Object> FoundRecordsForAddress = new Vector<Object> ();
			for (Exam Exam : Exams)
				if (Exam.Student != null && Exam.Student.Address.indexOf (studentAddress) > -1)
					FoundRecordsForAddress.add (Exam);
			Temp.add (FoundRecordsForAddress);
		}
		
		if (mark >= 0.0) {
			Vector<Object> FoundRecordsForMark = new Vector<Object> ();
			for (Exam Exam : Exams)
				if (Exam.Mark == mark)
					FoundRecordsForMark.add (Exam);
			Temp.add (FoundRecordsForMark);
		}
		
		switch (Temp.size ()) {
			case 4:
				return	intersection (
						intersection ((Vector<Object>) Temp.get (0), (Vector<Object>) Temp.get (1)),
						intersection ((Vector<Object>) Temp.get (2), (Vector<Object>) Temp.get (3))
				);
			
			case 3:
				return	intersection (
						intersection ((Vector<Object>) Temp.get (0), (Vector<Object>) Temp.get (1)),
						(Vector<Object>) Temp.get (2)
				);
			
			case 2:
				return intersection ((Vector<Object>) Temp.get (0), (Vector<Object>) Temp.get (1));
			
			case 1:
				return (Vector<Object>) Temp.get (0);
			
			default:
				return Temp;
		}
	}
	
	public Vector<Object> searchStudents (String name, String address) {
		Vector<Object> FoundRecords = new Vector<Object> ();
		
		if ((name == null || name.length () == 0) && (address == null || address.length () == 0))
			return FoundRecords;
		
		if (address == null || address.length () == 0) {
			for (Student Student : Students)
				if (Student.Address.indexOf (address) > -1)
					FoundRecords.add (Student);
		} else if (name == null || name.length () == 0) {
			for (Student Student : Students)
				if (Student.Address.indexOf (name) > -1)
					FoundRecords.add (Student);
		} else {
			for (Student Student : Students)
				if (Student.getRepresentant ().indexOf (name) > -1 && Student.Address.indexOf (address) > -1)
					FoundRecords.add (Student);
		}
		
		return FoundRecords;
	}
	
	@SuppressWarnings("unchecked")
	private Vector<Object> intersection (Vector<Object> vector1, Vector<Object> vector2) {
		if (vector1 == null || vector2 == null)
			return null;
		
		Vector<Object>	ToReturn = (Vector<Object>) vector1.clone (),
						Helper = (Vector<Object>) vector2.clone ();
		
		for(int i = ToReturn.size() - 1; i > -1; --i){
		    Object obj = ToReturn.get (i);
		    if(!Helper.remove (obj))
		        ToReturn.remove(obj);
		}
		
		return ToReturn;
	}
}
