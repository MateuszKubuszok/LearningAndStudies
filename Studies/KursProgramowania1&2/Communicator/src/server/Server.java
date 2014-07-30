package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import data.*;
import requests.*;
import responses.*;

public class Server {
	public static String UsersDatafile;
	
	private ServerSocket 				Server;
	private Map<Integer,Session>		Sessions;
	private Map<String,ServerUser>		Users;
	private Map<String,ArrayList<Session>>	UsersSessions;
	
	public Server (int port) {
		try {
			Server = new ServerSocket (port);
			
			Sessions 		= new TreeMap<Integer,Session> ();
			Users 			= new TreeMap<String,ServerUser> ();
			UsersSessions 	= new TreeMap<String,ArrayList<Session>> ();
			
			readUsers ();
			
			new ServerThread (this);
			new SessionCleaner (this);
		} catch (IOException e) {
			System.err.println ("Choosen port is already in use.");
		}
	}
	
	public void finallize () {
		writeUsers ();
		
		try {
			Server.close ();
		} catch (IOException e) {}
	}
	
	public ServerSocket getServerSocket () {
		return Server;
	}
	
	public Map<Integer,Session> getSessions () {
		return Sessions;
	}
	
	public Map<String,ArrayList<Session>> getUsersSessions () {
		return UsersSessions;
	}

	public void logoutSession (Session session) {
		if (session == null)
			return;
		
		String Username = session.Username;
		
		if (session.Messages != null && session.Messages.size() > 0) {
			synchronized (Users) {
				ServerUser User = Users.get (Username);
				if (User != null)
					User.Messages.addAll (session.Messages);
			}
		}
		
		synchronized (UsersSessions) {
			if (UsersSessions.containsKey (Username)) {
				ArrayList<Session> SessionsList = UsersSessions.get (Username);
				SessionsList.remove (session);
				
				if (SessionsList.size () == 0)
					UsersSessions.remove (Username);
			}
		}
		
		synchronized  (Sessions) {
			Sessions.remove (session.ID);
		}
	}
	
	public ResponseChangePassword requestChangePassword (RequestChangePassword request) {
		ResponseChangePassword Response = new ResponseChangePassword ();
		
		String Username = request.Username;
		
		synchronized (Users) {
			if (!Users.containsKey (Username))
				Response.ErrorMessage = "No user '"+Username+"' registerd!";
			else if (request.Password == null || !Users.get (Username).Password.equals (request.Password))
				Response.ErrorMessage = "Given password is incorrect for user '"+Username+"'!";
			else if (request.NewPassword == null || request.NewPassword.isEmpty ())
				Response.ErrorMessage = "No new password given!";
			else
				Users.get (Username).Password = request.NewPassword;
		}
		
		return Response;
	}
	
	public ResponseConnectionMaintenance requestConnectionMaintenance (RequestConnectionMaintenance request) {
		ResponseConnectionMaintenance Response = new ResponseConnectionMaintenance ();
		
		Session Session = getSession (request);
		if (Session == null)
			Response.ErrorMessage = "Request error: session ID ("+request.SessionID+") not found!";
		else
			synchronized (Session) {
				Session.Date = new Date ();
			}
		
		return Response;
	}
	
	public ResponseGetMessages requestGetMessages (RequestGetMessages request) {
		ResponseGetMessages Response = new ResponseGetMessages ();
		
		Session Session = getSession (request);
		if (Session == null) {
			Response.ErrorMessage = "Request error: session ID ("+request.SessionID+") not found!";
			return Response;
		} else
			synchronized (Session) {
				Session.Date = new Date ();
			}
		
		String Username = request.Username;
		synchronized (Users) {
			if (Users.containsKey (Username) &&
				Users.get (Username).Messages != null &&
				Users.get (Username).Messages.size () > 0) {
				ArrayList<Message> Messages = Users.get (Username).Messages;
				synchronized (Messages) {
					Response.Messages.addAll (Messages);
				}
				Messages.clear ();
			}
		}
		
		synchronized (Session.Messages) {
			Response.Messages.addAll (Session.Messages);
			Session.Messages.clear ();
		}
		
		return Response;
	}
	
	public ResponseGetStatuses requestGetStatuses (RequestGetStatuses request) {
		ResponseGetStatuses Response = new ResponseGetStatuses ();
		
		Session Session = getSession (request);
		if (Session == null) {
			Response.ErrorMessage = "Request error: session ID ("+request.SessionID+") not found!";
			return Response;
		} else
			synchronized (Session) {
				Session.Date = new Date ();
			}
		
		if (request.Users != null && request.Users.size () > 0) {
			ArrayList<ClientUser> CheckedUsers = new ArrayList<ClientUser> ();
			
			int i = 0;
			for (String Username : request.Users) {
				ClientUser CheckedUser = new ClientUser (Username); 
				CheckedUsers.add (CheckedUser);
				
				synchronized (UsersSessions) {
					if (UsersSessions.containsKey (Username)) {
						CheckedUser.State = UserStatus.Offline;
						
						ArrayList<Session> Sessions = UsersSessions.get (Username);
						synchronized (Sessions) {
							for (Session CheckedSession : Sessions) {
								if (CheckedSession.Status == UserStatus.Online) {
									CheckedUser.State = UserStatus.Online;
									break;
								} else if (CheckedSession.Status == UserStatus.Away)
									CheckedUser.State = UserStatus.Away;
							}
						}
					}
				}
				
				i++;
			}
			
			Response.Users = CheckedUsers;
		}
				
		return Response;
	}
	
