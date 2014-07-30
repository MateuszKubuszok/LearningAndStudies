package workstation;

public class Workstation extends computer_factory.Computer {
	public Workstation () {
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
			return "Intel i7";
		}
	}
	
	public class RAM extends computer_factory.RAM {
		public String getSpec () {
			return "Kingston 8GB";
		}
	}
	
	public class Screen extends computer_factory.Screen {
		public String getSpec () {
			return "Toshiba";
		}
	}
}
