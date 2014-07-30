package drawer_gui;

import graphic_drawer.GraphicDrawer;

import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Event;

import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;
import javax.swing.KeyStroke;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JFrame;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.JButton;

import commands.CommandType;
import commands.DrawCommand;
import controller.Controller;

import java.awt.Dimension;
import java.util.Vector;

public class DrawerWindow {
	private Controller Controller;
	
	private JFrame jFrame = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	private JMenuItem exitMenuItem = null;
	private JMenuItem connectMenuItem = null;
	private JPanel Content = null;
	private JPanel Toolbar = null;
	private JButton Point = null;
	private JButton Line = null;
	private JButton Circle = null;
	private JButton Rectangle = null;
	public int Width;
	public int Height;
	private Dimension	Start = null;  //  @jve:decl-index=0:
	private Dimension	Middle = null;  //  @jve:decl-index=0:
	private Dimension	Stop = null;  //  @jve:decl-index=0:
	private boolean		Clear = false;
	private Image OriginalImage = null;
	private CommandType Type = CommandType.Point;  //  @jve:decl-index=0:
	private JMenuItem clearItem = null;
	
	public DrawerWindow () {
		Controller = new Controller ();
		Controller.setWindow (this);
		getJFrame ();
		setSize (640, 480);
	}
	
	public void setMiddle (Dimension middle) {
		Middle = middle;
	}
	
	public void setStart (Dimension start) {
		Start = start;
	}
	
	public void setStop (Dimension stop) {
		Stop = stop;
	}
	
	public void update (Vector<DrawCommand> commands) {
		DrawPanel Content = (DrawPanel) this.Content;
		
		for (DrawCommand Command : commands)
			Content.updateOriginal (Command);
		Content.repaint ();
	}
	
	public void repaint () {
		Content.repaint ();
	}
	
