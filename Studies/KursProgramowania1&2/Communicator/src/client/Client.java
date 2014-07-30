package client;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;

import data.*;
import requests.*;
import responses.*;
import serializer.Serializer;

public class Client extends Observable {
	public ServerStatus ServerStatus;	
	public String		Host;
	public int			Port;
	
	public UserStatus	Status;
	public String		Username;
	public String		Password;
	public String		Resource;
	public String		NewPassword;
	public int			SessionID = 0;
	
	private	RequestPackage	Requests;
	private RequestPackage	UnloggedRequests;
	
	public Map<ErrorType,String>	Errors;
	public boolean					CheckStatus = false;
	
	public ArrayList<String> 			AllContacts;
	public ArrayList<ClientUser> 		KnownContacts;
	
	public Map<String,ArrayList<Message>>	ConfirmedMessages;
	public Map<String,Message>				FailedMessages;
	public Map<String,Message>				UnconfirmedMessages;
	public Map<String,ArrayList<Message>>	Recieved;
	
	public ArrayList<String>				Registered;
	
	public MD5	MD5;
	
	public boolean UpdatedContacts		= false;
	public boolean UpdatedErrors		= false;
	public boolean UpdatedRecieved		= false;
	public boolean UpdatedRegistered	= false;
	public boolean UpdatedPassword		= false;
	public boolean UpdatedSent			= false;
	public boolean UpdatedSettings		= false;
	public boolean UpdatedStatus		= false;
	
	public Client (ClientSettings settings) {
		Host = settings.Host;
		Port = settings.Port;
		
		startup ();
	}
	
	public Client (String host, int port) {
		Host	= host;
		Port	= port;
		
		startup ();
	}
	
	private void startup () {
		Status	= UserStatus.Offline;
		
		UnloggedRequests	= new RequestPackage (this);
		Requests			= new RequestPackage (this);
		
		Errors				= new TreeMap<ErrorType,String> ();
		
		AllContacts			= new ArrayList<String> ();
		KnownContacts		= new ArrayList<ClientUser> ();
		
		ConfirmedMessages	= new TreeMap<String,ArrayList<Message>> ();
		FailedMessages		= new TreeMap<String,Message> ();
		UnconfirmedMessages = new TreeMap<String,Message> ();
		Recieved			= new TreeMap<String,ArrayList<Message>> ();
		
		Registered			= new ArrayList<String> ();
		
		MD5 = new MD5 ();
		
		ServerStatus = client.ServerStatus.Disconnected;
		new Thread (new ClientThread (this)).start ();
	}
	
	public boolean areUnloggedRequestsQueued () {
		synchronized (UnloggedRequests) {			
			return (UnloggedRequests.Requests.size () > 0);
		}
	}
	
	public void executeRequests () {
		Socket Socket = null;
		
		try {
			synchronized (UnloggedRequests) {
				if (ServerStatus == client.ServerStatus.Alive) {
					synchronized (Requests) {
						UnloggedRequests.addAll (Requests);
						Requests.Requests.clear ();
					}
				} else if (UnloggedRequests.Requests.size () == 0)
					return;
			}	
			
			try {
				Socket = new Socket (Host, Port);
			} catch (SocketException e) {
				handleConnectionError ();
				return;
			}
			Serializer Serializer = new Serializer (Socket);
			
			synchronized (UnloggedRequests) {
				Serializer.write (UnloggedRequests);
				UnloggedRequests.Requests.clear ();
			}
			
			ResponsePackage Responses = (ResponsePackage) Serializer.read ();
			if (Responses != null && Responses.Responses != null)
			for (Response Response : Responses.Responses)
			if (Response != null)
				switch (Response.obtainResponseType ()) {
					case ChangePassword:
						parseResponseChangePassword ((ResponseChangePassword) Response);
					break;
				
					case ConnectionMaintenance:
						parseResponseConnectionMaintenance ((ResponseConnectionMaintenance) Response);
					break;
					
					case GetMessages:
						parseResponseGetMessages ((ResponseGetMessages) Response);
					break;
					
					case GetStatuses:
						parseResponseGetStatuses ((ResponseGetStatuses) Response);
					break;
				
					case Login:
						parseResponseLogin ((ResponseLogin) Response);
					break;
					
					case Logout:
						parseResponseLogout ((ResponseLogout) Response);
					break;
					
					case Register:
						parseResponseRegister ((ResponseRegister) Response);
					break;
					
					case SendMessage:
						parseResponseSendMessage ((ResponseSendMessage) Response);
					break;
					
					case SetStatus:
						parseResponseSetStatus ((ResponseSetStatus) Response);
					break;
				}			
		} catch (ClassNotFoundException e) {
		} catch (EOFException e) {
		} catch (Exception e) {} finally {
			if (Socket != null && !Socket.isClosed ())
				try {
					Socket.close ();
				} catch (IOException e) {}
				
			NewPassword = null;
			
			updateObservers ();
		}
	}
	
