package gui;

import java.util.ArrayList;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;

import client_controller.ClientController;
import data.ClientUser;
import data.Message;

public class MessagingWindow {
	private ClientController Controller;
	private String Username;
	
	private Display Display;
	public Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="60,20"
	private Text TextInput = null;
	private Button Send = null;
	private StyledText MessagingLog = null;

	public MessagingWindow (ClientController controller, final ClientUser user) {
		Controller = controller;
		Controller.MessagingWindows.put (user.Username, this);
		Username = user.Username;
		Display = Controller.Display;
		Display.syncExec (new Runnable () {
			public void run () {
				createSShell ();
				sShell.setText (user.Username + " ("+user.State+")");
				initialize ();
			}
		});
	}
	
	public void addMessages (final ArrayList<Message> messages) {
		if (!Display.isDisposed ()) {
			Display.syncExec (new Runnable () {
				public void run () {
					if (!sShell.isDisposed ())
						for (Message Message : messages)
							MessagingLog.append (Message.From+" ("+Message.Date+"):\n"+Message.Content+"\n");					
				}
			});
		}
	}
	
	private void initialize () {
		sShell.open ();
	}
	
	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		GridData gridData11 = new GridData();
		gridData11.grabExcessVerticalSpace = false;
		gridData11.horizontalAlignment = GridData.BEGINNING;
		gridData11.verticalAlignment = GridData.CENTER;
		gridData11.grabExcessHorizontalSpace = false;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = false;
				
		sShell = new Shell (Display);
		sShell.setText("Messaging");
		sShell.setLayout(gridLayout);
		sShell.setSize(new Point(600, 400));
		sShell.addShellListener(new org.eclipse.swt.events.ShellAdapter() {
			public void shellClosed(org.eclipse.swt.events.ShellEvent e) {
				sShell.setVisible (false);
			}
		});
		sShell.setMinimumSize(new Point(600, 400));
		MessagingLog = new StyledText(sShell, SWT.NONE);
		MessagingLog.setLayoutData(gridData);
		MessagingLog.setEditable (false);
		TextInput = new Text(sShell, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		TextInput.setLayoutData(gridData1);
		TextInput.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {
			public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
				if (e.keyCode == 13 && (e.stateMask & SWT.SHIFT) == 0)
					sendMessage ();
			}
		});
		Send = new Button(sShell, SWT.NONE);
		Send.setText("Send");
		Send.setLayoutData(gridData11);
		Send.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {   
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				sendMessage ();
			}
		});
	}

	private void sendMessage () {
		String ToSend = TextInput.getText ().trim ();
		if (!ToSend.isEmpty ()) {
			Controller.sendMessage (Username, ToSend);
			TextInput.setText ("");
		}
	}
}
