package graphic_drawer;

import java.awt.Color;
import java.awt.Graphics;

import commands.CommandType;
import commands.DrawCommand;

public class GraphicDrawer {
	public static void draw (DrawCommand command, Graphics gDC) {
		if (command.FirstCoordinate == null || (command.Type != CommandType.Point && command.Type != CommandType.Clear && command.SecondCoordinate == null))
			return;
		
		gDC.setColor (Color.BLACK);
		
		switch (command.Type) {
			case Point:
				gDC.drawLine (
					command.FirstCoordinate.width,
					command.FirstCoordinate.height,
					command.FirstCoordinate.width,
					command.FirstCoordinate.height
				);
			return;
		
			case Line:
				gDC.drawLine (
					command.FirstCoordinate.width,
					command.FirstCoordinate.height,
					command.SecondCoordinate.width,
					command.SecondCoordinate.height
				);
			return;
		
			case Circle:
				int X = Math.abs (command.SecondCoordinate.width - command.FirstCoordinate.width),
					Y = Math.abs (command.SecondCoordinate.height - command.FirstCoordinate.height),
					Diagonal = (int) Math.sqrt ((double) (X*X + Y*Y));
				
				gDC.drawOval (
					command.FirstCoordinate.width - Diagonal,
					command.FirstCoordinate.height - Diagonal,
					Diagonal*2,
					Diagonal*2
				);
			return;
		
			case Rectangle:
				gDC.drawRect (
					Math.min (command.FirstCoordinate.width, command.SecondCoordinate.width),
					Math.min (command.FirstCoordinate.height, command.SecondCoordinate.height),
					Math.abs (command.SecondCoordinate.width - command.FirstCoordinate.width),
					Math.abs (command.SecondCoordinate.height - command.FirstCoordinate.height)
				);
			return;
			
			case Clear:
				gDC.setColor (Color.WHITE);
				gDC.fillRect (0, 0, command.FirstCoordinate.width, command.FirstCoordinate.height);
			return;
		}
	}
}
