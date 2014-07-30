package server;

public class Server extends computer_factory.Computer {
	public Server () {
		GPU			= new GPU ();
		Processor	= new Processor ();
		RAM			= new RAM ();
		Screen		= new Screen ();
	}
	
	public class GPU extends computer_factory.GPU {
		public String getSpec () {
			return "nVidia";
		}
	}
	
	public class Processor extends computer_factory.Processor {
		public String getSpec () {
			return "Intel";
		}
	}
	
	public class RAM extends computer_factory.RAM {
		public String getSpec () {
			return "Kingston 4GB";
		}
	}
	
	public class Screen extends computer_factory.Screen {
		public String getSpec () {
			return "Samsung";
		}
	}
}
