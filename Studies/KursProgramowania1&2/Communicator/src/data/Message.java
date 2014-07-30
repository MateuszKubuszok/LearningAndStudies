package data;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class Message implements Comparable<Message>, Comparator<Message>, Serializable {
	private static final long serialVersionUID = 3623058018557499341L;
	
	public String	From;
	public String	To;
	public String	Content;
	public Date		Date;
	
	public Message () {
		From = "";
		To = "";
		Content = "";
	}
	
	public Message (String from, String to, String content) {
		From = from;
		To = to;
		Content = content;
		Date = new Date ();
	}
	
	public int compare (Message message1, Message message2) {
		return (int) (message1.Date.getTime () - message2.Date.getTime ());
	}
	
	public int compareTo (Message message) {
		return compare (this, message);
	}
	
	public boolean equals (Object message) {
		try {
			return compareTo ((Message) message) == 0;
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	public String getContent () {
		return Content;
	}
	
	public Date getDate () {
		return Date;
	}
	
	public String getFrom () {
		return From;
	}
	
	public String getTo () {
		return To;
	}
	
	public void setContent (String content) {
		Content = content;
	}
	
	public void setDate (Date date) {
		Date = date;
	}
	
	public void setFrom (String from) {
		From = from;
	}
	
	public void setTo (String to) {
		To = to;
	}
}
