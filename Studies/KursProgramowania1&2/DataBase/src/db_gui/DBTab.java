package db_gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import database.*;
import db_gui.DBPanel.DBTabLabel;

@SuppressWarnings("serial")
public class DBTab extends JPanel {
	private Storage		Database;
	private String		Filename;  //  @jve:decl-index=0:
	private JTabbedPane	Owner;
	private	JTabbedPane	Content;
	
	private JTable				Courses;
	private StorageTableBinder	CoursesBinder;
	private JPanel				CoursesTab;
	
	private JTable				Students;
	private StorageTableBinder	StudentsBinder;
	private JPanel				StudentsTab;
	
	private JTable				Exams;
	private StorageTableBinder	ExamsBinder;
	private JPanel				ExamsTab;
	private JComboBox			CoursesList;
	private JComboBox			MarksList;
	private JComboBox			StudentsList;
	
	private DBTabLabel			Label;
	private String				Title;
	
	private SearchCoursesWindow		SearchCoursesWindow;
	private SearchExamsWindow		SearchExamsWindow;
	private SearchStudentsWindow	SearchStudentsWindow;
	
	public DBTab() {
		super();
		initialize();
	}
	
	public DBTab (Storage database, JTabbedPane owner) {
		super ();
		Database = database;
		Owner = owner;
		initialize ();
	}
	
	public void addNewCourse () {
		CoursesBinder.addRecord ();
	}
	
	public void addNewCourse (Course course) {
		CoursesBinder.addRecord (course);
	}
	
	public void addNewExams () {
		ExamsBinder.addRecord ();
	}
	
	public void addNewExams (Exam exam) {
		ExamsBinder.addRecord (exam);
	}
	
	public void addNewStudent () {
		StudentsBinder.addRecord ();
	}
	
	public void addNewStudent (Student student) {
		StudentsBinder.addRecord (student);
	}
	
	public void discarded () {
		if (SearchCoursesWindow != null)
			SearchCoursesWindow.setVisible (false);
		if (SearchExamsWindow != null)
			SearchExamsWindow.setVisible (false);
		if (SearchStudentsWindow != null)
			SearchStudentsWindow.setVisible (false);
	}
	
	public Storage getDatabase () {
		return Database;
	}
	
	public String getFilename () {
		return Filename;
	}
	
	public void openSearchCoursesWindow () {
		if (SearchCoursesWindow == null)
			SearchCoursesWindow = new SearchCoursesWindow ();
		SearchCoursesWindow.setTitle ("Search courses in "+(Title != null ? Title : "unnamed"));
		SearchCoursesWindow.setVisible (true);
	}
	
	public void openSearchExamsWindow () {
		if (SearchExamsWindow == null)
			SearchExamsWindow = new SearchExamsWindow ();
		SearchExamsWindow.setTitle ("Search exams in "+(Title != null ? Title : "unnamed"));
		SearchExamsWindow.setVisible (true);
	}
	
	public void openSearchStudentsWindow () {
		if (SearchStudentsWindow == null)
			SearchStudentsWindow = new SearchStudentsWindow ();
		SearchStudentsWindow.setTitle ("Search students in "+(Title != null ? Title : "unnamed"));
		SearchStudentsWindow.setVisible (true);
	}
	
	public void setCurrentTabOnCourses () {
		Content.setSelectedComponent (CoursesTab);
	}
	
	public void setCurrentTabOnExams () {
		Content.setSelectedComponent (ExamsTab);
	}
	
	public void setCurrentTabOnStudents () {
		Content.setSelectedComponent (StudentsTab);
	}
	
	public void setDatabase (Storage database) {
		Database = database;
	}
	
	public void setFilename (String filename) {
		Filename = filename;
	}
	
	public void setLabel (DBTabLabel label) {
		Label = label;
	}
	
