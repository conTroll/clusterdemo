package net.rpeti.clusterdemo.gui;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.*;
import javax.swing.border.EtchedBorder;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FileDialog;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ToolTipManager;

import java.awt.Dimension;

import javax.swing.JSplitPane;
import javax.swing.JLabel;

import net.rpeti.clusterdemo.Controller;
import net.rpeti.clusterdemo.Main;
import net.rpeti.clusterdemo.gui.dialog.ImportCSV;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JToolBar;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.Scanner;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.JButton;

public class MainWindow {

	private static final String NEWLINE = System.getProperty("line.separator");
	
	private static final String TITLE = "ClusterDemo";
	private static final String VERSION = "v0.8 Beta";
	private static final String ABOUT_MESSAGE = "<html><h2>ClusterDemo</h2><b>Version: </b>" + VERSION + "<br><br>" + "Rónai Péter<br>(ROPSAAI.ELTE | KD1OUR)<br><br><b>Icons:</b><br>";
	private static final String ABOUT_WINDOW_TITLE = "About ClusterDemo";
	
	private static final String UNHANDLED_EXCEPTION_TITLE = "Unhandled Exception";
	private static final String UNHANDLED_EXCEPTION_TEXT = "Unhandled exception has occurred in the application." + NEWLINE +
			"This behavior is unintended." + NEWLINE + "Technical information can be found in the stack trace below.";
	
	private static final String ERROR_TITLE = "Error";
	private static final String ERROR_OPEN_BROWSER = "Error happened while trying to open a link in the default browser.";
	private static final String ABOUT_MENU_TEXT = "About";
	private static final String HELP_MENU_TEXT = "Help";
	private static final String FILE_MENU_TEXT = "File";
	private static final String CREATE_REPORT_MENU_TEXT = "Create Report...";
	private static final String IMPORT_CSV_MENU_TEXT = "Import (CSV)...";
	private static final String SAVE_MENU_TEXT = "Save";
	private static final String SAVE_AS_MENU_TEXT = "Save As...";
	private static final String EXIT_MENU_TEXT = "Exit";
	private static final String CONFIRM_EXIT_TITLE = "Confirm Exit";
	private static final String EXIT_QUESTION = "Exit Application?";
	private static final String WELCOME_TEXT = "Welcome to Clustering Demo application.";
	private static final String TOOLTIP_MOVING = "Moving Mode";
	private static final String TOOLTIP_PICKING = "Picking Mode";
	private static final String PICKING_TEXT = "<html><b>Selected Mode:</b> PICKING</html>";
	private static final String MOVING_TEXT = "<html><b>Selected Mode:</b> MOVING</html>";
	
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;

	private JFrame frmClusterDemo;
	private JPanel graphDrawingPanel;
	private BasicVisualizationServer<?,?> canvas;
	private SidePanel sidePanel;
	private JLabel statusBarLabel;
	
	private Controller controller;
	private JToggleButton pickingModeButton;
	private JToggleButton moveModeButton;
	private JToolBar graphMouseToolbar;
	
	private JLabel modeLabel;
	private JMenuItem mntmExport;
	private JButton rearrangeButton;
	private JButton zoomOutButton;
	private JButton zoomInButton;
	private JMenuItem mntmSaveAs;
	private JMenuItem mntmSave;

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Enables saving the HTML report.
	 */
	public void enableSave(){
		mntmExport.setEnabled(true);
		mntmSave.setEnabled(true);
		mntmSaveAs.setEnabled(true);
	}
	
	/**
	 * @return the frame, which for eg. can be passed to a child window (like a dialog).
	 */
	public JFrame getFrame(){
		return frmClusterDemo;
	}

	/**
	 * @return the mouse mode selected by the user on the toolbar
	 */
	public ModalGraphMouse.Mode getMouseMode(){
		if(moveModeButton.isSelected()) 
			return ModalGraphMouse.Mode.TRANSFORMING;
		if(pickingModeButton.isSelected())
			return ModalGraphMouse.Mode.PICKING;
		else
			return ModalGraphMouse.Mode.TRANSFORMING;
	}
	
