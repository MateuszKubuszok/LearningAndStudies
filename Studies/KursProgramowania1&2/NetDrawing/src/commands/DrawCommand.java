package commands;

import java.awt.Dimension;

public class DrawCommand implements Command {
	public CommandType	Type = CommandType.Point;
	public Dimension	FirstCoordinate = null;
	public Dimension	SecondCoordinate = null;
	
	public DrawCommand () {
		Type = CommandType.Point;
		FirstCoordinate = new Dimension ();
		SecondCoordinate = new Dimension ();
	}
	
	public DrawCommand (CommandType type, Dimension firstCoordinate, Dimension secondCoordinate) {
		Type = type;
		FirstCoordinate = firstCoordinate;
		SecondCoordinate = secondCoordinate;
	}
	
	/*
	public int getFirstX () {
		return FirstCoordinate.width;
	}
	
	public int getFirstY () {
		return FirstCoordinate.height;
	}
	
	public int getSecondX () {
		return SecondCoordinate.width;
	}
	
	public int getSecondY () {
		return SecondCoordinate.height;
	}
	
	public void setFirstX (int x) {
		if (FirstCoordinate == null)
			FirstCoordinate = new Dimension ();
		FirstCoordinate.width = x;
	}
	
	public void setFirstY (int y) {
		if (FirstCoordinate == null)
			FirstCoordinate = new Dimension ();
		FirstCoordinate.height = y;
	}
	
	public void setSecondX (int x) {
		if (SecondCoordinate == null)
			SecondCoordinate = new Dimension ();
		SecondCoordinate.width = x;
	}
	
	public void setSecondY (int y) {
		if (SecondCoordinate == null)
			SecondCoordinate = new Dimension ();
		SecondCoordinate.height = y;
	}
	 */
	public Dimension getFirstCoordinate () {
		return FirstCoordinate;
	}
	
	public Dimension getSecondCoordinate () {
		return SecondCoordinate;
	}
	
	public CommandType getType () {
		return Type;
	}
	
	public void setFirstCoordinate (Dimension coordinate) {
		FirstCoordinate = coordinate;
	}
	
	public void setSecondCoordinate (Dimension coordinate) {
		SecondCoordinate = coordinate;
	}
	
	public void setType (CommandType type) {
		Type = type;
	}
}
