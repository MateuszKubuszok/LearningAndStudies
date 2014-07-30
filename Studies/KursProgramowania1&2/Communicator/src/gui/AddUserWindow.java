package gui;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

import client_controller.ClientController;

public class AddUserWindow {
	private ClientController Controller;
	
	private Display Display;
	public Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="0,0"
	private Text NewUser = null;
	private Button AddUser = null;
	private Label NewUserLabel = null;

	public AddUserWindow (ClientController controller) {
		Controller = controller;
		Controller.AddUserWindow = this;
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
	@SuppressWarnings("unused")
	private void createSShell() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		sShell = new Shell(Display, SWT.DIALOG_TRIM);
		sShell.setText("Add new user");
		sShell.setLayout(gridLayout);
		sShell.setSize(new Point(156, 99));
		NewUserLabel = new Label(sShell, SWT.NONE);
		NewUserLabel.setText("New user");
		NewUser = new Text(sShell, SWT.BORDER);
		Label filler = new Label(sShell, SWT.NONE);
		AddUser = new Button(sShell, SWT.NONE);
		AddUser.setText("Add user");
		AddUser.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Controller.addNewUser (NewUser.getText ());
			}
		});
	}

}