	public SidePanel getSidePanel(){
		return this.sidePanel;
	}
	
	/**
	 * @return the actual available space for drawing the canvas
	 */
	public Dimension getSizeForCanvas(){
		return new Dimension(graphDrawingPanel.getWidth(),
				graphDrawingPanel.getHeight() - graphMouseToolbar.getHeight());
	}
	
	private void importFromCSV(){
		ImportCSV dialog = new ImportCSV(frmClusterDemo);
		if(dialog.getIsOk()){
			controller.importCSV(dialog.getIsAttributesInFirstLine(),
					dialog.getSeparator(), dialog.getSelectedFile());
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		controller = Main.getController();
		controller.setMainWindow(this);
		
		//setting the Swing Look and Feel for the application
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
		    		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		    	} catch (Exception e1){
		    		showUnhandledException(e1);
		    		e1.printStackTrace();
		    	}
		    }
		    else {
		    	showUnhandledException(e);
		    	e.printStackTrace();
		    }
		}
		
		//this way the tooltips won't disappear so quickly
		ToolTipManager.sharedInstance().setDismissDelay(60000);
		
		//Set title, position, size, etc.
		frmClusterDemo = new JFrame();
		frmClusterDemo.setFocusable(true);
		
		frmClusterDemo.setTitle(TITLE);
		frmClusterDemo.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		frmClusterDemo.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		frmClusterDemo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		frmClusterDemo.setLocationRelativeTo(null);
		frmClusterDemo.setIconImage(new ImageIcon(MainWindow.class.getResource("/icons/diagram.png")).getImage());
		
		//add the components
		graphMouseToolbar = new JToolBar();
		graphMouseToolbar.setRollover(true);
		graphMouseToolbar.setFloatable(false);
		
		JMenuBar menuBar = new JMenuBar();
		frmClusterDemo.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu(FILE_MENU_TEXT);
		menuBar.add(mnFile);
		
		mntmExport = new JMenuItem(CREATE_REPORT_MENU_TEXT);
		mntmExport.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/report.png")));
		mntmExport.setEnabled(false);
		mntmExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.getController().exportToHtml();
			}
		});
		
		JMenuItem mntmImportcsv = new JMenuItem(IMPORT_CSV_MENU_TEXT);
		mntmImportcsv.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/import_csv.png")));
		mntmImportcsv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importFromCSV();
			}
		});
		mnFile.add(mntmImportcsv);
		
		JSeparator separator_2 = new JSeparator();
		mnFile.add(separator_2);
		
		mntmSave = new JMenuItem(SAVE_MENU_TEXT);
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.getController().saveCSV();
			}
		});
		mntmSave.setEnabled(false);
		mntmSave.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/save.png")));
		mnFile.add(mntmSave);
		
		mntmSaveAs = new JMenuItem(SAVE_AS_MENU_TEXT);
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.getController().saveCSVAs();
			}
		});
		mntmSaveAs.setEnabled(false);
		mntmSaveAs.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/save.png")));
		mnFile.add(mntmSaveAs);
		mnFile.add(mntmExport);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmExit = new JMenuItem(EXIT_MENU_TEXT);
		mntmExit.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/exit.png")));
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int confirm = JOptionPane.showOptionDialog(MainWindow.this.getFrame(), EXIT_QUESTION, CONFIRM_EXIT_TITLE,
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if(confirm == JOptionPane.YES_OPTION){
					System.exit(0);
				}
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnHelp = new JMenu(HELP_MENU_TEXT);
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem(ABOUT_MENU_TEXT);
		mntmAbout.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/about.png")));
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton aha = new JButton("Aha-Soft");
				JButton glyph = new JButton("GLYPHICONS.com");
				aha.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						MainWindow.this.openBrowser("http://www.aha-soft.com/");
					}
				});
				glyph.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						MainWindow.this.openBrowser("http://www.glyphicons.com/");
					}
				});
				JOptionPane.showMessageDialog(frmClusterDemo, new Object[]{ABOUT_MESSAGE, aha, glyph}, ABOUT_WINDOW_TITLE,
						JOptionPane.INFORMATION_MESSAGE, new ImageIcon(this.getClass().getResource("/icons/about.png")));
			}
		});
		
		JMenuItem mntmHelp = new JMenuItem(HELP_MENU_TEXT);
		mntmHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InputStream is = MainWindow.this.getClass().getResourceAsStream("/text/help.txt");
				Scanner scanner = new Scanner(is);
				scanner.useDelimiter("\\Z");
				String helpText = scanner.next();
				helpText = helpText.replaceAll("(\\r|\\n)", "");
				scanner.close();
				JOptionPane.showMessageDialog(MainWindow.this.getFrame(), helpText,
						"Quick Help", JOptionPane.INFORMATION_MESSAGE,
						new ImageIcon(MainWindow.this.getClass().getResource("/icons/howto.png")));
			}
		});
		mntmHelp.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/howto.png")));
		mnHelp.add(mntmHelp);
		mnHelp.add(mntmAbout);
		frmClusterDemo.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(1);
		frmClusterDemo.getContentPane().add(splitPane);
		
		graphDrawingPanel = new JPanel();
		splitPane.setLeftComponent(graphDrawingPanel);
		graphDrawingPanel.setLayout(new BorderLayout(0, 0));
		graphDrawingPanel.add(graphMouseToolbar, BorderLayout.SOUTH);
		
		sidePanel = new SidePanel();
		splitPane.setRightComponent(sidePanel);
		
		JPanel statusBar = new JPanel();
		statusBar.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		statusBar.setPreferredSize(new Dimension(frmClusterDemo.getWidth(), 20));
		statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
		frmClusterDemo.getContentPane().add(statusBar, BorderLayout.SOUTH);
		
		Component horizontalStrut_4 = Box.createHorizontalStrut(4);
		statusBar.add(horizontalStrut_4);
		
		statusBarLabel = new JLabel(WELCOME_TEXT);
		statusBarLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
		statusBar.add(statusBarLabel);
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(10);
		graphMouseToolbar.add(horizontalStrut_3);
		
		rearrangeButton = new JButton("");
		rearrangeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.getController().rearrangeCanvas();
			}
		});
		rearrangeButton.setEnabled(false);
		rearrangeButton.setToolTipText("Rearrange");
		rearrangeButton.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/toolbar_refresh.png")));
		graphMouseToolbar.add(rearrangeButton);
		
		zoomOutButton = new JButton("");
		zoomOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.getController().zoomOut();
			}
		});
		zoomOutButton.setEnabled(false);
		zoomOutButton.setToolTipText("Zoom Out");
		zoomOutButton.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/toolbar_zoomout.png")));
		graphMouseToolbar.add(zoomOutButton);
		
		zoomInButton = new JButton("");
		zoomInButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.getController().zoomIn();
			}
		});
		zoomInButton.setEnabled(false);
		zoomInButton.setToolTipText("Zoom In");
		zoomInButton.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/toolbar_zoomin.png")));
		graphMouseToolbar.add(zoomInButton);
		
		Component horizontalStrut_5 = Box.createHorizontalStrut(10);
		graphMouseToolbar.add(horizontalStrut_5);
		
		moveModeButton = new JToggleButton("");
		moveModeButton.setEnabled(false);
		moveModeButton.setSelected(true);
		moveModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveModeButton.setSelected(true);
				pickingModeButton.setSelected(false);
				modeLabel.setText(MOVING_TEXT);
				controller.changeMouseMode();
			}
		});
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setOrientation(SwingConstants.VERTICAL);
		graphMouseToolbar.add(separator_3);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(10);
		graphMouseToolbar.add(horizontalStrut_1);
		moveModeButton.setToolTipText(TOOLTIP_MOVING);
		moveModeButton.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/toolbar_move.png")));
		graphMouseToolbar.add(moveModeButton);
		
		//create mouse mode toolbar
		pickingModeButton = new JToggleButton("");
		pickingModeButton.setEnabled(false);
		pickingModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveModeButton.setSelected(false);
				pickingModeButton.setSelected(true);
				modeLabel.setText(PICKING_TEXT);
				controller.changeMouseMode();
			}
		});
		pickingModeButton.setToolTipText(TOOLTIP_PICKING);
		pickingModeButton.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/toolbar_select.png")));
		graphMouseToolbar.add(pickingModeButton);
		
		Component horizontalStrut = Box.createHorizontalStrut(10);
		graphMouseToolbar.add(horizontalStrut);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		graphMouseToolbar.add(separator_1);
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(10);
		graphMouseToolbar.add(horizontalStrut_2);
		
		modeLabel = new JLabel(MOVING_TEXT);
		graphMouseToolbar.add(modeLabel);
		
		//tell the canvas to resize when the main window gets resized
		graphDrawingPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e){
				controller.updateCanvasSize();
			}
		});
		
		frmClusterDemo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == '9'){
					JOptionPane.showMessageDialog(frmClusterDemo, "", "=)", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(MainWindow.class.getResource("/icons/smile.png")));
				}
			}
		});
	}
	
	/**
	 * Cross-platform browser opener.
	 * @param URL
	 * 		the full URL to the webpage we will open (eg. "http://www.google.com/")
	 */
	public void openBrowser(String URL){
		if(Desktop.isDesktopSupported()){
			//this should work on Windows and Mac
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(URL));
            } catch (Exception e) {
            	JOptionPane.showMessageDialog(this.getFrame(), ERROR_OPEN_BROWSER,  ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }else{
        	//this should work on Linux
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + URL);
            } catch (Exception e) {
            	JOptionPane.showMessageDialog(this.getFrame(), ERROR_OPEN_BROWSER,  ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }
	}
	
	/**
	 * Opens a file save dialog.
	 * @param title
	 * 		Window title for the file save dialog.
	 * @param initialName
	 * 		Initial file name supplied in the dialog.
	 * @return
	 * 		The <code>java.io.File</code> object containing the selected path,
	 * 		or <code>null</code> if cancel has been selected by the user.
	 */
	public File selectSavePath(String title, String initialName){
		FileDialog chooser = new FileDialog(this.getFrame(), title, FileDialog.SAVE);
		this.getFrame().setEnabled(false);
		chooser.setFile(initialName);
		chooser.setVisible(true);
		this.getFrame().setEnabled(true);
		this.getFrame().toFront();
		if(chooser.getDirectory() == null || chooser.getFile() == null)
			return null;
		return new File(chooser.getDirectory() + chooser.getFile());
	}
	
	/**
	 * Method for injecting the drawing canvas for JUNG.
	 */
	public void setGraphDrawingComponent(BasicVisualizationServer<?,?> visualizationServer){
		//remove previous drawing panel, if any
		if (canvas != null) graphDrawingPanel.remove(canvas);
		
		//show loading message (the panel can take some time to show (especially for the first time)
		JLabel loadingLabel = new JLabel("Loading visualization...");
		loadingLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
		loadingLabel.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/barchart.png")));
		loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		graphDrawingPanel.add(loadingLabel, BorderLayout.CENTER);
		
		//show new visualization panel, when finished, remove loading label
		canvas = visualizationServer;
		graphDrawingPanel.add(visualizationServer, BorderLayout.NORTH);
		graphDrawingPanel.remove(loadingLabel);
		moveModeButton.setEnabled(true);
		pickingModeButton.setEnabled(true);
		zoomInButton.setEnabled(true);
		zoomOutButton.setEnabled(true);
		rearrangeButton.setEnabled(true);
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
	
	/**
	 * Method for displaying an error message.
	 */
	public void showErrorMessage(String title, Object message){
		JOptionPane.showMessageDialog(frmClusterDemo, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Method for displaying information message.
	 */
	public void showMessage(String title, Object message){
		JOptionPane.showMessageDialog(frmClusterDemo, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Displays the unhandled exception to the user in a copyable way.
	 * @param e 
	 * 		the exception
	 */
	public void showUnhandledException(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		JTextArea stacktrace = new JTextArea(sw.toString());
		stacktrace.setEditable(false);
		showErrorMessage(UNHANDLED_EXCEPTION_TITLE, 
				new Object[]{UNHANDLED_EXCEPTION_TEXT + NEWLINE, stacktrace});
	}
}
