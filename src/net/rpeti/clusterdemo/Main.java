package net.rpeti.clusterdemo;

import java.awt.EventQueue;

import net.rpeti.clusterdemo.gui.MainWindow;

public class Main {
	
	private static final Controller controller = new Controller();
	
	public static Controller getController(){
		return controller;
	}

	//TODO GUI megfagy, ha az algoritmus teker, mi�rt nem fut k�l�n sz�lon?
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