	public ResponseLogin requestLogin (RequestLogin request) {
		ResponseLogin Response = new ResponseLogin ();
		ArrayList<Session> UserSessions;
		
		String Username = request.Username;
		String Resource = request.Resource;
		if (Users.containsKey (Username)) {
			synchronized (UsersSessions) {
				if (!(Users.get (Username).Password.equals (request.Password))) {
					Response.ErrorMessage = "Given password is incorrect for user '"+Username+"'!";
					return Response;
				} if (UsersSessions.containsKey (Username)) {
					for (Session Session : UsersSessions.get (Username))
						if (Session.Resource.equals (Resource)) {
							Response.ErrorMessage = "User with username '"+Username+"' and resource '"+Resource+"' is already logged!";
							return Response;
						}
				} else {
					UserSessions = new ArrayList<Session> ();
					UsersSessions.put (Username, UserSessions);
				}
			
				Session Session	 = new Session ();
				Session.Username = Username;
				Session.Resource = Resource;
				Session.Status	 = UserStatus.Online;
				Session.Date	 = new Date ();
				
				int i;
				synchronized (Sessions) {
					for (i = 1; Sessions.containsKey (i); i++) ;
					Session.ID = i;
					Sessions.put (i, Session);
				}				
				
				UsersSessions.get (Username).add (Session);
				
				Response.SessionID = i;
			}
		} else
			Response.ErrorMessage = "No user '"+Username+"' registerd!";
		
		return Response;
	}
	
	public ResponseLogout requestLogout (RequestLogout request) {
		ResponseLogout Response = new ResponseLogout ();
		
		Session Session;
		if ((Session = getSession (request)) != null)
			logoutSession (Session);
		else
			Response.ErrorMessage = "Request error: session ID ("+request.SessionID+") not found!";
		
		return Response;
	}
	
	public ResponseRegister requestRegister (RequestRegister request) {
		ResponseRegister Response = new ResponseRegister (request.Username);
		
		String Username = request.Username;
		String Password = request.Password;
		if (Users.containsKey (Username)) {
			Response.ErrorMessage = "Username '"+Username+"' is already registered!";
		} else if (	Username == null || Username.isEmpty () ||
					Password == null || Password.isEmpty ()) {
			Response.ErrorMessage = "No username and/or password given!";
		} else {
			ServerUser User = new ServerUser (Username, Password);
			synchronized (Users) {
				Users.put (Username, User);
			}
		}
		
		return Response;
	}
	
	public ResponseSendMessage requestSendMessage (RequestSendMessage request) {
		ResponseSendMessage Response = new ResponseSendMessage ();
		
		Session Session = getSession (request);
		if (Session == null) {
			Response.ErrorMessage = "Request error: session ID ("+request.SessionID+") not found!";
			return Response;
		} else
			synchronized (Session) {
				Session.Date = new Date ();
			}
		
		Message Message = request.Message != null ? request.Message : new Message ();
		
		synchronized (Users) {
			if (!Users.containsKey (Message.To))
				Response.ErrorMessage = "Couldn't deliver message: user '"+Message.To+"' isn't registered!";
			else if (Message.To.equals (request.Username))
				return Response;
			else {
				synchronized (UsersSessions) {
					if (UsersSessions.containsKey (Message.To)) {
						ArrayList<Session> Sessions = UsersSessions.get (Message.To);
						for (Session USession : Sessions)
							USession.Messages.add (Message);
					} else
						Users.get (Message.To).Messages.add (Message);
				}
				Response.MessageHash = request.MessageHash;
			}
		}
		
		return Response;
	}
	
	public ResponseSetStatus requestSetStatus (RequestSetStatus request) {
		ResponseSetStatus Response = new ResponseSetStatus ();
		
		Session Session = getSession (request);
		if (Session == null) {
			Response.ErrorMessage = "Request error: session ID ("+request.SessionID+") not found!";
			return Response;
		} else
			synchronized (Session) {
				Session.Date = new Date ();
			}
		
		if (request.Status == UserStatus.Offline) {
			logoutSession (Session);
			return new ResponseLogout ();
		} 
		
		Session.Status	= request.Status;
		Response.Status	= request.Status;
		
		return Response;
	}
	
	
	public void readUsers () {
		readUsers (UsersDatafile);
	}
	
	@SuppressWarnings("unchecked")
	public void readUsers (String filename) {
		try {
			ObjectInputStream Input = new ObjectInputStream (
				new BufferedInputStream (
					new FileInputStream (filename)
				)
			);
			
			Users = (Map<String,ServerUser>) Input.readObject ();
		} catch (Exception e) {
			if (Users == null)
				Users = new TreeMap<String,ServerUser> ();
		}
	}
	
	public void writeUsers () {
		writeUsers (UsersDatafile);
	}
	
	public void writeUsers (String filename) {
		try {
			ObjectOutputStream Output = new ObjectOutputStream (
				new BufferedOutputStream (
					new FileOutputStream (filename)
				)
			);
			
			synchronized (Users) {
				Output.writeObject (Users);
			}
			
			Output.close ();
		} catch (Exception e) {
			System.err.println ("Cannot write to file!");
		}
	}
	
	
	private Session getSession (Request request) {
		return getSession (request.Username, request.Resource, request.SessionID);
	}
	
	private Session getSession (String username, String resource, int sessionID) {
		synchronized (Sessions) {
			if (Sessions.containsKey (sessionID)) {
				Session Session = Sessions.get (sessionID);
				if (Session != null && 
					Session.Username != null && Session.Username.equals (username) && 
					Session.Resource != null && Session.Resource.equals (resource))
					return Session;
			}
		}
		return null;
	}
}
