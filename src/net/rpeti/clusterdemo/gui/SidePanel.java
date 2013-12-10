package net.rpeti.clusterdemo.gui;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;

import javax.swing.JComboBox;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Font;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.DefaultComboBoxModel;

import net.rpeti.clusterdemo.Controller;
import net.rpeti.clusterdemo.Main;
import net.rpeti.clusterdemo.algorithms.Algorithms;

import javax.swing.JCheckBox;
import javax.swing.ImageIcon;

public class SidePanel extends JPanel {
	
	// TODO ability to select attributes to cluster on

	public static final int STATUS_TYPE_NONE = -1;
	public static final int STATUS_TYPE_READY = 0;
	public static final int STATUS_TYPE_WARNING = 1;
	public static final int STATUS_TYPE_ERROR = 2;
	
	private static final long serialVersionUID = 7654943576215466209L;
	
	private static final String OLARY = "Olary";
	private static final String KMEANS = "K-Means";
	
	private static final String TITLE = "Settings";
	private static final String ALGORITHM_TEXT = "Algorithm";
	private static final String MAX_ITERATIONS_DESC = "The algorithm will stop after this number of iterations even if the convergence criteria haven't met.";
	private static final String MAX_ITERATIONS_TEXT = "Max iterations";
	private static final String CLUSTERS_TEXT = "Clusters";
	private static final String SEED_TEXT = "Seed";
	private static final String RUN_CLUSTERING_TEXT = "Run Clustering";
	private static final String STATUS_ON_START = "Please import data.";
	
	private static final String READY_ICON = "/icons/ok.png";
	private static final String WARNING_ICON = "/icons/warning.png";
	private static final String ERROR_ICON = "/icons/error.png";
	private static final String CLUSTERING_ICON = "/icons/operation.png";
	private static final String PARAMETERS_ICON = "/icons/tune.png";
	
	private Controller controller = Main.getController();
	private JComboBox<String> comboBoxAlgo;
	private JSpinner spinnerMaxIterations;
	private JSpinner spinnerClusters;
	private JSpinner spinnerSeed;
	private JCheckBox manualSeed;
	private JButton btnRun;
	private JLabel statusLabel;
	
	//scaled icons
	ImageIcon warning;
	ImageIcon ready;
	ImageIcon error;

	/**
	 * Create the panel.
	 */
	public SidePanel() {
		this.setMinimumSize(new Dimension(200, 400));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {10, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblSettingsAndData = new JLabel(TITLE);
		lblSettingsAndData.setFont(new Font("Tahoma", Font.BOLD, 16));
		GridBagConstraints gbc_lblSettingsAndData = new GridBagConstraints();
		gbc_lblSettingsAndData.gridwidth = 2;
		gbc_lblSettingsAndData.insets = new Insets(0, 0, 5, 5);
		gbc_lblSettingsAndData.gridx = 1;
		gbc_lblSettingsAndData.gridy = 0;
		add(lblSettingsAndData, gbc_lblSettingsAndData);
		
		JLabel lblAlgorithm = new JLabel(ALGORITHM_TEXT);
		lblAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 11));
		GridBagConstraints gbc_lblAlgorithm = new GridBagConstraints();
		gbc_lblAlgorithm.insets = new Insets(0, 0, 5, 5);
		gbc_lblAlgorithm.anchor = GridBagConstraints.WEST;
		gbc_lblAlgorithm.gridx = 1;
		gbc_lblAlgorithm.gridy = 1;
		add(lblAlgorithm, gbc_lblAlgorithm);
		
