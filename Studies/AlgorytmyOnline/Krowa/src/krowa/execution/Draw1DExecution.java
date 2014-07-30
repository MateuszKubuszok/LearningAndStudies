package krowa.execution;

import static java.lang.Thread.sleep;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.SwingUtilities.invokeLater;

import javax.swing.JPanel;

import krowa.algo1d.Algo1D;
import krowa.gui.MainWindow1D;

public class Draw1DExecution implements Runnable {
	private final MainWindow1D window;
	
	public Draw1DExecution(MainWindow1D window) {
		this.window = window;
	}

	@Override
	public void run() {
		invokeLater(new Runnable() {
			@Override
			public void run() {				
				window.setEnabled(false);
			}
		});	
		
		JPanel drawingPanel = window.getPanel();
		
		Algo1D algo = new Algo1D(window.getStartDirection());
		algo.drawNextStep(drawingPanel);
		
		while (!algo.isCowReached()) {
			algo.makeNextStep();
			algo.drawNextStep(drawingPanel);
			try {
				sleep(500);
			} catch (InterruptedException e) {
			}
		}
		
		showMessageDialog(
				window.getFrame(),
				"Optimal distance: " + algo.getCowDistance() + "\n" +
				"Farmer's distance: " + algo.getFarmerDistance() + "\n" +
				"Ratio: " + algo.getFarmerDistance()/algo.getCowDistance());
		
		invokeLater(new Runnable() {
			@Override
			public void run() {				
				window.setEnabled(true);
			}
		});	
	}
}
