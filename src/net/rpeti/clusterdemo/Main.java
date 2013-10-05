package net.rpeti.clusterdemo;

import java.awt.EventQueue;

import net.rpeti.clusterdemo.gui.MainWindow;

public class Main {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.show();
				} catch (Exception e) {
					//TODO
					e.printStackTrace();
				}
			}
		});
	}

}
