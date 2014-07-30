import java.util.Random;

public class RandomizedSelect {
	public static Element select (Container array, int begin, int end, int i)
	throws ContainerException {
		if (begin == end) {
			array.setMessage ("Searched element found: ["+begin+"]="+array.get (begin).Value);
			array.setFollowed (begin);
			return array.get (begin);
		}
	
		int	Pivot = Partition.randomizedPartition (
			array,
			begin,
			end
		);
		
		int LeftLength = Pivot - begin + 1;
		
		if (i == LeftLength) {
			array.setMessage ("Searched element found: ["+Pivot+"]="+array.get (Pivot).Value);
			array.setFollowed (Pivot);
			return array.get (Pivot);
		}
		
		if (i < LeftLength) {
			array.setMessage ("Follow left branch: ["+begin+","+(Pivot-1)+"]");
			return RandomizedSelect.select (array, begin, Pivot-1, i);
		} else {
			array.setMessage ("Follow right branch: ["+(Pivot+1)+","+end+"]");
			return RandomizedSelect.select (array, Pivot+1, end, i-LeftLength);
		}
	}
}
