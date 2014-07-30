package searcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

import gui.Printer;

public class Wanderer {
	private final String AllowedInURL = "[\\w;/?:@=&$_.+!*'(),-]";	
	private final String LinkRawPattern = "href\\s*=\\s*(\")?(" + AllowedInURL + "*)(\")?";
	
	private String	Address;
	private String	RawPattern;
	private int		Depth;
	private boolean	CheckPattern;
	
	private Pattern Pattern;
	private Pattern LinkPattern;
	
	private Set<String> Visited;
	
	private Printer Printer;
	
	public Wanderer (String address, String pattern, int depth) {
		Address = address;
		RawPattern = pattern;
		Depth = depth;
		CheckPattern = RawPattern != null && RawPattern.length () > 0;
	}
	
	public void parse () {
		Visited = new TreeSet<String> ();
		
		if (CheckPattern)
			try {
				getPattern ();
			} catch (PatternSyntaxException e) {
				println ("Pattern syntax error in: \"" + RawPattern + "\"");
				return;
			}
		
		parse (Address, Depth);
	}
	
	public void setPrinter (Printer printer) {
		Printer = printer;
	}
	
	private Pattern getPattern ()
	throws PatternSyntaxException {
		if (Pattern == null)
			Pattern = java.util.regex.Pattern.compile (RawPattern, java.util.regex.Pattern.CASE_INSENSITIVE & java.util.regex.Pattern.MULTILINE & java.util.regex.Pattern.UNICODE_CASE);
		
		return Pattern;
	}
	
	private Pattern getLinkPattern () {
		if (LinkPattern == null)
			LinkPattern = java.util.regex.Pattern.compile (LinkRawPattern, java.util.regex.Pattern.CASE_INSENSITIVE & java.util.regex.Pattern.MULTILINE & java.util.regex.Pattern.UNICODE_CASE);
		
		return LinkPattern;
	}
	
	private String readSite (String address)
	throws IOException {
		URL Site = new URL (address);
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
	
	private void parse (String address, int depth) {
		String	Site,
				Tab = depthTab (depth);
		
		try {
			Site = readSite (address);
		} catch (IOException e) {
			println (Tab + "Cannot reach address: \"" + address + "\"");
			return;
		}
		
		println (Tab + "Adress: \"" + address + "\"");
		
		if (CheckPattern) {
			Matcher Matcher = getPattern ().matcher (Site);
			ArrayList<String> Matches = new ArrayList<String> ();
			
			while (Matcher.find ())
				Matches.add (Site.substring (Matcher.start(), Matcher.end ()));
			
			if (Matches.size () > 0) {
				println (Tab + "Matches (" + Matches.size () + "): ");
				for (String Match : Matches)
					println (Tab + " - \"" + Match +"\"");
			} else
				println (Tab + "No matches found.");
		}
		
		Matcher LinkMatcher = getLinkPattern ().matcher (Site);
		TreeSet<String> Sites = new TreeSet<String> ();
		
		URI URI = null;
		try {
			URI = new URL (address).toURI ();
		} catch (Exception e) {}
		
		while (LinkMatcher.find ()) {
			String Link = Site.substring (LinkMatcher.start(2), LinkMatcher.end (2));
			
			if (Link.startsWith ("javascript"))
				continue;
			
			if (Link.startsWith ("\"") || Link.startsWith ("'"))
				Link = Link.substring (1);
			if (Link.endsWith ("\"") || Link.endsWith ("'"))
				Link = Link.substring (0, Link.length ()-2);
			
			try {
				if (URI != null && !Link.startsWith ("http://") && !Link.startsWith ("https://")) {
					String	Prefix = address.startsWith ("https") ? "https://" : "http://",
							Authority = URI.resolve (Link).getAuthority (),
							Path = URI.resolve (Link).getPath ();
					
					if (!Path.startsWith ("/"))	
						Path = "/" + Path;
						
					Link = Prefix + Authority + Path;
				}
			} catch (Exception e) {}
			
			if (Link.length () > 0)
				Sites.add (Link);
		}
			
		String[] aSites = Sites.toArray (new String[0]);
			
		if (aSites.length > 0) {
			println (Tab + "Subsites (" + Sites.size () + ", only non-visited before will be followed): ");
			for (String SubSite : aSites)
				println (Tab + " - \"" + SubSite +"\"");
		} else
			println (Tab + "No subsites found.");
			
		for (String SubSite : aSites) {
			if (Visited.contains (SubSite))
				continue;
				
			Visited.add (SubSite);
			
			if (depth > 0) {
				println ("");
				parse (SubSite, depth-1);
			}
		}
	}
	
	private String depthTab (int depth) {
		String Result = "";
		
		for (int i = Depth-depth; i > 0; i--)
			Result += "\t";
		
		return Result;
	}
	
	private void print (String str) {
		if (Printer != null)
			Printer.print (str);
		else
			System.out.print (str);
	}
	
	private void println (String str) {
		if (Printer != null)
			Printer.println (str);
		else
			System.out.println (str);
	}
	
	public static void main (String[] args) {
		Wanderer test = new Wanderer (
			"http://www.cdaction.pl/",
			"(((",
			2
		);
		
		test.parse ();
	}
}
