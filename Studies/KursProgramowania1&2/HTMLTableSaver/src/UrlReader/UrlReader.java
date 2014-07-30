package UrlReader;

import java.net.*;
import java.io.*;

public class UrlReader {
	public static String readUrl (String url)
	throws IOException {
		URL Site = new URL (url);
		BufferedReader Reader = new BufferedReader (
			new InputStreamReader (
				Site.openStream ()
			)
		);
		
		String	Result = "",
				Input;
		
		while ((Input = Reader.readLine ()) != null)
			Result += Input + "\n";
		Reader.close ();
		
		return Result;
	}
	
	public static void main (String[] args) {
		try {
			System.out.println (UrlReader.readUrl ("http://www.cdaction.pl/"));
		} catch (IOException e) {
			System.out.println ("Couldn't read site!");
		}
	}
}
