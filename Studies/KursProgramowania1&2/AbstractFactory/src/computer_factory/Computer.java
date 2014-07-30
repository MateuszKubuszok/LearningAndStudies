package computer_factory;

public abstract class Computer {
	protected GPU		GPU;
	protected Processor	Processor;
	protected RAM		RAM;
	protected Screen	Screen;
	
	public GPU getGPU () {
		return GPU;
	}
	
	public Processor getProcessor () {
		return Processor;
	}
	
	public RAM getRAM () {
		return RAM;
	}
	
	public Screen getScreen () {
		return Screen;
	}
}
