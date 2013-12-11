package net.rpeti.clusterdemo;

import java.awt.EventQueue;

import net.rpeti.clusterdemo.gui.MainWindow;

public class Main {
	private static final Controller controller = Controller.INSTANCE;
	
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
