package db_gui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

import controller.Controller;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

@SuppressWarnings("serial")
public class DBPanel extends JApplet {
	public JMenuBar	MenuBar;
	public JMenu	File;
	public JMenu	Courses;
	public JMenu	Students;
	public JMenu	Exams;
	public JMenu	Find;
	
	public	JTabbedPane Content;
	
	public DBPanel() {
		super();
		Controller.initialize (this);
	}

	public void init () {
		makeMenu ();
		
		Content = new JTabbedPane ();	
		add (Content);
		
		setSize (600, 450);
	}
	
	public void start () {
		makeTab ();
	}
	
	public DBTab getCurrentTab () {
		return (DBTab) (Content.getSelectedComponent ());
	}
	
	public int getCurrentTabIndex () {
		return Content.getSelectedIndex ();
	}

	private void makeMenu () {
		MenuBar = new JMenuBar ();
		
		File		= new JMenu ("File");
		Courses		= new JMenu ("Courses");
		Students	= new JMenu ("Students");
		Exams		= new JMenu ("Exams");
		Find		= new JMenu ("Find");
		
		MenuBar.add (File);
		MenuBar.add (Courses);
		MenuBar.add (Students);
		MenuBar.add (Exams);
		MenuBar.add (Find);
		
		JMenuItem	FileNew		= new JMenuItem ("New"),
					FileSave	= new JMenuItem ("Save"),
					FileSaveAs	= new JMenuItem ("Save as"),
					FileRead	= new JMenuItem ("Read"),
					FileExit	= new JMenuItem ("Exit"),
					CoursesAdd	= new JMenuItem ("Add course"),
					StudentsAdd	= new JMenuItem ("Add student"),
					ExamsAdd	= new JMenuItem ("Add exam"),
					FindCourse	= new JMenuItem ("course"),
					FindStudent	= new JMenuItem ("student"),
					FindExam	= new JMenuItem ("exam");
		
		FileNew.addActionListener (new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent e) {
				System.out.println ("save");
				Controller.pressedFileNew ();
			}
		});
		FileSave.addActionListener (new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent e) {
				System.out.println ("save");
				Controller.pressedFileSave ();
			}
		});
		FileSaveAs.addActionListener (new java.awt.event.ActionListener () {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.out.println("save as");
				Controller.pressedFileSaveAs ();
			}
		});
		FileRead.addActionListener (new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent e) {
				System.out.println ("read");
				Controller.pressedFileRead ();
			}
		});
		FileExit.addActionListener (new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent e) {
				System.out.println ("exit");
				Controller.pressedFileExit ();
			}
		});
		File.add (FileNew);
		File.add (FileSave);
		File.add (FileSaveAs);
		File.add (FileRead);
		File.add (FileExit);
		
		CoursesAdd.addActionListener (new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent e) {
				System.out.println ("add course");
				Controller.pressedCoursesAdd ();
			}
		});
		StudentsAdd.addActionListener(new java.awt.event.ActionListener () {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.out.println("add student");
				Controller.pressedStudentsAdd ();
			}
		});
		ExamsAdd.addActionListener(new java.awt.event.ActionListener () {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.out.println("add exam");
				Controller.pressedExamsAdd ();
			}
		});
		Courses.add		(CoursesAdd);
		Students.add	(StudentsAdd);
		Exams.add		(ExamsAdd);
		
		FindCourse.addActionListener(new java.awt.event.ActionListener () {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.out.println("find course");
				Controller.pressedFindCourse ();
			}
		});
		FindStudent.addActionListener(new java.awt.event.ActionListener () {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.out.println("find student");
				Controller.pressedFindStudent ();
			}
		});
		FindExam.addActionListener(new java.awt.event.ActionListener () {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.out.println("find exam");
				Controller.pressedFindExam ();
			}
		});
		Find.add (FindCourse);
		Find.add (FindStudent);
		Find.add (FindExam);
		
		setJMenuBar (MenuBar);
	}
	
	public void makeTab () {
		DBTab Tab = new DBTab (Controller.getNewDB (), Content);
		Tab.setLayout (new CardLayout());
		Content.addTab ("unnamed", Tab);
		
		int Index = Content.indexOfComponent (Tab);
		if (Index > -1) {
			DBTabLabel Label = new DBTabLabel ();
			Content.setTabComponentAt (Index, Label);
			Tab.setLabel (Label);
		}
	}
	
	class DBTabLabel extends JPanel  {
		JLabel Title;
		
		public DBTabLabel () {
			super (new FlowLayout(FlowLayout.LEFT, 0, 0));
			setOpaque (false);
			
	        if (Content == null)
	            throw new NullPointerException ("TabbedPane is null");
			
	        Title = new JLabel () {
				public String getText() {
		            int i = Content.indexOfTabComponent (DBTabLabel.this);
		            if (i != -1) 
		                return Content.getTitleAt (i);
		            return null;
		        }
			};
			
			add (Title);
	        //add more space between the label and the button
	        Title.setBorder (BorderFactory.createEmptyBorder (0, 0, 0, 5));
	        //tab button
	        JButton Button = new DBTabButton ();
	        add (Button);
	        //add more space to the top of the component
	        setBorder (BorderFactory.createEmptyBorder (2, 0, 0, 0));
		}
		
		public void refreshName () {
			Title.getText ();
			Title.repaint ();
		}
	
		private class DBTabButton extends JButton {
			public DBTabButton() {
				setText ("x");
	            int size = 17;
	            setPreferredSize (new Dimension (size, size));
	            setToolTipText ("close");
	            //Make the button looks the same for all Laf's
	            setUI (new BasicButtonUI ());
	            //Make it transparent
	            setContentAreaFilled (false);
	            //No need to be focusable
	            setFocusable (false);
	            setBorder (BorderFactory.createEtchedBorder());
	            setBorderPainted (false);
	            //Making nice rollover effect
	            //we use the same listener for all buttons
	            addMouseListener (new MouseAdapter () {
	            	public void mouseEntered(MouseEvent e) {
	                    Component component = e.getComponent ();
	                    if (component instanceof AbstractButton) {
	                        AbstractButton button = (AbstractButton) component;
	                        button.setBorderPainted (true);
	                    }
	                }
	
	                public void mouseExited(MouseEvent e) {
	                    Component component = e.getComponent();
	                    if (component instanceof AbstractButton) {
	                        AbstractButton button = (AbstractButton) component;
	                        button.setBorderPainted(false);
	                    }
	                }
	            });
	            setRolloverEnabled(true);
	            //Close the proper tab by clicking the button
	            addActionListener (new ActionListener () {
	            	public void actionPerformed(ActionEvent e) {
	                    int i = Content.indexOfTabComponent (DBTabLabel.this);
	                    if (i != -1) {
	                    	((DBTab) Content.getComponentAt (i)).discarded ();
	                        Content.remove (i);
	                    }
	                }
	            });
	        }
		}
	}
}
