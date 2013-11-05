package net.rpeti.clusterdemo;

import java.awt.EventQueue;

import net.rpeti.clusterdemo.gui.MainWindow;

//TODO validáció, hibaüzenetek

public class Main {
	
	private static final Controller controller = new Controller();
	
	public static Controller getController(){
		return controller;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainWindow window = new MainWindow();
				try {
					window.show();
				} catch (Exception e) {
					window.showUnhandledException(e);
					e.printStackTrace();
				}
			}
		});
	}

}
