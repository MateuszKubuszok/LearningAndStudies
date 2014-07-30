package client_controller;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import client.ChangedProperty;
import client.Client;
import client.ClientSettings;
import client.ErrorType;
import client.ServerStatus;
import client.UserSettings;
import data.ClientUser;
import data.Message;
import data.UserStatus;
import gui.*;

public class ClientController implements Observer {
	private Client Client;
	
	public	Display				Display;
	public	MainWindow 			MainWindow;
	
	public	AddUserWindow			AddUserWindow;	
	public	ChangeSettingsWindow	ChangeSettingsWindow;
	
	public	CreateUserWindow	CreationWindow;
	public	RegistrationWindow	RegistrationWindow;
	public	SelectUserWindow	SelectUser;
	
	public	Map<String,MessagingWindow>	MessagingWindows;
	
	private String	UsersPath;
	private String	SettingsPath;
	
	public ClientController () {
		SettingsPath = System.getProperty ("user.dir")+"\\settings";
		
		UsersPath = System.getProperty ("user.dir")+"\\users";
		
		ClientSettings Settings = readClientSettings ();
		saveClientSettings (Settings);
		
		Client = new Client (Settings);
		Client.addObserver (this);
		
		MessagingWindows = new TreeMap<String,MessagingWindow> ();
		
		Display = new Display ();
		new SelectUserWindow (this);
		
		Display.syncExec (new Runnable () {
			public void run () {
				Shell[] Shells;
				while (!Display.isDisposed ()) {
					Shells = Display.getShells ();
					boolean CanExit = true;
					for (int i = 0; i < Shells.length; ++i) {
						if (!Shells[i].isDisposed ()) {
							CanExit = false;
							break;
						}
					}
					if (CanExit)
						break;
					if (!Display.isDisposed () && !Display.readAndDispatch ())
						Display.sleep ();
			    }
				finnish ();
			}
		});
	}
	
	public void addNewUser (String username) {
		if (username.isEmpty ()) {
			MessageBox mb = new MessageBox (AddUserWindow.sShell, SWT.ICON_ERROR);
			mb.setText ("User creation error");
	        mb.setMessage ("Username cannot be empty!");
	        mb.open ();
	        return;
		}
		
		if (!AddUserWindow.sShell.isDisposed ())
			AddUserWindow.sShell.setVisible (false);
		
		addUserToBase (username);
	}
	
	public void deleteOldUser (ArrayList<String> usernames) {
		synchronized (Client.AllContacts) {
			Client.AllContacts.removeAll (usernames);
		}
		synchronized (Client.KnownContacts) {
			for (int i = Client.KnownContacts.size ()-1; i >= 0; i--)
				if (usernames.contains (Client.KnownContacts.get (i).Username))
					Client.KnownContacts.remove (i);
		}
		saveUserContacts ();
		refreshContacts ();
		Client.makeGetStatusesRequest ();
	}
	
	public void changePassword (String password, String repeatPassword) {
		if (password == null || password.isEmpty ()) {
			MessageBox mb = new MessageBox (ChangeSettingsWindow.sShell, SWT.ICON_ERROR);
			mb.setText ("Password change error");
	        mb.setMessage ("Password cannot be empty!");
	        mb.open ();
	        return;
		} else if (repeatPassword == null || repeatPassword.isEmpty ()) {
			MessageBox mb = new MessageBox (ChangeSettingsWindow.sShell, SWT.ICON_ERROR);
			mb.setText ("Password change error");
	        mb.setMessage ("Password must match!");
	        mb.open ();
	        return;
		}
		
		Client.makeChangePasswordRequest (password);
	}
	
	public void changeSettings (String resource, String password) {
		if (resource == null || resource.isEmpty ()) {
			MessageBox mb = new MessageBox (ChangeSettingsWindow.sShell, SWT.ICON_ERROR);
			mb.setText ("Settings change error");
	        mb.setMessage ("Resource cannot be empty!");
	        mb.open ();
	        return;
		} else if (resource == null || resource.isEmpty ()) {
			MessageBox mb = new MessageBox (ChangeSettingsWindow.sShell, SWT.ICON_ERROR);
			mb.setText ("Settings change error");
	        mb.setMessage ("Password cannot be empty!");
	        mb.open ();
	        return;
		}
		
		Client.makeLogoutRequest ();
		Client.executeRequests ();
		
		Client.Password = password;
		Client.Resource = resource;
		saveUserSettings ();

		Client.makeLoginRequest ();
		
		MessageBox mb = new MessageBox (ChangeSettingsWindow.sShell, SWT.ICON_INFORMATION);
		mb.setText ("Settings change");
        mb.setMessage ("Settings changed successfully!");
        mb.open ();
	}
	