		comboBoxAlgo = new JComboBox<>();
		comboBoxAlgo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(spinnerSeed != null && manualSeed != null && getSelectedAlgorithm() == Algorithms.OLARY){
					if(isManualSeed()) spinnerSeed.setEnabled(true);
					manualSeed.setEnabled(true);
				}
				else if (spinnerSeed != null && manualSeed != null) {
					spinnerSeed.setEnabled(false);
					manualSeed.setEnabled(false);
				}
			}
		});
		comboBoxAlgo.setModel(new DefaultComboBoxModel<String>(new String[] {OLARY, KMEANS}));
		comboBoxAlgo.setSelectedIndex(0);
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 2;
		gbc_comboBox.gridy = 1;
		add(comboBoxAlgo, gbc_comboBox);
		
		btnRun = new JButton(RUN_CLUSTERING_TEXT);
		btnRun.setIcon(new ImageIcon(SidePanel.class.getResource(CLUSTERING_ICON)));
		btnRun.setEnabled(false);
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.runClustering();
			}
		});
		
		JLabel lblParameters = new JLabel("Parameters");
		lblParameters.setIcon(new ImageIcon(SidePanel.class.getResource(PARAMETERS_ICON)));
		lblParameters.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblParameters = new GridBagConstraints();
		gbc_lblParameters.anchor = GridBagConstraints.WEST;
		gbc_lblParameters.gridwidth = 2;
		gbc_lblParameters.insets = new Insets(0, 0, 5, 5);
		gbc_lblParameters.gridx = 1;
		gbc_lblParameters.gridy = 2;
		add(lblParameters, gbc_lblParameters);
		
		//adjust the spinner's width
		spinnerMaxIterations = new JSpinner();
		spinnerMaxIterations.setModel(new SpinnerNumberModel(new Integer(20), new Integer(1), null, new Integer(2)));
		JComponent componentMaxIter = (JSpinner.DefaultEditor) spinnerMaxIterations.getEditor();
		Dimension sizeMaxIter = componentMaxIter.getPreferredSize();
		sizeMaxIter = new Dimension(45, sizeMaxIter.height);
		componentMaxIter.setPreferredSize(sizeMaxIter);
		
		JLabel lblMaxIterations = new JLabel(MAX_ITERATIONS_TEXT);
		lblMaxIterations.setToolTipText(MAX_ITERATIONS_DESC);
		lblMaxIterations.setFont(new Font("Tahoma", Font.PLAIN, 11));
		GridBagConstraints gbc_lblMaxIterations = new GridBagConstraints();
		gbc_lblMaxIterations.anchor = GridBagConstraints.WEST;
		gbc_lblMaxIterations.insets = new Insets(0, 0, 5, 5);
		gbc_lblMaxIterations.gridx = 1;
		gbc_lblMaxIterations.gridy = 4;
		add(lblMaxIterations, gbc_lblMaxIterations);
		
		GridBagConstraints gbc_spinnerMaxIterations = new GridBagConstraints();
		gbc_spinnerMaxIterations.anchor = GridBagConstraints.WEST;
		gbc_spinnerMaxIterations.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerMaxIterations.gridx = 2;
		gbc_spinnerMaxIterations.gridy = 4;
		add(spinnerMaxIterations, gbc_spinnerMaxIterations);
		
		spinnerClusters = new JSpinner();
		spinnerClusters.setModel(new SpinnerNumberModel(new Integer(3), new Integer(1), null, new Integer(1)));
		JComponent componentClusters = (JSpinner.DefaultEditor) spinnerClusters.getEditor();
		componentClusters.setPreferredSize(sizeMaxIter);
		
		JLabel lblClusters = new JLabel(CLUSTERS_TEXT);
		lblClusters.setFont(new Font("Tahoma", Font.PLAIN, 11));
		GridBagConstraints gbc_lblClusters = new GridBagConstraints();
		gbc_lblClusters.anchor = GridBagConstraints.WEST;
		gbc_lblClusters.insets = new Insets(0, 0, 5, 5);
		gbc_lblClusters.gridx = 1;
		gbc_lblClusters.gridy = 5;
		add(lblClusters, gbc_lblClusters);
		GridBagConstraints gbc_spinnerClusters = new GridBagConstraints();
		gbc_spinnerClusters.anchor = GridBagConstraints.WEST;
		gbc_spinnerClusters.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerClusters.gridx = 2;
		gbc_spinnerClusters.gridy = 5;
		add(spinnerClusters, gbc_spinnerClusters);
		
		spinnerSeed = new JSpinner();
		spinnerSeed.setEnabled(false);
		spinnerSeed.setModel(new SpinnerNumberModel(new Integer(1), new Integer(0), null, new Integer(1)));
		JComponent componentSeed = (JSpinner.DefaultEditor) spinnerSeed.getEditor();
		componentSeed.setPreferredSize(sizeMaxIter);
		
		manualSeed = new JCheckBox(SEED_TEXT);
		manualSeed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(isManualSeed() && spinnerSeed != null){
					spinnerSeed.setEnabled(true);
				} else if(spinnerSeed != null){
					spinnerSeed.setEnabled(false);
				}
			}
		});
		manualSeed.setFont(new Font("Tahoma", Font.PLAIN, 11));
		GridBagConstraints gbc_manualSeed = new GridBagConstraints();
		gbc_manualSeed.anchor = GridBagConstraints.WEST;
		gbc_manualSeed.insets = new Insets(0, 0, 5, 5);
		gbc_manualSeed.gridx = 1;
		gbc_manualSeed.gridy = 6;
		add(manualSeed, gbc_manualSeed);
		GridBagConstraints gbc_spinnerSeed = new GridBagConstraints();
		gbc_spinnerSeed.anchor = GridBagConstraints.WEST;
		gbc_spinnerSeed.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerSeed.gridx = 2;
		gbc_spinnerSeed.gridy = 6;
		add(spinnerSeed, gbc_spinnerSeed);
		
		statusLabel = new JLabel();
		statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		GridBagConstraints gbc_statusLabel = new GridBagConstraints();
		gbc_statusLabel.gridwidth = 2;
		gbc_statusLabel.anchor = GridBagConstraints.EAST;
		gbc_statusLabel.insets = new Insets(0, 0, 5, 5);
		gbc_statusLabel.gridx = 1;
		gbc_statusLabel.gridy = 7;
		add(statusLabel, gbc_statusLabel);
		GridBagConstraints gbc_btnRun = new GridBagConstraints();
		gbc_btnRun.gridwidth = 3;
		gbc_btnRun.insets = new Insets(0, 0, 5, 5);
		gbc_btnRun.anchor = GridBagConstraints.EAST;
		gbc_btnRun.gridx = 0;
		gbc_btnRun.gridy = 8;
		add(btnRun, gbc_btnRun);
		
		try {
			Image readyImage = ImageIO.read(SidePanel.class.getResource(READY_ICON));
			Image warningImage = ImageIO.read(SidePanel.class.getResource(WARNING_ICON));
			Image errorImage = ImageIO.read(SidePanel.class.getResource(ERROR_ICON));
			this.ready = new ImageIcon(readyImage.getScaledInstance(12, 12, Image.SCALE_SMOOTH));
			this.warning = new ImageIcon(warningImage.getScaledInstance(12, 12, Image.SCALE_SMOOTH));
			this.error = new ImageIcon(errorImage.getScaledInstance(12, 12, Image.SCALE_SMOOTH));
		} catch (IOException e1) {
			Main.getController().getMainWindow().showUnhandledException(e1);
		}
		
		setStatus(STATUS_ON_START, STATUS_TYPE_WARNING);
	}
	
	public int getClusterNumber(){
		return (int)spinnerClusters.getValue();
	}
	
	public int getIterations(){
		return (int)spinnerMaxIterations.getValue();
	}
	
	//below are getters for the input fields
	
	public int getSeed(){
		return (int)spinnerSeed.getValue();
	}
	
	public Algorithms getSelectedAlgorithm(){
		if (comboBoxAlgo.getSelectedItem().equals(OLARY))
			return Algorithms.OLARY;
		else if (comboBoxAlgo.getSelectedItem().equals(KMEANS))
			return Algorithms.KMEANS;
		else
			return null;
	}
	
	public boolean isManualSeed(){
		return this.manualSeed.isSelected();
	}
	
	/**
	 * Enable/disable the run button.
	 */
	public void setRunEnabled(boolean enable){
		this.btnRun.setEnabled(enable);
	}
	
	/**
	 * Set the status display above the Run Clustering button.
	 * @param text
	 * 		the text to display
	 * @param statusType
	 * 		the icon to display before the text
	 * 		<ul><li><b>STATUS_TYPE_READY:</b> Indicates no warning, or error.</li>
	 * 		<li><b>STATUS_TYPE_WARNING:</b> Indicates that there is a warning present.</li>
	 * 		<li><b>STATUS_TYPE_ERROR:</b> Indicates that an error has happened.</li>
	 * 		<li><b>STATUS_TYPE_NONE:</b> Do not display any icon.</li></ul>
	 */
	public void setStatus(String text, int statusType){
		//set the icon of the label
		if(statusType == STATUS_TYPE_READY)
			statusLabel.setIcon(ready);
		else if(statusType == STATUS_TYPE_WARNING)
			statusLabel.setIcon(warning);
		else if(statusType == STATUS_TYPE_ERROR)
			statusLabel.setIcon(error);
		else
			statusLabel.setIcon(null);
		
		//set the text of the label
		statusLabel.setText(text);
	}

}
