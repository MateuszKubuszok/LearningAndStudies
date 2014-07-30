package main;

public class Main {
	private static void printComputer (computer_factory.Computer computer){
		System.out.println ("GPU:       " + computer.getGPU ().getSpec ());
		System.out.println ("Processor: " + computer.getProcessor ().getSpec ());
		System.out.println ("RAM:       " + computer.getRAM ().getSpec ());
		System.out.println ("Screen:    " + computer.getScreen ().getSpec ());
	} 
	
	public static void main (String[] args) {
		System.out.println ("PC specification:");
		printComputer (new pc.PC ());
		
		System.out.println ();
		
		System.out.println ("Server specification:");
		printComputer (new server.Server ());
		
		System.out.println ();
		
		System.out.println ("Workstation specification:");
		printComputer (new workstation.Workstation ());
	}
}