	@SuppressWarnings("rawtypes")
	public void closeAllWindows () {
		if (!MessagingWindows.isEmpty ()) {
			synchronized (MessagingWindows) {
				Iterator Iterator = MessagingWindows.entrySet ().iterator ();
				
				while (Iterator.hasNext ()) {
					Map.Entry Pairs = (Map.Entry) Iterator.next ();
			        MessagingWindow Window = (MessagingWindow) Pairs.getValue ();
			        if (!Window.sShell.isDisposed ())
			        	Window.sShell.dispose ();
				}
			}
		}
		if (ChangeSettingsWindow != null && !ChangeSettingsWindow.sShell.isDisposed ())
			ChangeSettingsWindow.sShell.dispose ();
		if (MainWindow != null && !MainWindow.Shell.isDisposed ())
			MainWindow.Shell.dispose ();
	}
	
	public void createNewUser (String username, String password) {
		if (username.isEmpty ()) {
			MessageBox mb = new MessageBox (CreationWindow.sShell, SWT.ICON_ERROR);
			mb.setText ("User creation error");
	        mb.setMessage ("Username cannot be empty!");
	        mb.open ();
	        CreationWindow.setEnabled (true);
	        return;
		} else if (password.isEmpty ()) {
			MessageBox mb = new MessageBox (CreationWindow.sShell, SWT.ICON_ERROR);
			mb.setText ("User creation error");
	        mb.setMessage ("Password cannot be empty!");
	        mb.open ();
	        CreationWindow.setEnabled (true);
	        return;
		}
		
		makeUser (new UserSettings (username, password, "home"));
		if (!CreationWindow.sShell.isDisposed ())
			CreationWindow.sShell.dispose ();
		if (SelectUser != null)
			SelectUser.repaint ();
	}
	
	
	public String[] getUsers () {
		File Dir = new File (UsersPath);
		
		FilenameFilter filter = new FilenameFilter () {
		    public boolean accept (File dir, String name) {
		        return name.endsWith (".user");
		    }
		};
		
		String[] 	Files = Dir.list (filter),
		       		Users = new String[Files.length];
		
		for (int i = 0; i < Files.length; i++)
			Users [i] = Files [i].substring(0, Files [i].length ()-5);
		
		return Users;
	}
	
	public UserSettings getUserSettings () {
		return Client.getUserSettings ();
	}
	
	public void loadUser (String[] selected) {
		if (selected == null || selected.length == 0)
			return;
		UserSettings Settings = readUserSettings (selected [0]);
		if (Settings == null)
			return;
		
		Client.setUser (Settings);
		readUserContacts ();
		for (String Username : Client.AllContacts)
			Client.KnownContacts.add (new ClientUser (Username, UserStatus.Offline));
		Display.syncExec(new Runnable () {
			public void run () {
				SelectUser.sShell.dispose ();
			}
		});
		setStatusOnline ();
		new MainWindow (this);
	}
	
	public void openAddUserWindow () {
		if (AddUserWindow == null || AddUserWindow.sShell.isDisposed ())
			new AddUserWindow (this);
		else
			AddUserWindow.sShell.setVisible (false);
	}
	
	public void openChangeSettingsWindow () {
		if (ChangeSettingsWindow == null || ChangeSettingsWindow.sShell.isDisposed ())
			new ChangeSettingsWindow (this);
		else if (!Display.isDisposed ())
			Display.syncExec (new Runnable () {				
				public void run () {					
					ChangeSettingsWindow.sShell.setVisible (true);
				}
			});
	}
	
	public void openCreationWindow () {
		if (CreationWindow == null || CreationWindow.sShell.isDisposed ())
			new CreateUserWindow (this);
	}

	public void openExportContactsDialog () {
		if (!Display.isDisposed ())
			Display.syncExec(new Runnable () {
				public void run () {
					FileDialog Dialog = new FileDialog (MainWindow.Shell, SWT.SAVE);
					Dialog.setFilterNames (new String[] { "Contacts' lists (*.contacts)" });
					Dialog.setFilterExtensions (new String[] { "*.contacts" });
					saveUserContacts (Dialog.open ());
				}
			});
	}
	
