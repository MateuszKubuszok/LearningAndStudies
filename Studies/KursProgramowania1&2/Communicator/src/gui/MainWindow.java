package gui;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.graphics.Rectangle;

import client_controller.ClientController;
import data.ClientUser;
import data.UserStatus;

public class MainWindow {
	private ClientController Controller;
	
	private Display Display;
	public Shell Shell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Menu MenuBar = null;
	private Menu SettingsMenu = null;
	private Menu StatusMenu = null;
	private Menu ContactsMenu = null;
	private CLabel ClientStatus = null;
	private List ContactsList = null;
	private UserStatus Status;  //  @jve:decl-index=0:
	
	public MainWindow (ClientController controller) {
		Controller = controller;
		Controller.MainWindow = this;
		Display = Controller.Display;
		createShell ();
		initialize ();
	}
	
	public void setStatus (UserStatus status) {
		Status = status;
		if (!Display.isDisposed ())
		Display.syncExec(new Runnable () {
			public void run () {
				if (!Shell.isDisposed ())
					ClientStatus.setText (Status+"");
			}
		});
	}
	
	public void updateContacts (final ArrayList<ClientUser> knownContacts) {
		if (!Display.isDisposed ())
		Display.syncExec(new Runnable () {
			public void run () {
				if (!Shell.isDisposed ()) {
					ArrayList<String>	Selected = parseContacts ();
					int[]				ToSelect = new int[Selected.size ()];
					
					Collections.sort (knownContacts);
					ContactsList.removeAll ();
					int i = 0,
						j = 0;
					for (ClientUser Contact : knownContacts) {
						ContactsList.add (Contact.Username+" ("+Contact.State+")");
						if (Selected.contains (Contact.Username))
							ToSelect [i++] = j;
						j++;
					}
					
					ContactsList.select (ToSelect);
				}
			}
		});
	}
	
	private void initialize () {
		Shell.open ();
		Controller.refreshContacts ();
	}
	
	/**
	 * This method initializes Shell
	 */
	private void createShell() {
		Shell = new Shell(Display);
		Shell.setText("IM FTW");
		ClientStatus = new CLabel(Shell, SWT.NONE);
		ClientStatus.setText("Offline");
		ClientStatus.setBounds(new Rectangle(0, 0, 42, 21));
		ContactsList = new List(Shell, SWT.V_SCROLL);
		ContactsList.setBounds(new Rectangle(0, 0, 67, 64));
		ContactsList.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent e) {
				Controller.openMessagingWindows (parseContacts ());
			}
		});
		Shell.setSize(new Point(200, 400));
		Shell.setMinimumSize(new Point(200, 400));
		Shell.setLayout(null);
		MenuBar = new Menu(Shell, SWT.BAR);
		MenuItem Menu = new MenuItem(MenuBar, SWT.CASCADE);
		Menu.setText("Settings");
		MenuItem Status = new MenuItem(MenuBar, SWT.CASCADE);
		Status.setText("Status");
		MenuItem Contacts1 = new MenuItem(MenuBar, SWT.CASCADE);
		Contacts1.setText("Contacts");
		ContactsMenu = new Menu(Contacts1);
		MenuItem Add = new MenuItem(ContactsMenu, SWT.PUSH);
		Add.setText("Add");
		Add.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Controller.openAddUserWindow ();
			}
		});
		MenuItem Delete = new MenuItem(ContactsMenu, SWT.PUSH);
		Delete.setText("Delete");
		Delete.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Controller.deleteOldUser (parseContacts ());
			}
		});
		MenuItem Import = new MenuItem(ContactsMenu, SWT.PUSH);
		Import.setText("Import");
		Import.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Controller.openImportContactsDialog ();
			}
		});
		MenuItem Export = new MenuItem(ContactsMenu, SWT.PUSH);
		Export.setText("Export");
		Export.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Controller.openExportContactsDialog ();
			}
		});
		Contacts1.setMenu(ContactsMenu);
		StatusMenu = new Menu(Status);
		MenuItem Online = new MenuItem(StatusMenu, SWT.PUSH);
		Online.setText("Online");
		Online.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Controller.setStatusOnline ();
			}
		});
		MenuItem Away = new MenuItem(StatusMenu, SWT.PUSH);
		Away.setText("Away");
		Away.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Controller.setStatusAway ();
			}
		});
		MenuItem Invisible = new MenuItem(StatusMenu, SWT.PUSH);
		Invisible.setText("Invisible");
		Invisible.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Controller.setStatusInvisible ();
			}
		});
		MenuItem Offline = new MenuItem(StatusMenu, SWT.PUSH);
		Offline.setText("Offline");
		Offline.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Controller.setStatusOffline ();
			}
		});
		Status.setMenu(StatusMenu);
		SettingsMenu = new Menu(Menu);
		MenuItem ChangeSettings = new MenuItem(SettingsMenu, SWT.PUSH);
		ChangeSettings.setText("Change settings");
		ChangeSettings.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Controller.openChangeSettingsWindow ();
			}
		});
		Menu.setMenu(SettingsMenu);
		Shell.setMenuBar(MenuBar);
		Shell.addControlListener(new org.eclipse.swt.events.ControlAdapter() {
			public void controlResized(org.eclipse.swt.events.ControlEvent e) {
				Point Size = Shell.getSize ();
				ClientStatus.setBounds(0, 0, Size.x, 20);
				ContactsList.setBounds(0, 20, Size.x, (Size.y)-20);
			}
		});
		Shell.addShellListener(new org.eclipse.swt.events.ShellAdapter() {
			public void shellClosed(org.eclipse.swt.events.ShellEvent e) {
				Controller.closeAllWindows ();
			}
		});
	}

	private ArrayList<String> parseContacts () {
		String[] Selected = ContactsList.getSelection ();
		ArrayList<String> SelectedContacts = new ArrayList<String> ();
		
		String[] ToCheck = new String[] {
			" (Online)",
			" (Away)",
			" (Invisible)",
			" (Offline)"
		};
		
		for (String Contact : Selected) {
			for (String Suffix : ToCheck)
				if (Contact.endsWith (Suffix)) {
					SelectedContacts.add (Contact.substring (0, Contact.length()-Suffix.length ()));
					break;
				}
		}
		
		return SelectedContacts;
	}
}
