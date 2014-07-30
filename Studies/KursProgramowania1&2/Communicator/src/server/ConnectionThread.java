package server;

import java.io.IOException;
import java.net.Socket;

import requests.*;
import responses.*;
import serializer.Serializer;

public class ConnectionThread implements Runnable {
	Serializer	Serializer;
	Server		Server;
	Socket		Socket;
	
	public ConnectionThread (Server server, Socket socket) {
		Server = server;
		Socket = socket;
		
		try {
			Serializer = new Serializer (socket);
		} catch (IOException e) {
			return;
		}
		
		new Thread (this).start ();
	}
	
	public void run () {
		try {
			RequestPackage Package = (RequestPackage) Serializer.read ();
			if (Package == null || Package.Requests == null || Package.Requests.size () == 0)
				return;
			
			ResponsePackage Responses = new ResponsePackage ();
			
			for (Request Request : Package.Requests) if (Request != null)
			switch (Request.obtainRequestType ()) {
				case ChangePassword:
					Responses.add (Server.requestChangePassword ((RequestChangePassword) Request));
					System.out.println ("Response to: password change for '"+Request.Username+".");
				break;
				
				case ConnectionMaintenance:
					Responses.add (Server.requestConnectionMaintenance ((RequestConnectionMaintenance) Request));
					System.out.println ("Response to: connection maintenance for '"+Request.Username+":"+Request.Resource+"'.");
				break;
				
				case GetMessages:
					Responses.add (Server.requestGetMessages ((RequestGetMessages) Request));
					System.out.println ("Response to: to get messages request for '"+Request.Username+":"+Request.Resource+"'.");
				break;
			
				case GetStatuses:
					Responses.add (Server.requestGetStatuses ((RequestGetStatuses) Request));
					System.out.println ("Response to: get contacts' statuses for '"+Request.Username+":"+Request.Resource+"'.");
				break;
				
				case Login:
					Responses.add (Server.requestLogin ((RequestLogin) Request));
					System.out.println ("Response to: login for '"+Request.Username+":"+Request.Resource+"'.");
				break;
				
				case Logout:
					Responses.add (Server.requestLogout ((RequestLogout) Request));
					System.out.println ("Response to: logout for '"+Request.Username+":"+Request.Resource+"'.");
				break;
				
				case Register:
					Responses.add (Server.requestRegister ((RequestRegister) Request));
					System.out.println ("Response to: register for '"+Request.Username+"'.");
				break;
				
				case SendMessage:
					Responses.add (Server.requestSendMessage ((RequestSendMessage) Request));
					System.out.println ("Response to: sending message for '"+Request.Username+":"+Request.Resource+"'.");
				break;
				
				case SetStatus:
					Responses.add (Server.requestSetStatus ((RequestSetStatus) Request));
					System.out.println ("Response to: set status for "+Request.Username+":"+Request.Resource+"'.");
				break;
			}
			
			Serializer.write (Responses);
		} catch (Exception e) {
		} finally {
			if (!Socket.isClosed ())
				try {
					Socket.close ();
				} catch (IOException e) {}
		}
	}
}
