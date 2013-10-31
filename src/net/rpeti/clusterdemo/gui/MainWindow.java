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
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

//TODO nimbus specifikus exception-ökkel lehet valamit kezdeni?

public class MainWindow {
	
	private static final String ABOUT_TITLE = "ClusterDemo";
	private static final String ABOUT_MESSAGE = "v0.2 Alpha\n\nRónai Péter\n(ROPSAAI.ELTE | KD1OUR)";
	
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
	private JToggleButton editingModeButton;
	private JToolBar graphMouseToolbar;
	
	private JLabel modeLabel;

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
		
		ToolTipManager.sharedInstance().setDismissDelay(60000);
		
		//Set title, position, size, etc.
		frmClusterDemo = new JFrame();
		frmClusterDemo.setTitle("ClusterDemo");
		frmClusterDemo.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		frmClusterDemo.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		frmClusterDemo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		frmClusterDemo.setLocationRelativeTo(null);
		
		graphMouseToolbar = new JToolBar();
		graphMouseToolbar.setFloatable(false);
		
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
				importFromCSV();
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
		
		pickingModeButton = new JToggleButton("");
		pickingModeButton.setEnabled(false);
		pickingModeButton.setSelectedIcon(new ImageIcon(MainWindow.class.getResource("/icons/picking_on.png")));
		pickingModeButton.setContentAreaFilled(false);
		pickingModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveModeButton.setSelected(false);
				editingModeButton.setSelected(false);
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
				editingModeButton.setSelected(false);
				pickingModeButton.setSelected(false);
				modeLabel.setText("<html><b>Selected Mode:</b> MOVING</html>");
				controller.changeMouseMode();
			}
		});
		moveModeButton.setToolTipText("Move");
		moveModeButton.setContentAreaFilled(false);
		moveModeButton.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/move_off.png")));
		graphMouseToolbar.add(moveModeButton);
		
		editingModeButton = new JToggleButton("");
		editingModeButton.setEnabled(false);
		editingModeButton.setContentAreaFilled(false);
		editingModeButton.setSelectedIcon(new ImageIcon(MainWindow.class.getResource("/icons/editing_on.png")));
		editingModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveModeButton.setSelected(false);
				editingModeButton.setSelected(true);
				pickingModeButton.setSelected(false);
				modeLabel.setText("<html><b>Selected Mode:</b> EDITING</html>");
				controller.changeMouseMode();
			}
		});
		editingModeButton.setToolTipText("Editing");
		editingModeButton.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/editing_off.png")));
		graphMouseToolbar.add(editingModeButton);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		graphMouseToolbar.add(separator_1);
		
		modeLabel = new JLabel("<html><b>Selected Mode:</b> MOVING</html>");
		graphMouseToolbar.add(modeLabel);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		frmClusterDemo.getContentPane().add(toolBar, BorderLayout.NORTH);
		
		JButton btnCsvImport = new JButton("");
		btnCsvImport.setToolTipText("CSV Import...");
		btnCsvImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importFromCSV();
			}
		});
		btnCsvImport.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/import_csv.png")));
		toolBar.add(btnCsvImport);
		
		//tell the canvas to resize itself when main window gets resized by the user
		frmClusterDemo.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e){
				controller.updateCanvasSize();
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
		if (canvas != null) graphDrawingPanel.remove(canvas);
		canvas = visualizationServer;
		graphDrawingPanel.add(visualizationServer, BorderLayout.NORTH);
		moveModeButton.setEnabled(true);
		pickingModeButton.setEnabled(true);
		editingModeButton.setEnabled(true);
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
	
	public ModalGraphMouse.Mode getMouseMode(){
		if(moveModeButton.isSelected()) 
			return ModalGraphMouse.Mode.TRANSFORMING;
		if(editingModeButton.isSelected())
			return ModalGraphMouse.Mode.EDITING;
		if(pickingModeButton.isSelected())
			return ModalGraphMouse.Mode.PICKING;
		else
			return ModalGraphMouse.Mode.TRANSFORMING;
	}
}
