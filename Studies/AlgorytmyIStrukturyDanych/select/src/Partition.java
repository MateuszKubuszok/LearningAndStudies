import java.util.Random;

public class Partition {
	public static int partition (Container array, int begin, int end, Element Pivot)
	throws ContainerException {
		array.setMessage ("["+Pivot.getCurrentIndex ()+"]="+Pivot.Value+" set as pivot.");
		array.setFollowed (Pivot);
		
		int	i = begin-1,
			j = end+1;
		do {
			do {
				i++;
			} while (array.lesserEqual (i, Pivot.getCurrentIndex ()) && i+1 <= end);
			do {
				j--;
			} while (array.greater (j, Pivot.getCurrentIndex ()) && j-1 >= begin);
			
			if (i < j) {
				array.setMessage ("Swap: ["+i+"]="+array.get (i).Value+" with ["+j+"]="+array.get (j).Value);
				array.swap (i, j);
			}
	    } while (i < j);
		
		array.setMessage ("Move Pivot inbetween branches - swap: ["+i+"]="+array.get (i).Value+" with ["+j+"]="+array.get (j).Value);
		array.swap (j, Pivot.getCurrentIndex ());
		
		return j;
	}
	
	public static int partition (Container array, int begin, int end)
	throws ContainerException {
		return Partition.partition (array, begin, end, array.get (begin));
	}
	
	public static int randomizedPartition (Container array, int begin, int end)
	throws ContainerException {
		Random Rand = new Random ();

		array.setMessage ("Randomize (Randomized-Partition)");
		array.swap (
			begin,
			Rand.nextInt (end-begin+1) + begin
		);
		
		return Partition.partition (
			array,
			begin,
			end
		);
	}
}
