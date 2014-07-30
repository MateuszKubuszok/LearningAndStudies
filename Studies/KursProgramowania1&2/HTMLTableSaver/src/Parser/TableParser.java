package Parser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TableParser {
	private final String AttributesRawPattern	= "\\s*([a-zA-Z]+(-[a-zA-Z]+)?\\s*=\\s*\"?[^\"]*?\"?\\s*)*";
	private final String CellRawPattern			= "<td"		+ AttributesRawPattern + ">(.*?)</td>";
	private final String RowRawPattern			= "<tr"		+ AttributesRawPattern + ">\\s*(" + CellRawPattern + "\\s*)+</tr>";
	private final String TableRawPattern		= "<table"	+ AttributesRawPattern + ">\\s*(" + RowRawPattern + "\\s*)+</table>";
	
	private Pattern CellPattern;
	private Pattern RowPattern;
	private Pattern TablePattern;
	
	public Pattern getCellPattern () {
		if (CellPattern == null)
			CellPattern = Pattern.compile (CellRawPattern, Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE & Pattern.MULTILINE);
		
		return CellPattern;
	}
	
	public Pattern getRowPattern () {
		if (RowPattern == null)
			RowPattern = Pattern.compile (RowRawPattern, Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE & Pattern.MULTILINE);
		
		return RowPattern;
	}
	
	public Pattern getTablePattern () {
		if (TablePattern == null)
			TablePattern = Pattern.compile (TableRawPattern, Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE & Pattern.MULTILINE);
		
		return TablePattern;
	}
	
	public String[] parseHtml (CharSequence html) {
		ArrayList<String>	Tables = new ArrayList<String> ();
		Matcher				TableMatcher = getTablePattern ().matcher (html);
		
		while (TableMatcher.find ())
			Tables.add (parseTable (html.subSequence (TableMatcher.start (), TableMatcher.end ())));
		
		return (String[]) Tables.toArray (new String [0]);
	}
	
	private String parseTable (CharSequence table) {
		ArrayList<String>	Rows = new ArrayList<String> ();
		Matcher 			RowMatcher = getRowPattern ().matcher (table);
		
		while (RowMatcher.find ())
			Rows.add(parseRow (table.subSequence (RowMatcher.start (), RowMatcher.end ())));
		
		String Result = "";
		
		if (Rows.size () > 0) {
			for (String Sequence : Rows)
				Result += "\n" + Sequence;
			Result = Result.substring (1);
		}
		
		return Result;
	}
	
	private String parseRow (CharSequence row) {
		ArrayList<CharSequence>	Cells = new ArrayList<CharSequence> ();
		Matcher					CellMatcher = getCellPattern ().matcher (row);
		
		while (CellMatcher.find ())
			Cells.add (row.subSequence (CellMatcher.start (3), CellMatcher.end (3)));
		
		String Result = "";
		
		if (Cells.size () > 0) {
			for (CharSequence Sequence : Cells) {
				String String = "" + Sequence;
				Result += ";\"" + String.replace("\"", "\\\"") + "\"";
			}
			Result = Result.substring (1);
		}
		
		return Result;
	}
	
	public static void main (String[] args) {
		String Test =	"<html>" + "\n" +
						"<body>" + "\n" +
						"<table border=\"1\" style=\"text-align: left; margin-left: auto; margin-right: auto;\">" + "\n" +
						"  <tr>" + "\n" +
						"    <td align=\"center\">Temat</td>" + "\n" +
						"    <td align=\"center\">Œrednia</td>" + "\n" +
						"    <td align=\"center\">Standardowe odchylenie</td>" + "\n" +
						"  </tr>" + "\n" +
						"  <tr>" + "\n" +
						"    <td align=\"center\">Programowanie liniowe</td>" + "\n" +
						"    <td align=\"center\">1,92</td>" + "\n" +
						"    <td align=\"center\">1,73</td>" + "\n" +
						"  </tr>" + "\n" +
						"  <tr>" + "\n" +
						"    <td align=\"center\">Algorytmy grafowe</td>" + "\n" +
						"    <td align=\"center\">1,92</td>" + "\n" +
						"    <td align=\"center\">1,55</td>" + "\n" +
						"  </tr>" + "\n" +
						"  <tr>" + "\n" +
						"    <td align=\"center\">Algorytmy wyszukiwania wzorca</td>" + "\n" +
						"    <td align=\"center\">2,04</td>" + "\n" +
						"    <td align=\"center\">1,34</td>" + "\n" +
						"  </tr>" + "\n" +
						"  <tr>" + "\n" +
						"    <td align=\"center\">Algorytmy aproksymacyjne</td>" + "\n" +
						"    <td align=\"center\">2,6</td>" + "\n" +
						"    <td align=\"center\">1,32</td>" + "\n" +
						"  </tr>" + "\n" +
						"  <tr>" + "\n" +
						"    <td align=\"center\">Algorytmy on-line</td>" + "\n" +
						"    <td align=\"center\">3,35</td>" + "\n" +
						"    <td align=\"center\">1,27</td>" + "\n" +
						"  </tr>" + "\n" +
						"  <tr>" + "\n" +
						"    <td align=\"center\">Algorytmy randomizacyjne</td>" + "\n" +
						"    <td align=\"center\">1,84</td>" + "\n" +
						"    <td align=\"center\">1,77</td>" + "\n" +
						"  </tr>" + "\n" +
						"  <tr>" + "\n" +
						"    <td align=\"center\">Algorytmy z powrotami (backtracking)</td>" + "\n" +
						"    <td align=\"center\">3,6</td>" + "\n" +
						"    <td align=\"center\">1,58</td>" + "\n" +
						"  </tr>" + "\n" +
						"  <tr>" + "\n" +
						"    <td align=\"center\">Algorytmy rozproszone - sieci peer-to-peer</td>" + "\n" +
						"    <td align=\"center\">4,68</td>" + "\n" +
						"    <td align=\"center\">0,99</td>" + "\n" +
						"  </tr>" + "\n" +
						"  <tr>" + "\n" +
						"    <td align=\"center\">Algorytmy rozproszone - sieci sensorów</td>" + "\n" +
						"    <td align=\"center\">4,36</td>" + "\n" +
						"    <td align=\"center\">1,35</td>" + "\n" +
						"  </tr>" + "\n" +
						"  <tr>" + "\n" +
						"    <td align=\"center\">Algorytmy dla du¿ych systemów rozproszonych</td>" + "\n" +
						"    <td align=\"center\">4,24</td>" + "\n" +
						"    <td align=\"center\">1,59</td>" + "\n" +
						"  </tr>" + "\n" +
						"  <tr>" + "\n" +
						"    <td align=\"center\">Zarz¹dzanie pamiêci¹ i Garbage Collection</td>" + "\n" +
						"    <td align=\"center\">3,56</td>" + "\n" +
						"    <td align=\"center\">2,22</td>" + "\n" +
						"  </tr>" + "\n" +
						"  <tr>" + "\n" +
						"    <td align=\"center\">Algorytmy nisko-poziomowe</td>" + "\n" +
						"    <td align=\"center\">2,8</td>" + "\n" +
						"    <td align=\"center\">2,66</td>" + "\n" +
						"  </tr>" + "\n" +
						"  <tr>" + "\n" +
						"  <td align=\"center\">Dowodzenie poprawnoœci programów</td>" + "\n" +
						"    <td align=\"center\">3,52</td>" + "\n" +
						"    <td align=\"center\">2,58</td>" + "\n" +
						"  </tr>" + "\n" +
						"  <tr>" + "\n" +
						"  <td align=\"center\">FFT</td>" + "\n" +
						"    <td align=\"center\">3,76</td>" + "\n" +
						"    <td align=\"center\">2,62</td>" + "\n" +
						"  </tr>" + "\n" +
						"</table>" + "\n" +
						"</body>" + "\n" +
						"</html>";
		
		
		new TableParser ().parseHtml (Test);
		//System.out.println (Test);
	}
}
