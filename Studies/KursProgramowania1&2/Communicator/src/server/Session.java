package server;

import java.util.ArrayList;
import java.util.Date;

import data.Message;
import data.ServerUser;
import data.UserStatus;

public class Session {
	public String				Username;
	public String				Resource;
	public int					ID;
	public Date					Date;
	public ServerUser			User;
	public ArrayList<Message>	Messages;
	public UserStatus			Status;	
	
	public Session () {
		Messages = new ArrayList<Message> ();
		Status = UserStatus.Offline;
	}
}