	public UserSettings getUserSettings () {
		return new UserSettings (Username, Password, Resource);
	}
	
	public void makeChangePasswordRequest (String newPassword) {
		if (Username == null || Username.isEmpty () ||
			Password == null || Password.isEmpty () ||
			newPassword == null || newPassword.isEmpty () ||
			NewPassword != null) {
			addError (ErrorType.PasswordChange, "Cannot connect if each of: username, password, new password isn't specified.");
			return;
		}
			
		NewPassword = newPassword;
		synchronized (UnloggedRequests) {
			UnloggedRequests.add (new RequestChangePassword (Username, Password, newPassword));
		}
	}
	
	public void makeGetMessagesRequest () {
		synchronized (Requests) {
			Requests.add (new RequestGetMessages ());
		}
	}
	
	public void makeGetStatusesRequest () {
		synchronized (AllContacts) {
			makeGetStatusesRequest (AllContacts);
		}
	}
	
	public void makeGetStatusesRequest (ArrayList<String> users) {
		synchronized (Requests) {
			Requests.add (new RequestGetStatuses (users));
		}
	}
	
	public void makeLoginRequest () {
		if (Username == null || Username.isEmpty () ||
			Password == null || Password.isEmpty () ||
			Resource == null || Resource.isEmpty ()) {
			addError (ErrorType.Login, "Cannot connect if each of: username, password, resource isn't specified.");
			return;
		}
		
		synchronized (UnloggedRequests) {
			UnloggedRequests.add (new RequestLogin (Username, Password, Resource));
		}
	}
	
	public void makeLoginRequest (String username, String password, String resource) {
		setUser (username, password, resource);
		makeLoginRequest ();
	}
	
	public void makeLogoutRequest () {
		synchronized (Requests) {
			Requests.add (new RequestLogout ());
		}
	}
	
	public void makeSendMessageRequest (Message message) {
		String Hash = MD5.hash (message.Content);
		synchronized (UnconfirmedMessages) {
			UnconfirmedMessages.put (Hash, message);
		}
		synchronized (Requests) {
			Requests.add (new RequestSendMessage (message, Hash));
		}
	}

	public void makeSendMessageRequest (String reciever, String content) {
		makeSendMessageRequest (new Message (Username, reciever, content));
	}
	
	public void makeSetStatusRequest (UserStatus status) {
		synchronized (Requests) {
			Requests.add (new RequestSetStatus (status));
		}
	}
	
	public void makeRegisterRequest () {
		if (Username == null || Username.length () == 0 ||
			Password == null || Password.length () == 0) {
			addError (ErrorType.Register, "Cannot register if each of: username, password isn't specified.");
			return;
		}
		
		synchronized (UnloggedRequests) {
			UnloggedRequests.add (new RequestRegister (Username, Password));
		}
	}
	
	public void makeRegisterRequest (String username, String password) {
		setUser (username, password, Resource);
		makeRegisterRequest ();
	}
	
	public void maintainConnection () {
		synchronized (Requests) {
			Requests.add (new RequestConnectionMaintenance (Username, Resource));
		}
	}
	
	public void setUser (UserSettings settings) {
		setUser (settings.Username, settings.Password, settings.Resource);
	}
	
	public void setUser (String username, String password, String resource) {
		Username = username;
		Password = password;
		Resource = resource;
		UpdatedSettings = true;
	}
	
