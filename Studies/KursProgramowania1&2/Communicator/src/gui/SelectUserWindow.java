package gui;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.FillLayout;

import client_controller.ClientController;

public class SelectUserWindow {
	private ClientController Controller;
	
	private Display Display;
	public Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="229,31"
	private Button Select = null;
	private Button Register = null;
	private Button Create = null;
	private Composite composite = null;
	private List UsersList = null;

	public SelectUserWindow (ClientController controller) {
		Controller = controller;
		Controller.SelectUser = this;
		Display = Controller.Display;
		
		createSShell ();
		initialize ();
	}
	
	public void repaint () {
		Display.syncExec(new Runnable () {
			public void run () {
				UsersList.removeAll ();
				for (String User : Controller.getUsers ())
					UsersList.add (User);
				if (sShell != null && !sShell.isDisposed ())
					sShell.redraw ();
			}
		});
	}
	
	private void initialize () {
		sShell.open ();
		repaint ();
	}
	
	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		sShell = new Shell(Display, SWT.DIALOG_TRIM);
		sShell.setText("Select user");
		sShell.setLayout(gridLayout);
		createComposite();
		sShell.setSize(new Point(188, 132));
		Register = new Button(sShell, SWT.NONE);
		Register.setText("Register");
		Register.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent event) {
				Controller.openRegistrationWindow ();
			}
		});
		Create = new Button(sShell, SWT.NONE);
		Create.setText("Create");
		Create.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent event) {
				Controller.openCreationWindow ();
			}
		});
		Select = new Button(sShell, SWT.NONE);
		Select.setText("Select");
		Select.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent event) {
				Controller.loadUser (UsersList.getSelection ());
			}
		});
	}

	/**
	 * This method initializes composite	
	 *
	 */
	private void createComposite() {
		GridData gridData1 = new GridData();
		gridData1.horizontalSpan = 3;
		gridData1.verticalAlignment = GridData.CENTER;
		gridData1.horizontalAlignment = GridData.FILL;
		composite = new Composite(sShell, SWT.NONE);
		composite.setLayout(new FillLayout());
		composite.setLayoutData(gridData1);
		UsersList = new List(composite, SWT.V_SCROLL);
		UsersList.addMouseListener(new org.eclipse.swt.events.MouseListener() {
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent e) {
				Controller.loadUser (UsersList.getSelection ());
			}
			public void mouseDown(org.eclipse.swt.events.MouseEvent e) {}
			public void mouseUp(org.eclipse.swt.events.MouseEvent e) {}
		});
	}
}
