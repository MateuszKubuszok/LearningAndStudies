public class Element {
	public 	int Value;
	
	private int CurrentIndex;
	
	private int OriginalIndex;
	
	public Element (int value, int index) {
		this.Value 			= value;
		this.CurrentIndex 	= index;
		this.OriginalIndex 	= index;
	}
	
	public int getCurrentIndex () {
		return this.CurrentIndex;
	}
	
	public int getOriginalIndex () {
		return this.OriginalIndex;
	}
	
	public void setCurrentIndex (int index) {
		this.CurrentIndex = index;
	}
}