	private void addError (ErrorType type, String message) {
		synchronized (Errors) {
			Errors.put (type, message);
		}
		CheckStatus = true;
		UpdatedErrors = true;
	}
	
	private void handleConnectionError () {
		ServerStatus = client.ServerStatus.Disconnected;
		addError (ErrorType.Connection, "Cannot connect with the server.");
		synchronized (UnloggedRequests) {
			for (Request Request : UnloggedRequests.Requests) {
				switch (Request.obtainRequestType ()) {
					case ChangePassword:
						addError (ErrorType.PasswordChange, "Cannot connect to the server.");
					break;
					
					case ConnectionMaintenance:
						// TODO
					break;
					
					case GetMessages:
						// TODO
					break;
					
					case GetStatuses:
						// TODO
					break;
					
					case Login:
						addError (ErrorType.Login, "Cannot login to the server.");
					break;
					
					case Logout:
						// TODO
					break;
					
					case Register:
						addError (ErrorType.Register, "Cannot connect with the server.");
					break;
					
					case SendMessage:
						// TODO
					break;
					
					case SetStatus:
						// TODO
					break;
				}
			}
			UnloggedRequests.Requests.clear ();
		}
		NewPassword = null;
		updateObservers ();
	}
	
	private void parseResponseChangePassword (ResponseChangePassword response) {
		if (response.ErrorMessage == null || response.ErrorMessage.length () == 0) {
			Password = NewPassword;
			NewPassword = null;
			UpdatedPassword = true;
			System.out.println ("Password changed successfully to '"+Password+"'.");
		} else {
			addError (ErrorType.PasswordChange, response.ErrorMessage);
			System.err.println ("Error at password changing: "+response.ErrorMessage);
		}
	}
	
	private void parseResponseConnectionMaintenance (ResponseConnectionMaintenance response) {
		if (response.ErrorMessage == null || response.ErrorMessage.length () == 0) {
			System.out.println ("Connection maintained.");
		} else {
			addError (ErrorType.Connection, response.ErrorMessage);
			SessionID		= 0;
			ServerStatus	= client.ServerStatus.Disconnected;
			Status			= UserStatus.Offline;
			System.err.println ("Error at connection maintenance: " + response.ErrorMessage);
		}
	}

	private void parseResponseGetMessages (ResponseGetMessages response) {
		if (response.ErrorMessage == null || response.ErrorMessage.length () == 0) {
			if (response.Messages.size () > 0) {
				synchronized (Recieved) {
					for (Message Message : response.Messages)
						if (Message.From != null && Message.From.length () > 0) {
							if (!Recieved.containsKey (Message.From))
								Recieved.put (Message.From, new ArrayList<Message> ());
							Recieved.get (Message.From).add (Message);
							System.out.println ("Added message| "+Message.From+" ("+Message.Date+"): "+Message.Content);
						}
				}
				UpdatedRecieved = true;
				System.out.println ("Messages obtained successfully!");
			}
		} else {
			addError (ErrorType.Recieving, response.ErrorMessage);
			System.err.println ("Error at messages recieving: " + response.ErrorMessage);
		}
	}
	
	private void parseResponseGetStatuses (ResponseGetStatuses response) {
		if (response.ErrorMessage == null || response.ErrorMessage.length () == 0) {
			KnownContacts = response.Users;
			UpdatedContacts = true;
			System.out.println ("Obtained contacts' statuses successfully.");
		} else {
			addError (ErrorType.GetStatuses, response.ErrorMessage);
			System.err.println ("Error at obtaining contacts' statuses: "+response.ErrorMessage);
		}
	}
	
	private void parseResponseLogin (ResponseLogin response) {
		if (response.ErrorMessage == null || response.ErrorMessage.length () == 0) {
			SessionID		= response.SessionID;
			ServerStatus	= client.ServerStatus.Alive;
			Status			= UserStatus.Online;
			UpdatedStatus = true;
			System.out.println ("Logged in with SID: '"+response.SessionID+"'.");
		} else {
			SessionID		= 0;
			ServerStatus	= client.ServerStatus.Disconnected;
			Status			= UserStatus.Offline;
			addError (ErrorType.Login, response.ErrorMessage);
			System.err.println ("Error at logging in: "+response.ErrorMessage);
		}
	}
	
