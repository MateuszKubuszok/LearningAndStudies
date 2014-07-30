public class Select {
	public static Element select (Container array, int begin, int end, int i)
	throws ContainerException {
		if (begin == end) {
			array.setMessage ("Searched element found: ["+begin+"]="+array.get (begin).Value);
			array.setFollowed (begin);
			return array.get (begin);
		}
		
		Element Mediana = Select.medianaOfMedians (array, begin, end);
		array.setFollowed (Mediana);
		array.setMessage ("Mediana of medians: " + Mediana.Value);
		
		int	Pivot = Partition.partition (
			array,
			begin,
			end,
			Mediana
		);
			
		int LeftLength = Pivot - begin + 1; 
		
		if (i == LeftLength) {
			array.setMessage ("Searched element found: ["+Pivot+"]="+array.get (Pivot).Value);
			array.setFollowed (Pivot);
			return array.get (Pivot);
		}
		
		if (i < LeftLength) {
			array.setMessage ("Follow left branch: ["+begin+","+(Pivot-1)+"]");
			return Select.select (array, begin, Pivot-1, i);
		} else {
			array.setMessage ("Follow right branch: ["+(Pivot+1)+","+end+"]");
			return Select.select (array, Pivot+1, end, i-LeftLength);
		}
	}
	
	public static Element mediana (Element[] array) {
		Sort.insertionSort (array);
		return array [array.length / 2];
	}
	
	public static Element medianaOfMedians (Element[] Array)
	throws ContainerException {
		int Size = Array.length;
		int I, J, k;
		
		// Obliczanie mediany pe³nych/pocz¹tkowych grup
		
		int LastGroupSize = Size % 5, // Rozmiar ostatniej grupy
			GroupsNumber = Size / 5 + (LastGroupSize > 0 ? 1 : 0); // Liczba wszystkich grup
		
		// Warunek przerwania (pojedynczy kube³ek)
		if (GroupsNumber == 1)
			return Select.mediana (Array);
		
		Element[]	Medians = new Element [GroupsNumber], // Mediany grup
					Group = new Element[5]; // Pojedyncza grupa
		
		for (I = 1; I < GroupsNumber; I++) {
			k = (I-1)*5;
			
			for (J = 0; J < 5; J++)
				Group [J] = Array [J + k];
			
			Medians [I-1] = Select.mediana (Group);
		}
		
		// Mediana ostatniej grupy
		if (LastGroupSize == 0)
			LastGroupSize = 5;
		Group = new Element[LastGroupSize];
		k = Size - LastGroupSize;
		
		for (J = 0; J < LastGroupSize; J++)
			Group [J] = Array [J + k];
		
		Medians [GroupsNumber-1] = Select.mediana (Group);
		
		// Obliczanie i zwracanie mediany median
		return Select.medianaOfMedians (Medians);
	}
	
	public static Element medianaOfMedians (Container array, int begin, int end)
	throws ContainerException {
		int I;
		
		int Size = end-begin+1;
	
		Element[] Array = new Element[Size];
		for (I = 0; I < Size; I++) {
			try {
				Array [I] = array.get (begin + I);
			} catch (ContainerException ex) {}
		}

		return Select.medianaOfMedians (Array);
	}
	
	/*
	 public static Element medianaOfMedians (Container array, int begin, int end)
	throws ContainerException {
		int I, J, k, l;
		
		int Size = end-begin+1;
	
		Element[] Array = new Element[Size];
		for (I = 0; I < Size; I++) {
			try {
				Array [I] = array.get (begin + I);
			} catch (ContainerException ex) {}
		}

		// Obliczanie mediany pe³nych/pocz¹tkowych grup
		
		int LastGroupSize = Size % 5, // Rozmiar ostatniej grupy
			GroupsNumber = Size / 5 + (LastGroupSize > 0 ? 1 : 0); // Liczba wszystkich grup
		
		Element[]	Medians = new Element [GroupsNumber], // Mediany grup
					Group = new Element[5]; // Pojedyncza grupa
		
		
		for (I = 1; I < GroupsNumber; I++) {
			k = (I-1)*5;
			
			for (J = 0; J < 5; J++)
				Group [J] = Array [J + k];
			
			Medians [I-1] = Select.mediana (Group);
		}
		
		// Mediana ostatniej grupy
		if (LastGroupSize == 0)
			LastGroupSize = 5;
		Group = new Element[LastGroupSize];
		k = Size - LastGroupSize;
		
		for (J = 0; J < LastGroupSize; J++)
			Group [J] = Array [J + k];
		
		Medians [GroupsNumber-1] = Select.mediana (Group);
		
		// Obliczanie i zwracanie mediany median
		return Select.mediana (Medians);
	} 
	 */
}
