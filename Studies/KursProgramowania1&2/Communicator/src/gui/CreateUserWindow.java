package gui;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

import client_controller.ClientController;

public class CreateUserWindow {
	private ClientController Controller; 
	
	private Display Display;
	public Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="0,0"
	private Label UsernameLabel = null;
	private Text Username = null;
	private Label PasswordLabel = null;
	private Text Password = null;
	private Button Create = null;

	public CreateUserWindow (ClientController controller) {
		Controller = controller;
		Controller.CreationWindow = this;
		Display = controller.Display;
		createSShell ();
		initialize ();
	}
	
	public void setEnabled (boolean enabled) {
		Username.setEnabled (enabled);
		Password.setEnabled (enabled);
		Create.setEnabled (enabled);
	}
	
	private void initialize () {
		sShell.open ();
	}
	
	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		sShell = new Shell(Display, SWT.DIALOG_TRIM);
		sShell.setText("Create new user");
		sShell.setLayout(gridLayout);
		sShell.setSize(new Point(160, 125));
		UsernameLabel = new Label(sShell, SWT.NONE);
		UsernameLabel.setText("Username");
		Username = new Text(sShell, SWT.BORDER);
		PasswordLabel = new Label(sShell, SWT.NONE);
		PasswordLabel.setText("Password");
		Password = new Text(sShell, SWT.BORDER | SWT.PASSWORD);
		@SuppressWarnings("unused")
		Label filler = new Label(sShell, SWT.NONE);
		Create = new Button(sShell, SWT.NONE);
		Create.setText("Create");
		Create.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {			
				setEnabled (false);
				Controller.createNewUser (Username.getText (), Password.getText ());			
			}
		});
	}

}
