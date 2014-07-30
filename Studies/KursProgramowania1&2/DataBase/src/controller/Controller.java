package controller;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import database.BadDatabaseFileException;
import database.Storage;
import db_gui.DBPanel;
import db_gui.DBTab;

public class Controller {
	private static boolean	Initialized = false;
	private static DBPanel	Panel;
	private static final JFileChooser FileChooser = new JFileChooser ();
	
	public static void initialize (DBPanel panel) {
		if (Initialized)
			return;
		
		Panel = panel;
		
		Initialized = true;
	}
	
	public static Storage getNewDB () {
		return new Storage ();
	}
	
	public static void pressedFileNew () {
		Panel.makeTab ();
	}
	public static void pressedFileSave () {
		if (Panel.getCurrentTabIndex () <= -1)
			return;
		
		Storage Database = Panel.getCurrentTab ().getDatabase ();
		String	Filename = Panel.getCurrentTab ().getFilename ();
		
		if (Filename == null)
			pressedFileSaveAs ();
		else {
			try {
				Database.saveData (Filename);
				System.out.println ("Saved to file");
			} catch (IOException ex) {
				JOptionPane.showMessageDialog (Panel.getCurrentTab (), "Program was unable to save the file.");
				System.out.println ("save fail");
			}
		}
	}
	public static void pressedFileSaveAs () {
		if (Panel.getCurrentTabIndex () <= -1)
			return;
		
		DBTab	Tab = Panel.getCurrentTab ();
		
		if (FileChooser.showSaveDialog (Tab) == JFileChooser.APPROVE_OPTION) {
			File File = FileChooser.getSelectedFile ();
			Tab.setFilename (File.getPath ());
			Tab.setTitle (File.getName ());
			System.out.println ("File set to: " + Tab.getFilename ());
			pressedFileSave ();
		} else {
			System.out.println ("save cancelled");
		}
	}
	public static void pressedFileRead () {
		if (FileChooser.showOpenDialog (Panel) == JFileChooser.APPROVE_OPTION) {
			Panel.makeTab ();
			if (Panel.getCurrentTabIndex () <= -1)
				return;
			
			DBTab Tab = (DBTab) Panel.getCurrentTab ();
			
			Storage Database = Tab.getDatabase ();
			File File = FileChooser.getSelectedFile ();
			
			try {
				Database.readData (File.getPath ());
				Tab.setTitle (File.getName ());
				Tab.setFilename (File.getPath ());
				System.out.println ("Read file");
			} catch (IOException ex) {
				JOptionPane.showMessageDialog (Tab, "Program was unable to open the file.");
				Panel.remove (Tab);
				System.out.println ("read fail");
			} catch (BadDatabaseFileException ex) {
				JOptionPane.showMessageDialog (Tab, "File is corrupted - program was unable to open it.");
				Panel.remove (Tab);
				System.out.println ("parse fail");
			}
		} else {
			System.out.println ("read cancelled");
		}
	}
	public static void pressedFileExit () {
		System.exit (0);
	}
	public static void pressedCoursesAdd () {
		if (Panel.getCurrentTabIndex () <= -1)
			return;
		
		DBTab Tab = Panel.getCurrentTab ();
		
		Tab.setCurrentTabOnCourses ();
		Tab.addNewCourse ();
	}
	public static void pressedStudentsAdd () {
		if (Panel.getCurrentTabIndex () <= -1)
			return;
		
		DBTab Tab = Panel.getCurrentTab ();
		
		Tab.setCurrentTabOnStudents ();
		Tab.addNewStudent ();
	}
	public static void pressedExamsAdd () {
		if (Panel.getCurrentTabIndex () <= -1)
			return;
		
		DBTab Tab = Panel.getCurrentTab ();
		
		Tab.setCurrentTabOnExams ();
		Tab.addNewExams ();
	}
	public static void pressedFindCourse () {
		if (Panel.getCurrentTabIndex () <= -1)
			return;
		
		DBTab Tab = Panel.getCurrentTab ();
		
		Tab.setCurrentTabOnCourses ();
		Tab.openSearchCoursesWindow ();
	}
	public static void pressedFindExam () {
		if (Panel.getCurrentTabIndex () <= -1)
			return;
		
		DBTab Tab = Panel.getCurrentTab ();
		
		Tab.setCurrentTabOnExams ();
		Tab.openSearchExamsWindow ();
	}
	public static void pressedFindStudent () {
		if (Panel.getCurrentTabIndex () <= -1)
			return;
		
		DBTab Tab = Panel.getCurrentTab ();
		
		Tab.setCurrentTabOnStudents ();
		Tab.openSearchStudentsWindow ();
	}
}