	public void openImportContactsDialog () {
		if (!Display.isDisposed ())
			Display.syncExec(new Runnable () {
				public void run () {
					FileDialog Dialog = new FileDialog (MainWindow.Shell, SWT.OPEN);
					Dialog.setFilterNames (new String[] { "Contacts' lists (*.contacts)" });
					Dialog.setFilterExtensions (new String[] { "*.contacts" });
					readUserContacts (Dialog.open ());
					saveUserContacts ();
				}
			});
	}
	
	public void openMessagingWindow (final ClientUser user) {
		if (user == null || user.Username == null || user.Username.equals (Client.Username))
			return;
		else if (!MessagingWindows.containsKey (user.Username) || MessagingWindows.get (user.Username).sShell.isDisposed ()) {
			new MessagingWindow (this, user);
		} else if (!Display.isDisposed ())
			Display.syncExec (new Runnable () {
				public void run () {					
					MessagingWindows.get (user.Username).sShell.setVisible (true);
				}
			});
	}
	
	public void openMessagingWindow (String username) {
		synchronized (Client.KnownContacts) {
			for (ClientUser User : Client.KnownContacts)
				if (User.Username.equals (username)) {
					openMessagingWindow (User);
					return;
				}
			addUserToBase (username);
			openMessagingWindow (username);
		}
	}
	
	public void openMessagingWindows (ArrayList<String> usernames) {
		if (usernames != null)
			for (String Username : usernames)
				openMessagingWindow (Username);
	}
	
	public void openRegistrationWindow () {
		if (RegistrationWindow == null || RegistrationWindow.sShell.isDisposed ())
			new RegistrationWindow (this);
	}
	
	public void registerUser (String username, String password, String repeatedPassword) {
		if (username.isEmpty ()) {
			MessageBox mb = new MessageBox (RegistrationWindow.sShell, SWT.ICON_ERROR);
			mb.setText ("Registration error");
	        mb.setMessage ("Username cannot be empty!");
	        mb.open ();
	        RegistrationWindow.setEnabled (true);
	        return;
		} else if (password.isEmpty ()) {
			MessageBox mb = new MessageBox (RegistrationWindow.sShell, SWT.ICON_ERROR);
			mb.setText ("Registration error");
	        mb.setMessage ("Password cannot be empty!");
	        mb.open ();
	        RegistrationWindow.setEnabled (true);
	        return;
		} else if (!password.equals (repeatedPassword)) {
			MessageBox mb = new MessageBox (RegistrationWindow.sShell, SWT.ICON_ERROR);
			mb.setText ("Registration error");
	        mb.setMessage ("Passwords cannot differ!");
	        mb.open ();
	        RegistrationWindow.setEnabled (true);
	        return;
		}
		
		Client.setUser (
			username,
			password,
			(Client.Resource != null && !Client.Resource.isEmpty ()) ? Client.Resource : "home"
		);
		Client.makeRegisterRequest ();
	}
	
	public void refreshContacts () {
		if (MainWindow != null)
			MainWindow.updateContacts (Client.KnownContacts);
		if (MessagingWindows != null && !MessagingWindows.isEmpty () && !Display.isDisposed ()) {
			Display.syncExec (new Runnable () {
				public void run () {			
					synchronized (Client.KnownContacts) {
						for (ClientUser User : Client.KnownContacts)
							if (MessagingWindows.containsKey (User.Username)) {
								MessagingWindow Window = MessagingWindows.get (User.Username);
								if (!Window.sShell.isDisposed ())
									Window.sShell.setText (User.Username + " ("+User.State+")");
							}
						}
				}
			});
		}
	}
	
	public void sendMessage (String username, String message) {
		Client.makeSendMessageRequest (new Message (Client.Username, username, message));
	}
	
	public void setStatusAway () {
		if (Client.ServerStatus == ServerStatus.Disconnected)
			Client.makeLoginRequest ();
		Client.makeSetStatusRequest (UserStatus.Away);
	}
	
	public void setStatusInvisible () {
		if (Client.ServerStatus == ServerStatus.Disconnected)
			Client.makeLoginRequest ();
		Client.makeSetStatusRequest (UserStatus.Invisible);
	}
	
	public void setStatusOffline () {
		Client.makeLogoutRequest ();
	}
	
