package controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import gui.UrlReaderWindow;
import Parser.TableParser;
import UrlReader.UrlReader;

public class Controller {
	private TableParser	Parser;
	
	private UrlReaderWindow Window;
	private JFileChooser FileChooser;
	
	public Controller () {
		Window = new UrlReaderWindow (this);
		Window.getJFrame ().setVisible (true);
	}
	
	public void saveFile () {
		try {
			Window.lock ();
			
			switch (getFileChooser ().showSaveDialog (Window.getJFrame ())) {
				case JFileChooser.APPROVE_OPTION:
					File File = getFileChooser ().getSelectedFile ();
					
					BufferedReader Reader = new BufferedReader (
						new InputStreamReader (
							new FileInputStream (File.getAbsoluteFile ())
						)
					);
					
					String	Result = "",
							Input;
			
					while ((Input = Reader.readLine ()) != null)
						Result += Input + "\n";
					Reader.close ();
					
					save (Result);
				break;
				
				case JFileChooser.CANCEL_OPTION:
				break;
				
				case JFileChooser.ERROR_OPTION:
					JOptionPane.showMessageDialog (Window.getJFrame (),
						"Error occured during opening.", 
						"Opening error",
						JOptionPane.ERROR_MESSAGE
					);
				break;
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog (Window.getJFrame (),
				"Program coundn't save tables on given page.", 
				"Saving error",
				JOptionPane.ERROR_MESSAGE
			);
		} finally {
			Window.unlock ();
		}
	}
	
	public void saveUrl (String url) {
		try {
			Window.lock ();
			
			save (UrlReader.readUrl (url));
		} catch (IOException e) {
			JOptionPane.showMessageDialog (Window.getJFrame (),
				"Program coundn't save tables on given page.", 
				"Saving error",
				JOptionPane.ERROR_MESSAGE
			);
		} finally {
			Window.unlock ();
		}
	}
	
	private void save (String site) {
			String[]	Result	= getParser ().parseHtml (site);
			
			if (Result.length == 0) {
				JOptionPane.showMessageDialog (Window.getJFrame (),
					"No correct tables was found on the page.", 
					"No tables",
					JOptionPane.INFORMATION_MESSAGE
				);
			} else
				switch (getFileChooser ().showSaveDialog (Window.getJFrame ())) {
					case JFileChooser.APPROVE_OPTION:
						File File = getFileChooser ().getSelectedFile ();
						
						String Name = File.getName ();
						Name = Name.endsWith (".csv") ? Name.substring (0, Name.length ()-4) : Name;
						
						try {
							saveFile (
								File.getParent () + System.getProperty ("file.separator") + Name + ".csv",
								Result [0]
							);
							for (int i = 1; i < Result.length; i++)
								saveFile (
										File.getParent () + System.getProperty ("file.separator") + Name + "(" + (i+1) + ").csv",
										Result [i]
								);
						} catch (IOException e) {
							JOptionPane.showMessageDialog (Window.getJFrame (),
								"Error occured during saving result into the file('s).", 
								"Saving error",
								JOptionPane.ERROR_MESSAGE
							);
						}
					break;
					
					case JFileChooser.CANCEL_OPTION:
					break;
					
					case JFileChooser.ERROR_OPTION:
						JOptionPane.showMessageDialog (Window.getJFrame (),
							"Error occured during saving.", 
							"Saving error",
							JOptionPane.ERROR_MESSAGE
						);
					break;
				}
		
	}
	
	private void saveFile (String path, String content)
	throws IOException {
		BufferedOutputStream Out = new BufferedOutputStream (new FileOutputStream (path));
		Out.write (content.getBytes ());
		Out.close ();
	}
	
	private JFileChooser getFileChooser () {
		if (FileChooser == null)
			FileChooser = new JFileChooser ();
		
		return FileChooser;
	}
	
	private TableParser getParser () {
		if (Parser == null)
			Parser = new TableParser ();
		
		return Parser;
	}
	
	public static void main (String[] args) {
		new Controller ();
	}
}
