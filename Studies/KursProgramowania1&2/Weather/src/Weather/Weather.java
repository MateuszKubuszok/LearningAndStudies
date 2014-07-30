package Weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Weather {
	private final String Address = "http://www.weather.com/outlook/travel/businesstraveler/local/PLXX0029";
	
	private final String ImageRawPattern	=	"<td class=\"twc-col-1 twc-animated-icon\" rowspan=\"2\">\\s*" +
												"<span class=\"twc-forecast-when twc-none\">Right Now</span>\\s*" +
												"<img src=\"(http://[a-zA-Z0-9\\./-]+)\" alt=\"[a-zA-Z ]+\" width=\"130\" height=\"130\">";
	private final String TempRawPattern		=	"<td class=\"twc-col-1 twc-forecast-temperature\"><strong>(-?[0-9]+)&deg;F</strong></td>";
	private final String FeelTempRawPattern	=	"<td class=\"twc-col-1 twc-forecast-temperature-info\">Feels Like: <strong>(-?[0-9]+)&deg;</strong>";
	
	private Pattern ImagePattern;
	private Pattern TempPattern;
	private Pattern FeelTempPattern;
	
	private String	ImagePath;
	private int		Temp;
	private int		FeelTemp;
	
	public void obtain ()
	throws IOException {
		parseSite (readSite ());
	}
	
	public String getImagePath () {
		return ImagePath;
	}
	
	public int getTemp () {
		return Temp;
	}
	
	public int getFeelTemp () {
		return FeelTemp;
	}
	
	private Pattern getImagePattern () {
		if (ImagePattern == null)
			ImagePattern = Pattern.compile (ImageRawPattern, Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE & Pattern.MULTILINE);
		
		return ImagePattern;
	}
	
	private Pattern getTempPattern () {
		if (TempPattern == null)
			TempPattern = Pattern.compile (TempRawPattern, Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE & Pattern.MULTILINE);
		
		return TempPattern;
	}
	
	private Pattern getFeelTempPattern () {
		if (FeelTempPattern == null)
			FeelTempPattern = Pattern.compile (FeelTempRawPattern, Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE & Pattern.MULTILINE);
		
		return FeelTempPattern;
	}
	
	private void parseSite (String site) {
		Matcher ImageMatcher = getImagePattern ().matcher (site),
				TempMatcher = getTempPattern ().matcher (site),
				FeelTempMatcher = getFeelTempPattern ().matcher (site);
		
		if (ImageMatcher.find ())
			ImagePath = site.substring (ImageMatcher.start (1), ImageMatcher.end (1));
		if (TempMatcher.find ())
			Temp = (int) Math.round (5.0/9.0 * (((double) Integer.parseInt (site.substring (TempMatcher.start (1), TempMatcher.end (1)))) - 32.0));
		if (FeelTempMatcher.find ())
			FeelTemp = (int) Math.round (5.0/9.0 * (((double) Integer.parseInt (site.substring (FeelTempMatcher.start (1), FeelTempMatcher.end (1)))) - 32.0));
	}
	
	private String readSite ()
	throws IOException {
		URL Site = new URL (Address);
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
		Weather Weather = new Weather ();
		
		try {
			Weather.obtain ();
			
			System.out.println ("Img "+Weather.getImagePath ());
			System.out.println ("Temp " + Weather.getTemp ());
			System.out.println ("FeelTemp " + Weather.getFeelTemp ());
		} catch (IOException e) {
			System.out.println ("Coundn't obtain data!");
		}
	}
}
