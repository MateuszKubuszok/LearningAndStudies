package krowa.gui;

import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static krowa.algo2d.RotationDirection.CLOCKWISE;
import static krowa.algo2d.RotationDirection.COUNTER_CLOCKWISE;
import static krowa.algo2d.StartDirection.TOP;
import static krowa.algo2d.StartDirection.RIGHT;
import static krowa.algo2d.StartDirection.BOTTOM;
import static krowa.algo2d.StartDirection.LEFT;

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

import krowa.algo2d.RotationDirection;
import krowa.algo2d.StartDirection;
import krowa.execution.Draw2DExecution;

public class MainWindow2D {
	private final JFrame frame;
	private final JRadioButton startLeft;
	private final JRadioButton startRight;
	private final JRadioButton startRandomDirection;
	private final JButton startButton;
	private final JPanel panel;
	private final JRadioButton startTop;
	private final JRadioButton startBottom;
	private final JRadioButton startClockwise;
	private final JRadioButton startCounterClockwise;
	private final JRadioButton startGoRandom;

	public MainWindow2D() {
		frame = new JFrame();
		frame.setTitle("Cow's searching algorithm for 2D");
		frame.setResizable(false);
		frame.setBounds(200, 200, 800, 600);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 3;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		frame.getContentPane().add(panel, gbc_panel);
		
		JLabel filler = new JLabel("How farmer should start and move");
		GridBagConstraints gbc_filler = new GridBagConstraints();
		gbc_filler.gridheight = 3;
		gbc_filler.insets = new Insets(0, 0, 0, 5);
		gbc_filler.gridx = 0;
		gbc_filler.gridy = 3;
		frame.getContentPane().add(filler, gbc_filler);	
		
		startClockwise = new JRadioButton("Move clockwise");
		GridBagConstraints gbc_startClockwise = new GridBagConstraints();
		gbc_startClockwise.insets = new Insets(0, 0, 5, 0);
		gbc_startClockwise.gridx = 2;
		gbc_startClockwise.gridy = 1;
		frame.getContentPane().add(startClockwise, gbc_startClockwise);
		
		startCounterClockwise = new JRadioButton("Move counter clockwise");
		GridBagConstraints gbc_startCounterClockwise = new GridBagConstraints();
		gbc_startCounterClockwise.insets = new Insets(0, 0, 5, 0);
		gbc_startCounterClockwise.gridx = 2;
		gbc_startCounterClockwise.gridy = 2;
		frame.getContentPane().add(startCounterClockwise, gbc_startCounterClockwise);
		
		startGoRandom = new JRadioButton("Move randomly");
		startGoRandom.setSelected(true);
		GridBagConstraints gbc_rdbtnGoRandom = new GridBagConstraints();
		gbc_rdbtnGoRandom.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnGoRandom.gridx = 2;
		gbc_rdbtnGoRandom.gridy = 3;
		frame.getContentPane().add(startGoRandom, gbc_rdbtnGoRandom);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(startClockwise);
		buttonGroup.add(startCounterClockwise);
		buttonGroup.add(startGoRandom);	
		
		startTop = new JRadioButton("Start top");
		GridBagConstraints gbc_rdbtnStartTop = new GridBagConstraints();
		gbc_rdbtnStartTop.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnStartTop.gridx = 1;
		gbc_rdbtnStartTop.gridy = 1;
		frame.getContentPane().add(startTop, gbc_rdbtnStartTop);
		
		startRight = new JRadioButton("Start right");
		GridBagConstraints gbc_startRight = new GridBagConstraints();
		gbc_startRight.insets = new Insets(0, 0, 5, 5);
		gbc_startRight.gridx = 1;
		gbc_startRight.gridy = 2;
		frame.getContentPane().add(startRight, gbc_startRight);
		
		startBottom = new JRadioButton("Start bottom");
		GridBagConstraints gbc_rdbtnStartbottom = new GridBagConstraints();
		gbc_rdbtnStartbottom.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnStartbottom.gridx = 1;
		gbc_rdbtnStartbottom.gridy = 3;
		frame.getContentPane().add(startBottom, gbc_rdbtnStartbottom);
		
		startLeft = new JRadioButton("Start left");
		GridBagConstraints gbc_startLeft = new GridBagConstraints();
		gbc_startLeft.insets = new Insets(0, 0, 5, 5);
		gbc_startLeft.gridx = 1;
		gbc_startLeft.gridy = 4;
		frame.getContentPane().add(startLeft, gbc_startLeft);
		
		startRandomDirection = new JRadioButton("Start random");
		startRandomDirection.setSelected(true);
		GridBagConstraints gbc_startRandom = new GridBagConstraints();
		gbc_startRandom.insets = new Insets(0, 0, 0, 5);
		gbc_startRandom.gridx = 1;
		gbc_startRandom.gridy = 5;
		frame.getContentPane().add(startRandomDirection, gbc_startRandom);
		
		ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(startTop);		
		buttonGroup2.add(startRight);		
		buttonGroup2.add(startBottom);		
		buttonGroup2.add(startLeft);
		buttonGroup2.add(startRandomDirection);		
		
		startButton = new JButton("Start animation");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 5;
		frame.getContentPane().add(startButton, gbc_btnNewButton);
		
		final MainWindow2D thisWindow = this;
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Draw2DExecution(thisWindow)).start();
			}
		});
		
		frame.setVisible(true);
	}

	public synchronized RotationDirection getRotationDirection() {
		if (startClockwise.isSelected())
			return CLOCKWISE;
		else if (startCounterClockwise.isSelected())
			return COUNTER_CLOCKWISE;
		else if (startGoRandom.isSelected())
			return RotationDirection.random();
		throw new RuntimeException("Where is Your god now?");
	}
	
	public synchronized StartDirection getStartDirection() {
		if (startTop.isSelected())
			return TOP;
		else if (startRight.isSelected())
			return RIGHT;
		else if (startBottom.isSelected())
			return BOTTOM;
		else if (startLeft.isSelected())
			return LEFT;
		else if (startRandomDirection.isSelected())
			return StartDirection.random();
		throw new RuntimeException("Where is Your god now?");
	}
	
	public synchronized void setEnabled(boolean enabled) {
		startButton.setEnabled(enabled);
		startTop.setEnabled(enabled);
		startRight.setEnabled(enabled);
		startBottom.setEnabled(enabled);
		startLeft.setEnabled(enabled);
		startRandomDirection.setEnabled(enabled);
		startClockwise.setEnabled(enabled);
		startCounterClockwise.setEnabled(enabled);
		startGoRandom.setEnabled(enabled);
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	public JPanel getPanel() {
		return panel;
	}
}
