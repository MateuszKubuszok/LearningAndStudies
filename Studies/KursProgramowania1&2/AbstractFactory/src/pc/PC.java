package pc;

public class PC extends computer_factory.Computer {
	public PC () {
		GPU			= new GPU ();
		Processor	= new Processor ();
		RAM			= new RAM ();
		Screen		= new Screen ();
	}
	
	public class GPU extends computer_factory.GPU {
		public String getSpec () {
			return "Radeon";
		}
	}
	
	public class Processor extends computer_factory.Processor {
		public String getSpec () {
			return "Athlon";
		}
	}
	
	public class RAM extends computer_factory.RAM {
		public String getSpec () {
			return "Kingston 2GB";
		}
	}
	
	public class Screen extends computer_factory.Screen {
		public String getSpec () {
			return "Acer";
		}
	}
}