	public void setTitle (String title) {
		Title = title;
		
		int Index = Owner.indexOfComponent (this);
		
		if (Index <= -1)
			return;
		
		Owner.setTitleAt (Index, title);
		Label.refreshName ();
	}
	
	
	private void addCoursesTab () {
		CoursesBinder = new StorageTableBinder (Database, StorageTableBinder.RecordType.Courses);
		CoursesBinder.addRecord ();
		
		CoursesTab = new JPanel ();
		JPanel	ButtonsBar	= new JPanel (); 
		
		ButtonsBar.add (new ToolButton ("add", new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				CoursesBinder.addRecord ();
			}	
		}));
		ButtonsBar.add (new ToolButton ("delete", new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				int[] Rows = Courses.getSelectedRows (); 
				for (int i = Rows.length-1; i >= 0; i--)
					((DBTableModel) (Courses.getModel ())).removeRow (Rows [i]);
			}	
		}));
		
		Courses				= new JTable (CoursesBinder.getModel ());
		JScrollPane Panel	= new JScrollPane (Courses);
		
		CoursesTab.setLayout (new BoxLayout (CoursesTab, BoxLayout.PAGE_AXIS));
		CoursesTab.add (Panel);
		CoursesTab.add (ButtonsBar);
		
		Content.insertTab ("Courses", null, CoursesTab, "", 0);
	}
	
	private void addExamTab () {
		ExamsBinder = new StorageTableBinder (Database, StorageTableBinder.RecordType.Exams);
		ExamsBinder.setCoursesStorage (CoursesBinder);
		ExamsBinder.setStudentsStorage (StudentsBinder);
		ExamsBinder.addRecord ();
		
		CoursesBinder.setExamsStorage (ExamsBinder);
		StudentsBinder.setExamsStorage (ExamsBinder);
		
		ExamsTab = new JPanel ();
		JPanel	ButtonsBar	= new JPanel (); 
		
		ButtonsBar.add (new ToolButton ("add", new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				ExamsBinder.addRecord ();
			}	
		}));
		ButtonsBar.add (new ToolButton ("delete", new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				int[] Rows = Exams.getSelectedRows (); 
				for (int i = Rows.length-1; i >= 0; i--)
					((DBTableModel) (Exams.getModel ())).removeRow (Rows [i]);
			}	
		}));
				
		Exams				= new JTable (ExamsBinder.getModel ());
		JScrollPane Panel	= new JScrollPane (Exams);
		
		CoursesList		= new JComboBox (new DefaultComboBoxModel (CoursesBinder.getRepresentants ()));
		Exams.getColumnModel ().getColumn (0).setCellEditor (new DefaultCellEditor (CoursesList));
		StudentsList	= new JComboBox (new DefaultComboBoxModel (StudentsBinder.getRepresentants ()));
		Exams.getColumnModel ().getColumn (1).setCellEditor (new DefaultCellEditor (StudentsList));
		MarksList		= new JComboBox (new DefaultComboBoxModel (new Exam ().getPossibleValues ()));		
		Exams.getColumnModel ().getColumn (2).setCellEditor (new DefaultCellEditor (MarksList));
		
		ExamsTab.setLayout (new BoxLayout (ExamsTab, BoxLayout.PAGE_AXIS));
		ExamsTab.add (Panel);
		ExamsTab.add (ButtonsBar);
		
		Content.insertTab ("Exams", null, ExamsTab, "", 2);
	}
	
	private void addStudentsTab () {
		StudentsBinder = new StorageTableBinder (Database, StorageTableBinder.RecordType.Students);
		StudentsBinder.addRecord ();
		
		StudentsTab= new JPanel ();
		JPanel	ButtonsBar	= new JPanel (); 
		
		ButtonsBar.add (new ToolButton ("add", new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				StudentsBinder.addRecord ();
			}	
		}));
		ButtonsBar.add (new ToolButton ("delete", new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				int[] Rows = Students.getSelectedRows (); 
				for (int i = Rows.length-1; i >= 0; i--)
					((DBTableModel) (Students.getModel ())).removeRow (Rows [i]);
			}	
		}));
		
		Students			= new JTable (StudentsBinder.getModel ());
		JScrollPane Panel	= new JScrollPane (Students);
		
		StudentsTab.setLayout (new BoxLayout (StudentsTab, BoxLayout.PAGE_AXIS));
		StudentsTab.add (Panel);
		StudentsTab.add (ButtonsBar);
		
		Content.insertTab ("Students", null, StudentsTab, "", 1);
	}
	
	private void initialize() {
		Content = new JTabbedPane ();
		add (Content);
		
		addCoursesTab ();
		addStudentsTab ();
		addExamTab ();
		
		addHierarchyBoundsListener (new HierarchyBoundsListener () {
			public void ancestorMoved (HierarchyEvent arg0) {}
			public void ancestorResized (HierarchyEvent arg0) {
				if (Owner != null) {
					Dimension Size = Owner.getSize ();
					setSize ((int) Size.getWidth () - 50, (int) Size.getHeight () - 50);
				}
			}
			
		});
		setLayout (new GridBagLayout ());
	}
	
	private class SearchCoursesWindow extends JFrame {
		private JTextField	CourseName;
		private JButton		SearchButton;
		
		SearchCoursesWindow () {
			CourseName		= new JTextField ();
			SearchButton	= new JButton ("Search");
			JLabel Label	= new JLabel ("Course's name");
			
			GroupLayout Layout = new GroupLayout (rootPane);
			rootPane.setLayout (Layout);

			Layout.setAutoCreateGaps (true);
			Layout.setAutoCreateContainerGaps (true);
			
			Layout.setHorizontalGroup (
				Layout.createSequentialGroup ()
					.addComponent (Label)
					.addGroup (Layout.createParallelGroup (GroupLayout.Alignment.LEADING)
						.addComponent (CourseName)
						.addComponent (SearchButton)
					)
			);
			Layout.setVerticalGroup (
				Layout.createSequentialGroup ()
					.addGroup (Layout.createParallelGroup (GroupLayout.Alignment.BASELINE)
						.addComponent (Label)
						.addComponent (CourseName)
					)
					.addComponent (SearchButton)
			);
			
			SearchButton.addActionListener (new ActionListener () {
				@SuppressWarnings("unchecked")
				public void actionPerformed (ActionEvent event) {
					Vector<Object> Records = Database.searchCourses (CourseName.getText ());
					Vector<Object> Rows = CoursesBinder.searchRowsForRecords (Records);
					
					ListSelectionModel Model = Courses.getSelectionModel ();
					Vector<Object> AllRows = ((DBTableModel) Courses.getModel ()).getDataVector ();
					
					setCurrentTabOnCourses ();
					Model.clearSelection ();
					for (Object Row : Rows) {
						int Index = AllRows.indexOf (Row);
						Model.addSelectionInterval (Index, Index);
					}
					System.out.println (CourseName.getText ());
				}
			});
			
			setSize (450, 100);
			setResizable (false);
		}
	}
	
	private class SearchExamsWindow extends JFrame {
		private JTextField	CourseName;
		private JTextField	StudentName;
		private JTextField	StudentAddress;
		private JComboBox	Mark;
		private JButton		SearchButton;
		
		SearchExamsWindow () {
			Vector<Object> PossibleMarks = new Vector<Object> ();
			PossibleMarks.add (0, "");
			PossibleMarks.addAll (new Exam ().getPossibleValues ());
			
			CourseName		= new JTextField ();
			StudentName		= new JTextField ();
			StudentAddress	= new JTextField ();
			Mark			= new JComboBox (new DefaultComboBoxModel (PossibleMarks));
			SearchButton	= new JButton ("Search");
			JLabel Course	= new JLabel ("Course's name");
			JLabel Student	= new JLabel ("Student's name");
			JLabel Address	= new JLabel ("Student's address");
			JLabel MarkL	= new JLabel ("Mark");
			
			GroupLayout Layout = new GroupLayout (rootPane);
			rootPane.setLayout (Layout);

			Layout.setAutoCreateGaps (true);
			Layout.setAutoCreateContainerGaps (true);
			
			Layout.setHorizontalGroup (
				Layout.createSequentialGroup ()
					.addGroup (Layout.createParallelGroup (GroupLayout.Alignment.LEADING)
						.addComponent (Course)
						.addComponent (Student)
						.addComponent (Address)
						.addComponent (MarkL)
					)
					.addGroup (Layout.createParallelGroup (GroupLayout.Alignment.LEADING)
						.addComponent (CourseName)
						.addComponent (StudentName)
						.addComponent (StudentAddress)
						.addComponent (Mark)
						.addComponent (SearchButton)
					)
			);
			Layout.setVerticalGroup (
				Layout.createSequentialGroup ()
					.addGroup (Layout.createParallelGroup (GroupLayout.Alignment.BASELINE)
						.addComponent (Course)
						.addComponent (CourseName)
					)
					.addGroup (Layout.createParallelGroup (GroupLayout.Alignment.BASELINE)
						.addComponent (Student)
						.addComponent (StudentName)
					)
					.addGroup (Layout.createParallelGroup (GroupLayout.Alignment.BASELINE)
						.addComponent (Address)
						.addComponent (StudentAddress)
					)
					.addGroup (Layout.createParallelGroup (GroupLayout.Alignment.BASELINE)
						.addComponent (Mark)
						.addComponent (MarkL)
					)
					.addComponent (SearchButton)
			);
			
			SearchButton.addActionListener (new ActionListener () {
				@SuppressWarnings("unchecked")
				public void actionPerformed (ActionEvent event) {
					double MarkInt;
					try {
						MarkInt = Double.valueOf ((Double) Mark.getSelectedItem ());
					} catch (Exception ex) {
						MarkInt = -1.0;
					}
					Vector<Object> Records = Database.searchExams (
						CourseName.getText (),
						StudentName.getText (),
						StudentAddress.getText (),
						MarkInt
					);
					Vector<Object> Rows = ExamsBinder.searchRowsForRecords (Records);
					
					ListSelectionModel Model = Exams.getSelectionModel ();
					Vector<Object> AllRows = ((DBTableModel) Exams.getModel ()).getDataVector ();
					
					setCurrentTabOnExams ();
					Model.clearSelection ();
					for (Object Row : Rows) {
						int Index = AllRows.indexOf (Row);
						Model.addSelectionInterval (Index, Index);
					}
					//System.out.println (CourseName.getText ());
				}
			});
			
			setSize (450, 200);
			setResizable (false);
		}
	}
	
	private class SearchStudentsWindow extends JFrame {
		private JTextField	StudentName;
		private JTextField	StudentAddress;
		private JButton		SearchButton;
		
		SearchStudentsWindow () {
			StudentName		= new JTextField ();
			StudentAddress	= new JTextField ();
			SearchButton	= new JButton ("Search");
			JLabel Name		= new JLabel ("Student's name");
			JLabel Address	= new JLabel ("Student's address");
			
			GroupLayout Layout = new GroupLayout (rootPane);
			rootPane.setLayout (Layout);

			Layout.setAutoCreateGaps (true);
			Layout.setAutoCreateContainerGaps (true);
			
			Layout.setHorizontalGroup (
				Layout.createSequentialGroup ()
					.addGroup (Layout.createParallelGroup (GroupLayout.Alignment.LEADING)
						.addComponent (Name)
						.addComponent (Address)
					)
					.addGroup (Layout.createParallelGroup (GroupLayout.Alignment.LEADING)
						.addComponent (StudentName)
						.addComponent (StudentAddress)
						.addComponent (SearchButton)
					)
			);
			Layout.setVerticalGroup (
				Layout.createSequentialGroup ()
					.addGroup (Layout.createParallelGroup (GroupLayout.Alignment.BASELINE)
						.addComponent (Name)
						.addComponent (StudentName)
					)
					.addGroup (Layout.createParallelGroup (GroupLayout.Alignment.BASELINE)
						.addComponent (Address)
						.addComponent (StudentAddress)
					)
					.addComponent (SearchButton)
			);
			
			SearchButton.addActionListener (new ActionListener () {
				@SuppressWarnings("unchecked")
				public void actionPerformed (ActionEvent event) {
					Vector<Object> Records = Database.searchStudents (StudentName.getText (), StudentAddress.getText ());
					Vector<Object> Rows = StudentsBinder.searchRowsForRecords (Records);
					
					ListSelectionModel Model = Students.getSelectionModel ();
					Vector<Object> AllRows = ((DBTableModel) Students.getModel ()).getDataVector ();
					
					setCurrentTabOnStudents ();
					Model.clearSelection ();
					for (Object Row : Rows) {
						int Index = AllRows.indexOf (Row);
						Model.addSelectionInterval (Index, Index);
					}
					System.out.println (StudentName.getText ());
				}
			});
			
			setSize (450, 150);
			setResizable (false);
		}
	}
	
	private class ToolButton extends JButton {
		ToolButton (String name, ActionListener listener) {
			super (name);
			addActionListener (listener);
		}
	}
}
