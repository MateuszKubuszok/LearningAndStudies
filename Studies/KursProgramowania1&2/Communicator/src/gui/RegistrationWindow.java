package gui;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionAdapter;

import client_controller.ClientController;

public class RegistrationWindow {
	private ClientController Controller;
	
	private Display Display;
	public Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Text Username = null;
	private Label PasswordLabel = null;
	private Label UsernameLabel = null;
	private Text RepeatPassword = null;
	private Label RepeatPasswordLabel = null;
	private Text Password = null;
	private Button Register = null;
	
	public RegistrationWindow (ClientController controller) {
		Controller = controller;
		Controller.RegistrationWindow = this;
		Display = Controller.Display;
		createSShell ();		
		initialize ();
	}
	
	public void setEnabled (boolean enabled) {
		Username.setEnabled (enabled);
		Password.setEnabled (enabled);
		RepeatPassword.setEnabled (enabled);
		Register.setEnabled (enabled);
	}
	
	private void initialize () {
		sShell.open ();
	}
	
	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		gridLayout1.makeColumnsEqualWidth = true;
		sShell = new Shell(Display, SWT.DIALOG_TRIM);
		sShell.setText("Register new user");
		UsernameLabel = new Label(sShell, SWT.NONE);
		UsernameLabel.setText("Username");
		Username = new Text(sShell, SWT.BORDER);
		PasswordLabel = new Label(sShell, SWT.NONE);
		PasswordLabel.setText("Password");
		sShell.setLayout(gridLayout1);
		sShell.setSize(new Point(185, 141));
		Password = new Text(sShell, SWT.BORDER | SWT.PASSWORD);
		RepeatPasswordLabel = new Label(sShell, SWT.NONE);
		RepeatPasswordLabel.setText("Repeat password");
		RepeatPassword = new Text(sShell, SWT.BORDER | SWT.PASSWORD);
		@SuppressWarnings("unused")
		Label filler = new Label(sShell, SWT.NONE);
		Register = new Button(sShell, SWT.NONE);
		Register.setText("Register");
		Register.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent event) {
				setEnabled (false);
				Controller.registerUser (Username.getText (), Password.getText (), RepeatPassword.getText ());
			}
		});
	}
}
