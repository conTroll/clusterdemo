package net.rpeti.clusterdemo.gui;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.*;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.BorderLayout;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JSplitPane;
import javax.swing.JLabel;

import net.rpeti.clusterdemo.Controller;
import net.rpeti.clusterdemo.Main;
import net.rpeti.clusterdemo.gui.dialog.ImportCSV;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class MainWindow {
	
	private static final String ABOUT_TITLE = "ClusterDemo";
	private static final String ABOUT_MESSAGE = "v0.01 Alpha\n\nRónai Péter\n(ROPSAAI.ELTE | KD1OUR)";
	
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;

	private JFrame frmClusterDemo;
	private JPanel graphDrawingPanel;
	private JPanel sidePanel;
	private JLabel statusBarLabel;
	
	private Controller controller;

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		controller = Main.getController();
		controller.setMainWindow(this);
		
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    if(e instanceof UnsupportedLookAndFeelException){
		    	try{
		    		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		    	} catch (Exception e1){
		    		//TODO
		    		e1.printStackTrace();
		    	}
		    }
		    else {
		    	//TODO
		    	e.printStackTrace();
		    }
		}
		
		//Set title, position, size, etc.
		frmClusterDemo = new JFrame();
		frmClusterDemo.setTitle("ClusterDemo");
		Dimension screenResolution = Toolkit.getDefaultToolkit().getScreenSize();
		frmClusterDemo.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		frmClusterDemo.setBounds((screenResolution.width / 2) - (WINDOW_WIDTH / 2),
				(screenResolution.height / 2) - (WINDOW_HEIGHT / 2), WINDOW_WIDTH, WINDOW_HEIGHT);
		frmClusterDemo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		frmClusterDemo.setLocationRelativeTo(null);
		
		JMenuBar menuBar = new JMenuBar();
		frmClusterDemo.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenu mnNewMenu = new JMenu("Import");
		mnFile.add(mnNewMenu);
		
		JMenuItem mntmFromCsvFile = new JMenuItem("CSV file...");
		mntmFromCsvFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					
						ImportCSV dialog = new ImportCSV(frmClusterDemo);
						if(dialog.getIsOk()){
							controller.importCSV(dialog.getIsAttributesInFirstLine(),
									dialog.getSeparator(), dialog.getSelectedFile());
						}
			}

		});
		mnNewMenu.add(mntmFromCsvFile);
		
		JMenuItem mntmExport = new JMenuItem("Export...");
		mnFile.add(mntmExport);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JLabel aboutTitle = new JLabel(ABOUT_TITLE);
				aboutTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
				JOptionPane.showMessageDialog(frmClusterDemo, 
						new Object[]{aboutTitle, ABOUT_MESSAGE}, "About ClusterDemo",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mnHelp.add(mntmAbout);
		frmClusterDemo.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(1);
		frmClusterDemo.getContentPane().add(splitPane);
		
		graphDrawingPanel = new JPanel();
		graphDrawingPanel.setBackground(Color.WHITE);
		splitPane.setLeftComponent(graphDrawingPanel);
		
		sidePanel = new SidePanel();
		splitPane.setRightComponent(sidePanel);
		
		JPanel statusBar = new JPanel();
		statusBar.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		statusBar.setPreferredSize(new Dimension(frmClusterDemo.getWidth(), 20));
		statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
		frmClusterDemo.getContentPane().add(statusBar, BorderLayout.SOUTH);
		
		statusBarLabel = new JLabel("Welcome to Clustering Demo application.");
		statusBarLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
		statusBar.add(statusBarLabel);
	}

	public void showMessage(String title, String message){
		JOptionPane.showMessageDialog(frmClusterDemo, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Method for displaying an error message.
	 */
	public void showErrorMessage(String title, String message){
		JOptionPane.showMessageDialog(frmClusterDemo, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Method for injecting the drawing canvas for JUNG.
	 */
	public void setGraphDrawingComponent(BasicVisualizationServer<?,?> visualizationServer){
		graphDrawingPanel.removeAll();
		graphDrawingPanel.add(visualizationServer);
	}
	
	/**
	 * Change the text in the status bar located at the bottom of the main window.
	 */
	public void setStatusBarText(String text){
		statusBarLabel.setText(text);
	}
	
	/**
	 * Show the window.
	 */
	public void show() {
		frmClusterDemo.setVisible(true);
	}
}
