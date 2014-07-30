package controller;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JOptionPane;

import commands.Command;
import commands.CommandType;
import commands.DrawCommand;

import connection_gui.ConnectionWindow;
import drawer_gui.DrawerWindow;
import server.*;

public class Controller {
	private Dimension		Start = null;
	private Dimension		Middle = null;
	private Dimension		Stop = null;
	private DrawerWindow	Window = null;
	
	private	ConnectionWindow	ConnectionWindow;
	private Client				Client;
	private Synchronizer		Synchronizer;
	
	public Controller () {
		ConnectionWindow = new ConnectionWindow (this);
		System.out.println ("initialized");
	}
	
	public Client getClient () {
		return Client;
	}
	
	public void createHost (String port) {
		try {
			if (Client != null && Client.getClass ().getName ().equals ("Server"))
				((Server) Client).closePort ();
			
			Client = new Server (Integer.parseInt (port));
		} catch (NumberFormatException Exception) {
			System.out.println (Exception.getMessage ());
			JOptionPane.showMessageDialog (Window.getJFrame (), port+" is not valid port number format.");
			Window.getJFrame ().setTitle ("NetDrawer");
			return;
		} catch (ServerError Exception) {
			System.out.println (Exception.getMessage ());
			JOptionPane.showMessageDialog (Window.getJFrame (), Exception.getMessage ());
			Window.getJFrame ().setTitle ("NetDrawer");
			return;
		}
		
		Window.getJFrame ().setTitle("NetDrawer (host at: "+port+")");
		ConnectionWindow.setVisible (false);
		
		Synchronizer = new Synchronizer (this);
		Thread Thread = new Thread (Synchronizer);
		Thread.setDaemon (true);
		Thread.start ();
	}
	
	public void connectToHost (String host, String port) {
		try {
			if (Client != null && Client.getClass ().getName ().equals ("Server"))
				((Server) Client).closePort ();
			
			Client = new Client (host, Integer.parseInt (port));
		} catch (NumberFormatException Exception) {
			System.out.println (Exception.getMessage ());
			JOptionPane.showMessageDialog (Window.getJFrame (), port+" is not valid port number format.");
			Window.getJFrame ().setTitle ("NetDrawer");
			return;
		} catch (ServerError Exception) {
			System.out.println (Exception.getMessage ());
			JOptionPane.showMessageDialog (Window.getJFrame (), Exception.getMessage ());
			Window.getJFrame ().setTitle ("NetDrawer");
			return;
		}
		
		Window.getJFrame ().setTitle("NetDrawer (connected to: "+host+":"+port+")");
		ConnectionWindow.setVisible (false);
		
		Synchronizer = new Synchronizer (this);
		Thread Thread = new Thread (Synchronizer);
		Thread.setDaemon (true);
		Thread.start ();
	}
	
	public void showConnectionWindow () {
		ConnectionWindow.setVisible (true);
	}
	
	public void clear () {
		DrawCommand Command = new DrawCommand (CommandType.Clear, new Dimension (Window.Width, Window.Height), null);
		
		Vector<DrawCommand> Commands = new Vector<DrawCommand> ();
		Commands.add (Command);
		
		Window.repaint ();
		Window.update (Commands);
		
		if (Synchronizer != null)
			Synchronizer.addCommand (Command);
	}
	
	public void update (Vector<Command> commands) {
		Vector<DrawCommand> Commands = new Vector<DrawCommand> ();
		for (Command Command : commands)
			try {
				Commands.add ((DrawCommand) Command);
			} catch (ClassCastException Exception) {}
		Window.update (Commands);
	}
	
	public void mouseDragged (MouseEvent event) {
		Middle	= new Dimension (event.getX (), event.getY ());
		Stop	= null;
		refresh ();
	}
	
	public void mousePressed (MouseEvent event) {
		if (event.getButton () != MouseEvent.BUTTON1)
			return;
		
		Start	= new Dimension (event.getX (), event.getY ());
		Middle	= null;
		Stop	= null;
	}
	
	public void mouseReleased (MouseEvent event) {
		if (event.getButton () != MouseEvent.BUTTON1)
			return;
		
		Middle	= null;
		Stop	= new Dimension (event.getX (), event.getY ());
		refresh ();
		
		if (Synchronizer != null)
			Synchronizer.addCommand (new DrawCommand (Window.getType (), Start, Stop));
		
		Start	= null;
		Stop	= null;
	}
	
	public void setWindow (DrawerWindow window) {
		Window = window;
	}
	
	private void refresh () {
		Window.setStart		(Start);
		Window.setMiddle	(Middle);
		Window.setStop		(Stop);
		Window.repaint ();
	}
}
