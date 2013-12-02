package net.rpeti.clusterdemo.gui;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.*;
import javax.swing.border.EtchedBorder;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.BorderLayout;

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
import java.io.PrintWriter;
import java.io.StringWriter;

//TODO write help

public class MainWindow {

	private static final String NEWLINE = System.getProperty("line.separator");
	
	private static final String TITLE = "ClusterDemo";
	private static final String ABOUT_TITLE = TITLE;
	private static final String ABOUT_MESSAGE = "v0.6 Alpha" + NEWLINE + NEWLINE + "Rónai Péter" + NEWLINE + "(ROPSAAI.ELTE | KD1OUR)";
	private static final String ABOUT_WINDOW_TITLE = "About ClusterDemo";
	
	private static final String UNHANDLED_EXCEPTION_TITLE = "Unhandled Exception";
	private static final String UNHANDLED_EXCEPTION_TEXT = "Unhandled exception has occurred in the application." + NEWLINE +
			"This behavior is unintended." + NEWLINE + "Technical information can be found in the stack trace below.";
	
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
		
		ToolTipManager.sharedInstance().setDismissDelay(60000);
		
		//Set title, position, size, etc.
		frmClusterDemo = new JFrame();
		frmClusterDemo.setFocusable(true);
		
		frmClusterDemo.setTitle(TITLE);
		frmClusterDemo.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		frmClusterDemo.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		frmClusterDemo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		frmClusterDemo.setLocationRelativeTo(null);
		
		//add the components
		graphMouseToolbar = new JToolBar();
		graphMouseToolbar.setFloatable(false);
		
		JMenuBar menuBar = new JMenuBar();
		frmClusterDemo.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmExport = new JMenuItem("Create Report...");
		mntmExport.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/report.png")));
		mntmExport.setEnabled(false);
		mntmExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.getController().exportToHtml();
			}
		});
		
		JMenuItem mntmImportcsv = new JMenuItem("Import (CSV)...");
		mntmImportcsv.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/import_csv.png")));
		mntmImportcsv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importFromCSV();
			}
		});
		mnFile.add(mntmImportcsv);
		
		JSeparator separator_2 = new JSeparator();
		mnFile.add(separator_2);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setEnabled(false);
		mntmSave.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/save.png")));
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save As...");
		mntmSaveAs.setEnabled(false);
		mntmSaveAs.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/save.png")));
		mnFile.add(mntmSaveAs);
		mnFile.add(mntmExport);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/exit.png")));
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int confirm = JOptionPane.showOptionDialog(MainWindow.this.getFrame(), "Exit Application?", "Confirm Exit",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if(confirm == JOptionPane.YES_OPTION){
					System.exit(0);
				}
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/about.png")));
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JLabel aboutTitle = new JLabel(ABOUT_TITLE);
				aboutTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
				JOptionPane.showMessageDialog(frmClusterDemo, 
						new Object[]{aboutTitle, ABOUT_MESSAGE}, ABOUT_WINDOW_TITLE,
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		JMenuItem mntmHelp = new JMenuItem("Help");
		mntmHelp.setEnabled(false);
		mntmHelp.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/howto.png")));
		mnHelp.add(mntmHelp);
		mnHelp.add(mntmAbout);
		frmClusterDemo.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
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
		
		statusBarLabel = new JLabel("Welcome to Clustering Demo application.");
		statusBarLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
		statusBar.add(statusBarLabel);
		
		//create mouse mode toolbar
		pickingModeButton = new JToggleButton("");
		pickingModeButton.setEnabled(false);
		pickingModeButton.setSelectedIcon(new ImageIcon(MainWindow.class.getResource("/icons/picking_on.png")));
		pickingModeButton.setContentAreaFilled(false);
		pickingModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveModeButton.setSelected(false);
				pickingModeButton.setSelected(true);
				modeLabel.setText("<html><b>Selected Mode:</b> PICKING</html>");
				controller.changeMouseMode();
			}
		});
		pickingModeButton.setToolTipText("Picking");
		pickingModeButton.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/picking_off.png")));
		graphMouseToolbar.add(pickingModeButton);
		
		moveModeButton = new JToggleButton("");
		moveModeButton.setEnabled(false);
		moveModeButton.setSelected(true);
		moveModeButton.setSelectedIcon(new ImageIcon(MainWindow.class.getResource("/icons/move_on.png")));
		moveModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveModeButton.setSelected(true);
				pickingModeButton.setSelected(false);
				modeLabel.setText("<html><b>Selected Mode:</b> MOVING</html>");
				controller.changeMouseMode();
			}
		});
		moveModeButton.setToolTipText("Move");
		moveModeButton.setContentAreaFilled(false);
		moveModeButton.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/move_off.png")));
		graphMouseToolbar.add(moveModeButton);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		graphMouseToolbar.add(separator_1);
		
		modeLabel = new JLabel("<html><b>Selected Mode:</b> MOVING</html>");
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
	
	private void importFromCSV(){
		ImportCSV dialog = new ImportCSV(frmClusterDemo);
		if(dialog.getIsOk()){
			controller.importCSV(dialog.getIsAttributesInFirstLine(),
					dialog.getSeparator(), dialog.getSelectedFile());
		}
	}

	/**
	 * Method for displaying information message.
	 */
	public void showMessage(String title, Object message){
		JOptionPane.showMessageDialog(frmClusterDemo, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Method for displaying an error message.
	 */
	public void showErrorMessage(String title, Object message){
		JOptionPane.showMessageDialog(frmClusterDemo, message, title, JOptionPane.ERROR_MESSAGE);
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
	
	/**
	 * Method for injecting the drawing canvas for JUNG.
	 */
	public void setGraphDrawingComponent(BasicVisualizationServer<?,?> visualizationServer){
		if (canvas != null) graphDrawingPanel.remove(canvas);
		canvas = visualizationServer;
		graphDrawingPanel.add(visualizationServer, BorderLayout.NORTH);
		moveModeButton.setEnabled(true);
		pickingModeButton.setEnabled(true);
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
	 * Enables saving the HTML report.
	 */
	public void enableSave(){
		mntmExport.setEnabled(true);
	}
	
	/**
	 * @return the frame, which for eg. can be passed to a child window (like a dialog).
	 */
	public JFrame getFrame(){
		return frmClusterDemo;
	}
	
	/**
	 * @return the actual available space for drawing the canvas
	 */
	public Dimension getSizeForCanvas(){
		return new Dimension(graphDrawingPanel.getWidth(),
				graphDrawingPanel.getHeight() - graphMouseToolbar.getHeight());
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
}
