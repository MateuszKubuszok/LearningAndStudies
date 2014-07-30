public class RandomizedSelectTest {
	public static void main(String[] args) {
		try {
			RunHelper.parseArgs (args);
		} catch (IllegalArgumentException ex) {
			System.out.println (ex.getMessage ());
			return;
		}
		
		int[] OriginalArray = RunHelper.Array;
		int I = RunHelper.I;
		
		Container		Array = new Container (OriginalArray),
						SelectCopy = Array.clone ();
		
		//ContainerViewer ConsoleSelect = new ContainerViewer (SelectCopy, false);
		GraphFrame 		GraphSelect = new GraphFrame (SelectCopy, "RandomizedSelect");
		
		SelectCopy.informAll ();
		
		try {
			RandomizedSelect.select (SelectCopy, 1, SelectCopy.size (), I);
		} catch (ContainerException ex) {
			System.out.println (ex);
		}
		
		RunHelper.waitFor (1000);
		
		Container 		SortCopy = SelectCopy.clone ();
		SortCopy.lockFollowed ();
		
		//ContainerViewer ConsoleSort = new ContainerViewer (SortCopy, false);
		GraphFrame 		GraphSort = new GraphFrame (SortCopy, "InsertionSort");
		
		//SortCopy.setDelay (0);
		SortCopy.informAll ();
		
		try {
			Sort.insertionSort (SortCopy);;
			SortCopy.setMessage (I+". element of the given array is ["+I+"]="+SortCopy.get (I).Value);
		} catch (ContainerException ex) {
			System.out.println (ex);
		}
	}
}
