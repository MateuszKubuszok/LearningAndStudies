package krowa.gui;

import static java.awt.EventQueue.invokeLater;

public class Main {
	public static void main(String[] args) {
		invokeLater(new Runnable() {
			public void run() {
				try {
					new MainWindow1D();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		invokeLater(new Runnable() {
			public void run() {
				try {
					new MainWindow2D();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