	public void setStatusOnline () {
		if (Client.ServerStatus == ServerStatus.Disconnected)
			Client.makeLoginRequest ();
		else
			Client.makeSetStatusRequest (UserStatus.Online);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void update (Observable observable, Object updated) {
		if (observable != Client)
			return;
		
		switch ((ChangedProperty) updated) {
			case ClientStatus:
				MainWindow.setStatus (Client.Status);
			break;
			
			case ContactsStatuses:
				refreshContacts ();
			break;
			
			case Errors:
				if (Client.Errors.containsKey (ErrorType.Connection) &&
					MainWindow.Shell != null &&
					!MainWindow.Shell.isDisposed ()) {
					
					Display.syncExec (new Runnable () {
						public void run () {
							MessageBox mb = new MessageBox (MainWindow.Shell, SWT.ICON_ERROR);
							mb.setText ("Connection error");
							mb.setMessage (Client.Errors.get (ErrorType.Connection));
							mb.open ();
						}
					});
				}
				if (Client.Errors.containsKey (ErrorType.Login) &&
					MainWindow.Shell != null &&
					!MainWindow.Shell.isDisposed ()) {
							
					Display.syncExec (new Runnable () {
						public void run () {
							MessageBox mb = new MessageBox (MainWindow.Shell, SWT.ICON_ERROR);
							mb.setText ("Connection error");
							mb.setMessage (Client.Errors.get (ErrorType.Login));
							mb.open ();
						}
					});
				}
				if (Client.Errors.containsKey (ErrorType.PasswordChange) &&
					MainWindow.Shell != null &&
					!MainWindow.Shell.isDisposed ()) {
					
					Display.syncExec (new Runnable () {
						public void run () {
							MessageBox mb = new MessageBox (MainWindow.Shell, SWT.ICON_ERROR);
							mb.setText ("Password change error");
							mb.setMessage (Client.Errors.get (ErrorType.PasswordChange));
							mb.open ();
						}
					});
				}
				if (Client.Errors.containsKey (ErrorType.Register) &&
					RegistrationWindow.sShell != null &&
					!RegistrationWindow.sShell.isDisposed ()) {
					
					Display.syncExec (new Runnable () {
						public void run () {
							MessageBox mb = new MessageBox (RegistrationWindow.sShell, SWT.ICON_ERROR);
							mb.setText ("Registration error");
							mb.setMessage (Client.Errors.get (ErrorType.Register));
							mb.open ();
							RegistrationWindow.setEnabled (true);
						}
					});
				}
				Client.Errors.clear ();
			break;
			
			case Password:
				if (ChangeSettingsWindow != null && !ChangeSettingsWindow.sShell.isDisposed ()) {
					Display.syncExec (new Runnable () {
						public void run () {
							MessageBox mb = new MessageBox (ChangeSettingsWindow.sShell, SWT.ICON_INFORMATION);
							mb.setText ("Password change");
							mb.setMessage ("Password changed successfully.");
							mb.open ();
						}
					});
				}
				saveUserSettings ();
			break;
			
			case RecievedMessages:
			case SentMessages:
				Map<String,ArrayList<Message>> Messages = new TreeMap<String,ArrayList<Message>> ();
				Iterator Iterator;
				if (Client.Recieved.size () > 0)
					synchronized (Client.Recieved) {
						Iterator = Client.Recieved.entrySet ().iterator ();
						while (Iterator.hasNext ()) {
							Map.Entry Pairs = (Map.Entry) Iterator.next ();
							String Key = (String) Pairs.getKey ();
							if (!Messages.containsKey (Key))
								Messages.put (Key, new ArrayList<Message> ());
							Messages.get (Key).addAll ((ArrayList<Message>) Pairs.getValue ());
						}
						Client.Recieved.clear ();
					}
				if (Client.ConfirmedMessages.size () > 0)
					synchronized (Client.ConfirmedMessages) {
						Iterator = Client.ConfirmedMessages.entrySet ().iterator ();
						while  (Iterator.hasNext ()) {
							Map.Entry Pairs = (Map.Entry) Iterator.next ();
							String Key = (String) Pairs.getKey ();
							if (!Messages.containsKey (Key))
								Messages.put (Key, new ArrayList<Message> ());
							Messages.get (Key).addAll ((ArrayList<Message>) Pairs.getValue ());
						}
						Client.ConfirmedMessages.clear ();
					}
				if (!Messages.isEmpty ()) {
					Iterator = Messages.entrySet ().iterator ();
					while  (Iterator.hasNext ()) {
						Map.Entry Pairs = (Map.Entry) Iterator.next ();
						String Key = (String) Pairs.getKey ();
						ArrayList<Message> UsersMessages = (ArrayList<Message>) Pairs.getValue ();
						Collections.sort (UsersMessages);
						openMessagingWindow (Key);
						MessagingWindows.get (Key).addMessages (UsersMessages);
					}
				}
			break;
			
			case UsernamesRegistered:
				if (RegistrationWindow.sShell != null && !RegistrationWindow.sShell.isDisposed ())
					Display.syncExec (new Runnable () {
						public void run () {
							MessageBox mb = new MessageBox (RegistrationWindow.sShell, SWT.ICON_INFORMATION);
							mb.setText ("Registration");
							mb.setMessage ("User '"+Client.Registered.get (0)+"' registered successfully!");
							mb.open ();
							RegistrationWindow.sShell.dispose ();
						}
					});
				makeUser (Client.getUserSettings ());
				if (SelectUser != null)
					SelectUser.repaint ();
				Client.Registered.clear ();
			break;
		}
	}
	
	private void addUserToBase (String username) {
		synchronized (Client.AllContacts) {
			Client.AllContacts.add (username);
		}
		synchronized (Client.KnownContacts) {
			Client.KnownContacts.add (new ClientUser (username, UserStatus.Offline));
		}
		saveUserContacts ();
		refreshContacts ();
		Client.makeGetStatusesRequest ();
	}
	
	private void finnish () {
		if (!Display.isDisposed ())
			Display.dispose ();
		Client.makeLogoutRequest ();
		Client.executeRequests ();
		
		try {
			Thread.sleep (1000);
		} catch (InterruptedException e) {}
		
		System.exit (0);
	}
	
	
	private boolean makePath (String path) {
		return (new File (path)).mkdirs ();
	}
	
	
	private void makeUser (UserSettings settings) {
		saveUserSettings (settings);
	}
	
	private ClientSettings readClientSettings () {
		XMLDecoder Input = null; 
		try {
			Input = new XMLDecoder (
				new FileInputStream (
					SettingsPath+"\\.client_settings"
				)
			);
			return (ClientSettings) Input.readObject ();
		} catch (Exception e) {
			try {
				Input.close ();
			} catch (Exception e1) {}
			return new ClientSettings ("localhost", 7777);
		}
	}
	
	private void readUserContacts () {
		readUserContacts (UsersPath+"\\"+Client.Username+".contacts");
	}
	
	@SuppressWarnings("unchecked")
	private void readUserContacts (String filename) {
		XMLDecoder Input = null; 
		try {
			Input = new XMLDecoder (
				new FileInputStream (
					filename
				)
			);
			Client.AllContacts = (ArrayList<String>) Input.readObject ();
		} catch (Exception e) {
			try {
				Input.close ();
			} catch (Exception e1) {}
			Client.AllContacts = new ArrayList<String> ();
		}
	}
	
	private UserSettings readUserSettings (String username) {
		XMLDecoder Input = null; 
		try {
			Input = new XMLDecoder (
					new FileInputStream (
							UsersPath+"\\"+username+".user"
					)
			);
			return (UserSettings) Input.readObject ();
		} catch (Exception e) {
			try {
				Input.close ();
			} catch (Exception e1) {}
			return null;
		}
	}

	private void saveClientSettings (ClientSettings clientSettings) {
		makePath (SettingsPath);
		try {
			XMLEncoder Output = new XMLEncoder (
				new BufferedOutputStream (
					new FileOutputStream (
						SettingsPath+"\\.client_settings"
					)
				)
			);
			Output.writeObject (clientSettings);
			Output.close ();
		} catch (IOException e) {}
	}
	
	private void saveUserContacts () {
		makePath (UsersPath);
		saveUserContacts (UsersPath+"\\"+Client.Username+".contacts");
	}
	
	private void saveUserContacts (String filename) {
		try {
			XMLEncoder Output = new XMLEncoder (
				new BufferedOutputStream (
					new FileOutputStream (
						filename
					)
				)
			);
			Output.writeObject (Client.AllContacts);
			Output.close ();
		} catch (IOException e) {}
	}
	
	private void saveUserSettings () {
		saveUserSettings (Client.getUserSettings ());
	}
	
	private void saveUserSettings (UserSettings settings) {
		try {
			XMLEncoder Output = new XMLEncoder (
				new BufferedOutputStream (
					new FileOutputStream (
						UsersPath+"\\"+settings.Username+".user"
					)
				)
			);
			Output.writeObject (settings);
			Output.close ();
		} catch (IOException e) {}
	}
}
