package gui;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

import client.UserSettings;
import client_controller.ClientController;
import org.eclipse.swt.layout.GridData;

public class ChangeSettingsWindow {
	private ClientController Controller;
	
	private Display Display;
	public Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="15,11"
	private Group Settings = null;
	private Group ChangePassword = null;
	private Label ResourceLabel = null;
	private Text Resource = null;
	private Label PasswordLabel = null;
	private Text Password = null;
	private Label NewPasswordLabel = null;
	private Text NewPassword = null;
	private Label RepeatPasswordLabel = null;
	private Text RepeatPassword = null;
	private Label ClientSettingsLabel = null;
	private Label ChangePasswordLabel = null;
	private Button SaveSettings = null;
	private Button SaveNewPassword = null;

	public ChangeSettingsWindow (ClientController controller) {
		Controller = controller;
		Controller.ChangeSettingsWindow = this;
		Display = Controller.Display;
		createSShell ();
		initialize ();
	}
	
	private void initialize () {
		sShell.open ();
	}
	
	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 2;
		sShell = new Shell(Display, SWT.DIALOG_TRIM);
		sShell.setText("Change settings");
		sShell.setLayout(gridLayout2);
		ClientSettingsLabel = new Label(sShell, SWT.NONE);
		ClientSettingsLabel.setText("Client settings");
		ChangePasswordLabel = new Label(sShell, SWT.NONE);
		ChangePasswordLabel.setText("Change password");
		createSettings();
		createChangePassword();
		sShell.setSize(new Point(391, 163));
		sShell.addShellListener(new org.eclipse.swt.events.ShellAdapter() {   
			public void shellActivated(org.eclipse.swt.events.ShellEvent e) {    
				if (!Display.isDisposed ())
					Display.syncExec(new Runnable () {
						public void run () {							
							if (!sShell.isDisposed ()) {
								UserSettings Settings = Controller.getUserSettings ();
								Resource.setText (Settings.Resource);
								Password.setText (Settings.Password);
							}
						}
					});
			}
			public void shellClosed(org.eclipse.swt.events.ShellEvent e) {
				if (!Display.isDisposed ())
					Display.syncExec(new Runnable () {
						public void run () {							
							if (!sShell.isDisposed ())
								sShell.setVisible (false);
						}
					});
			}
		});
	}

	/**
	 * This method initializes Settings	
	 *
	 */
	@SuppressWarnings("unused")
	private void createSettings() {
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.verticalAlignment = GridData.CENTER;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		Settings = new Group(sShell, SWT.NONE);
		Settings.setLayout(gridLayout);
		ResourceLabel = new Label(Settings, SWT.NONE);
		ResourceLabel.setText("Resource");
		Resource = new Text(Settings, SWT.BORDER);
		Resource.setLayoutData(gridData3);
		PasswordLabel = new Label(Settings, SWT.NONE);
		PasswordLabel.setText("Password");
		Password = new Text(Settings, SWT.BORDER | SWT.PASSWORD);
		Password.setLayoutData(gridData2);
		Label filler = new Label(Settings, SWT.NONE);
		SaveSettings = new Button(Settings, SWT.NONE);
		SaveSettings.setText("Save changes");
		SaveSettings.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Controller.changeSettings (Resource.getText (), Password.getText ());
			}
		});
	}

	/**
	 * This method initializes ChangePassword	
	 *
	 */
	@SuppressWarnings("unused")
	private void createChangePassword() {
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.verticalAlignment = GridData.CENTER;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		ChangePassword = new Group(sShell, SWT.NONE);
		ChangePassword.setLayout(gridLayout1);
		NewPasswordLabel = new Label(ChangePassword, SWT.NONE);
		NewPasswordLabel.setText("New password");
		NewPassword = new Text(ChangePassword, SWT.BORDER | SWT.PASSWORD);
		NewPassword.setLayoutData(gridData);
		RepeatPasswordLabel = new Label(ChangePassword, SWT.NONE);
		RepeatPasswordLabel.setText("Repeat password");
		RepeatPassword = new Text(ChangePassword, SWT.BORDER | SWT.PASSWORD);
		RepeatPassword.setLayoutData(gridData1);
		Label filler1 = new Label(ChangePassword, SWT.NONE);
		SaveNewPassword = new Button(ChangePassword, SWT.NONE);
		SaveNewPassword.setText("Change password");
		SaveNewPassword.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Controller.changePassword (NewPassword.getText (), RepeatPassword.getText ());
			}
		});
	}

}