	/**
	 * This method initializes jFrame
	 * 
	 * @return javax.swing.JFrame
	 */
	public JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setSize(new Dimension(530, 378));
			jFrame.setJMenuBar(getJJMenuBar());
			jFrame.setContentPane(getJContentPane());
			jFrame.setResizable(false);
			jFrame.setVisible(true);
			jFrame.setTitle("NetDrawer");
		}
		return jFrame;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getContent(), null);
			jContentPane.add(getToolbar(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("Menu");
			fileMenu.add(getConnectMenuItem());
			//fileMenu.add(getClearItem());
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getConnectMenuItem() {
		if (connectMenuItem == null) {
			connectMenuItem = new JMenuItem();
			connectMenuItem.setText("Connect");
			connectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					Event.CTRL_MASK, true));
			connectMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("connectMenu");
					Controller.showConnectionWindow ();
				}
			});
		}
		return connectMenuItem;
	}

	/**
	 * This method initializes Content	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getContent() {
		if (Content == null) {
			Content = (JPanel) new DrawPanel();
			Content.setLayout(new FlowLayout());
			Content.addMouseListener(new java.awt.event.MouseAdapter() {   
				public void mousePressed(java.awt.event.MouseEvent event) {
					System.out.println("mousePressed()");
					Controller.mousePressed (event);
				}
				public void mouseReleased(java.awt.event.MouseEvent event) {    
					System.out.println("mouseReleased()");
					Controller.mouseReleased (event);
				}
			});
			Content.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
				public void mouseDragged(java.awt.event.MouseEvent event) {
					System.out.println("mouseDragged()");
					Controller.mouseDragged (event);
				}
			});
		}
		return Content;
	}

	/**
	 * This method initializes Toolbar	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getToolbar() {
		if (Toolbar == null) {
			Toolbar = new JPanel();
			Toolbar.setLayout(new GridBagLayout());
			Toolbar.add(getPoint(), new GridBagConstraints());
			Toolbar.add(getLine(), new GridBagConstraints());
			Toolbar.add(getCircle(), new GridBagConstraints());
			Toolbar.add(getRectangle(), new GridBagConstraints());
		}
		return Toolbar;
	}
	
	private void refreshContent () {
		Clear = true;
		Content.repaint ();
	}

	private void setSize (int width, int height) {
		if (width <= 0 || height <= 0)
			return;
		
		Width = width;
		Height = height;
		
		Insets	Insets = jFrame.getInsets ();
		
		int	ContentWidth = Width,
			ContentHeight = Height,
			ContentX = 0,
			ContentY = 0,
			ToolbarWidth = Width,
			ToolbarHeight = 30,
			ToolbarX = ContentX,
			ToolbarY = ContentY + ContentHeight;
		
		jFrame.setSize (Width + Insets.left + Insets.right, Insets.top + (jJMenuBar.getSize ().height) + ContentHeight + ToolbarHeight + Insets.bottom);
		Content.setBounds (ContentX, ContentY, ContentWidth, ContentHeight);
		Toolbar.setBounds (ToolbarX, ToolbarY, ToolbarWidth, ToolbarHeight);
		
		refreshContent ();
	}

	/**
	 * This method initializes Point	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPoint() {
		if (Point == null) {
			Point = new JButton();
			Point.setText("Point");
			Point.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setType(CommandType.Point);
					System.out.println("Type := Point");
				}
			});
		}
		return Point;
	}

	/**
	 * This method initializes Line	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLine() {
		if (Line == null) {
			Line = new JButton();
			Line.setText("Line");
			Line.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setType(CommandType.Line);
					System.out.println("Type := Line");
				}
			});
		}
		return Line;
	}

	/**
	 * This method initializes Circle	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCircle() {
		if (Circle == null) {
			Circle = new JButton();
			Circle.setText("Circle");
			Circle.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setType(CommandType.Circle);
					System.out.println("Type := Circle");
				}
			});
		}
		return Circle;
	}

	/**
	 * This method initializes Rectangle	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRectangle() {
		if (Rectangle == null) {
			Rectangle = new JButton();
			Rectangle.setText("Rectangle");
			Rectangle.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setType(CommandType.Rectangle);
					System.out.println("Type := Rectangle");
				}
			});
		}
		return Rectangle;
	}
	
	@SuppressWarnings("serial")
	private class DrawPanel extends JPanel {
		public void paint (Graphics gDC) {
			if (Clear) {
				OriginalImage = createImage (Width, Height);
				Graphics Drawer = OriginalImage.getGraphics (); 
				Drawer.setColor (Color.WHITE);
				Drawer.fillRect (0, 0, Width, Height);
				gDC.drawImage (OriginalImage, 0, 0, this);
				Clear = false;
			}
			
			if (Start != null && Stop != null) {
				updateOriginal (new DrawCommand (getType(), Start, Stop));
				gDC.drawImage (OriginalImage, 0, 0, this);
				
				Start = null;
				Middle = null;
				Stop = null;
			} else if (Start != null && Middle != null) {
				Image TempImage = createImage (Width, Height);
				Graphics Drawer = TempImage.getGraphics ();
				Drawer.drawImage (OriginalImage, 0, 0, null);
				GraphicDrawer.draw (new DrawCommand (getType(), Start, Middle), Drawer);
				gDC.drawImage (TempImage, 0, 0, this);
			} else {
				gDC.drawImage (OriginalImage, 0, 0, this);
			}
		}
		
		public void updateOriginal (DrawCommand command) {
			GraphicDrawer.draw (command, OriginalImage.getGraphics ());
		}
	}

	/**
	 * This method initializes clearItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getClearItem() {
		if (clearItem == null) {
			clearItem = new JMenuItem();
			clearItem.setText("Clear");
			clearItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("Clear");
					Controller.clear ();
				}
			});
		}
		return clearItem;
	}

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new DrawerWindow ();
			}
		});
	}

	public void setType(CommandType type) {
		Type = type;
	}

	public CommandType getType() {
		return Type;
	}
}
