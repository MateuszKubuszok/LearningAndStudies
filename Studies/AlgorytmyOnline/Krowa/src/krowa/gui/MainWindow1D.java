package krowa.gui;

import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static krowa.algo1d.StartDirection.LEFT;
import static krowa.algo1d.StartDirection.RANDOM;
import static krowa.algo1d.StartDirection.RIGHT;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import javax.swing.JRadioButton;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JButton;

import krowa.algo1d.StartDirection;
import krowa.execution.Draw1DExecution;

public class MainWindow1D {
	private final JFrame frame;
	private final JRadioButton startLeft;
	private final JRadioButton startRight;
	private final JRadioButton startRandom;
	private final JButton startButton;
	private final JPanel panel;

	public MainWindow1D() {
		frame = new JFrame();
		frame.setTitle("Cow's searching algorithm for 1D");
		frame.setResizable(false);
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		frame.getContentPane().add(panel, gbc_panel);
		
		JLabel filler = new JLabel("Where farmer should start");
		GridBagConstraints gbc_filler = new GridBagConstraints();
		gbc_filler.gridheight = 3;
		gbc_filler.insets = new Insets(0, 0, 5, 5);
		gbc_filler.gridx = 0;
		gbc_filler.gridy = 1;
		frame.getContentPane().add(filler, gbc_filler);
		
		startLeft = new JRadioButton("Start left");
		GridBagConstraints gbc_startLeft = new GridBagConstraints();
		gbc_startLeft.insets = new Insets(0, 0, 5, 0);
		gbc_startLeft.gridx = 1;
		gbc_startLeft.gridy = 1;
		frame.getContentPane().add(startLeft, gbc_startLeft);
		
		startRight = new JRadioButton("Start right");
		GridBagConstraints gbc_startRight = new GridBagConstraints();
		gbc_startRight.insets = new Insets(0, 0, 5, 0);
		gbc_startRight.gridx = 1;
		gbc_startRight.gridy = 2;
		frame.getContentPane().add(startRight, gbc_startRight);
		
		startRandom = new JRadioButton("Start random");
		startRandom.setSelected(true);
		GridBagConstraints gbc_startRandom = new GridBagConstraints();
		gbc_startRandom.insets = new Insets(0, 0, 5, 0);
		gbc_startRandom.gridx = 1;
		gbc_startRandom.gridy = 3;
		
		frame.getContentPane().add(startRandom, gbc_startRandom);
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(startLeft);
		buttonGroup.add(startRight);		
		buttonGroup.add(startRandom);		
		
		startButton = new JButton("Start animation");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 4;
		frame.getContentPane().add(startButton, gbc_btnNewButton);
		
		final MainWindow1D thisWindow = this;
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Draw1DExecution(thisWindow)).start();
			}
		});
		
		frame.setVisible(true);
	}

	public synchronized StartDirection getStartDirection() {
		if (startLeft.isSelected())
			return LEFT;
		else if (startRight.isSelected())
			return RIGHT;
		else if (startRandom.isSelected())
			return RANDOM;
		throw new RuntimeException("Where is Your god now?");
	}
	
	public synchronized void setEnabled(boolean enabled) {
		startButton.setEnabled(enabled);
		startLeft.setEnabled(enabled);
		startRight.setEnabled(enabled);
		startRandom.setEnabled(enabled);
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	public JPanel getPanel() {
		return panel;
	}
}