	private void parseResponseLogout (ResponseLogout response) {
		if (response.ErrorMessage == null || response.ErrorMessage.length () == 0) {
			System.out.println ("Logged out.");
		} else {
			addError (ErrorType.Logout, response.ErrorMessage);
			System.err.println ("Error at logging out: "+response.ErrorMessage);
		}
		SessionID		= 0;
		ServerStatus	= client.ServerStatus.Disconnected;
		Status			= UserStatus.Offline;
		UpdatedStatus = true;
	}
	
	private void parseResponseRegister (ResponseRegister response) {
		if (response.ErrorMessage == null || response.ErrorMessage.length () == 0) {
			Registered.add (response.Username);
			UpdatedRegistered = true;
			System.out.println ("Username "+response.Username+" registered successfully.");
		} else {
			addError (ErrorType.Register, response.ErrorMessage);
			System.err.println ("Error at registering: "+response.ErrorMessage);
		}
	}

	private void parseResponseSendMessage (ResponseSendMessage response) {
		if (response.ErrorMessage == null || response.ErrorMessage.length () == 0) {
			synchronized (ConfirmedMessages) {
			synchronized (UnconfirmedMessages) {
				Message Message = UnconfirmedMessages.get (response.MessageHash);
				UnconfirmedMessages.remove (response.MessageHash);
				if (!ConfirmedMessages.containsKey (Message.To))
					ConfirmedMessages.put (Message.To, new ArrayList<Message> ());
				ConfirmedMessages.get (Message.To).add (Message);
			}
			}
			System.out.println ("Message sent successfully.");
		} else {
			synchronized (FailedMessages) {
			synchronized (UnconfirmedMessages) {
				Message Message = UnconfirmedMessages.get (response.MessageHash);
				UnconfirmedMessages.remove (response.MessageHash);
				FailedMessages.put (response.MessageHash, Message);
			}
			}
			addError (ErrorType.PasswordChange, response.ErrorMessage);
			System.err.println ("Error at message sending: "+response.ErrorMessage);
		}
		UpdatedSent = true;
	}
	
	private void parseResponseSetStatus (ResponseSetStatus response) {
		if (response.ErrorMessage == null || response.ErrorMessage.length () == 0) {
			if (Status != UserStatus.Offline)
				Status = response.Status;
			else {
				SessionID		= 0;
				ServerStatus	= client.ServerStatus.Disconnected;
				Status			= UserStatus.Offline;
			}
			UpdatedStatus = true;
			System.out.println ("Status successfully set to '"+Status+"'.");
		} else {
			addError (ErrorType.SetStatus, response.ErrorMessage);
			System.err.println ("Error at setting status: " + response.ErrorMessage);
		}
	}
	
	private void updateObservers () {
		if (UpdatedContacts) {
			setChanged ();
			notifyObservers (ChangedProperty.ContactsStatuses);
			UpdatedContacts = false;
		} if (UpdatedErrors) {
			setChanged ();
			notifyObservers (ChangedProperty.Errors);
			UpdatedErrors = false;
		} if (UpdatedRecieved) {
			setChanged ();
			notifyObservers (ChangedProperty.RecievedMessages);
			UpdatedRecieved = false;
		} if (UpdatedRegistered) {
			setChanged ();
			notifyObservers (ChangedProperty.UsernamesRegistered);
			UpdatedRegistered = false;
		} if (UpdatedPassword) {
			setChanged ();
			notifyObservers (ChangedProperty.Password);
			UpdatedPassword = false;
		} if (UpdatedSent) {
			setChanged ();
			notifyObservers (ChangedProperty.SentMessages);
			UpdatedSent = false;
		} if (UpdatedSettings) {
			setChanged ();
			notifyObservers (ChangedProperty.ClientSettings);
			UpdatedSettings = false;
		} if (UpdatedStatus) {
			setChanged ();
			notifyObservers (ChangedProperty.ClientStatus);
			UpdatedStatus = false;
		}
	}
}
