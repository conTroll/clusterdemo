package net.rpeti.clusterdemo;

import java.awt.EventQueue;

import net.rpeti.clusterdemo.gui.MainWindow;

//TODO elkapni az exception-öket EventQueue-ból is és megjeleníteni
// http://ruben42.wordpress.com/2009/03/30/catching-all-runtime-exceptions-in-swing/

public class Main {
	
	private static final Controller controller = new Controller();
	
	public static Controller getController(){
		return controller;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
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
