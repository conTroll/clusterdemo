package net.rpeti.clusterdemo.gui;

import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;

import javax.swing.JComboBox;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Font;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.nio.channels.SeekableByteChannel;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.DefaultComboBoxModel;

import net.rpeti.clusterdemo.Controller;
import net.rpeti.clusterdemo.Main;
import net.rpeti.clusterdemo.algorithms.Algorithms;

import javax.swing.JCheckBox;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

public class SidePanel extends JPanel {
	
	// TODO ability to select attributes to cluster on
	// TODO write live statistics and info about status
	// TODO comments

	private static final long serialVersionUID = 7654943576215466209L;
	private static final String OLARY = "Olary";
	private static final String KMEANS = "K-Means";
	
	private Controller controller = Main.getController();
	private JComboBox<String> comboBoxAlgo;
	private JSpinner spinnerMaxIterations;
	private JSpinner spinnerClusters;
	private JSpinner spinnerSeed;
	private JCheckBox manualSeed;
	private JButton btnRun;

	/**
	 * Create the panel.
	 */
	public SidePanel() {
		this.setMinimumSize(new Dimension(200, 400));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {10, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblSettingsAndData = new JLabel("Settings & Stats");
		lblSettingsAndData.setFont(new Font("Tahoma", Font.BOLD, 16));
		GridBagConstraints gbc_lblSettingsAndData = new GridBagConstraints();
		gbc_lblSettingsAndData.gridwidth = 2;
		gbc_lblSettingsAndData.insets = new Insets(0, 0, 5, 0);
		gbc_lblSettingsAndData.gridx = 1;
		gbc_lblSettingsAndData.gridy = 0;
		add(lblSettingsAndData, gbc_lblSettingsAndData);
		
		JLabel lblAlgorithm = new JLabel("Algorithm");
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
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 2;
		gbc_comboBox.gridy = 1;
		add(comboBoxAlgo, gbc_comboBox);
		
		btnRun = new JButton("Run Clustering");
		btnRun.setIcon(new ImageIcon(SidePanel.class.getResource("/icons/operation.png")));
		btnRun.setEnabled(false);
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.runClustering();
			}
		});
		
		JLabel lblParameters = new JLabel("Parameters");
		lblParameters.setIcon(new ImageIcon(SidePanel.class.getResource("/icons/tune.png")));
		lblParameters.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblParameters = new GridBagConstraints();
		gbc_lblParameters.anchor = GridBagConstraints.WEST;
		gbc_lblParameters.gridwidth = 2;
		gbc_lblParameters.insets = new Insets(0, 0, 5, 0);
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
		
		JLabel lblMaxIterations = new JLabel("Max iterations");
		lblMaxIterations.setToolTipText("The algorithm will stop after this number of iterations even if the convergence criteria haven't met.");
		lblMaxIterations.setFont(new Font("Tahoma", Font.PLAIN, 11));
		GridBagConstraints gbc_lblMaxIterations = new GridBagConstraints();
		gbc_lblMaxIterations.anchor = GridBagConstraints.WEST;
		gbc_lblMaxIterations.insets = new Insets(0, 0, 5, 5);
		gbc_lblMaxIterations.gridx = 1;
		gbc_lblMaxIterations.gridy = 3;
		add(lblMaxIterations, gbc_lblMaxIterations);
		
		GridBagConstraints gbc_spinnerMaxIterations = new GridBagConstraints();
		gbc_spinnerMaxIterations.anchor = GridBagConstraints.WEST;
		gbc_spinnerMaxIterations.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerMaxIterations.gridx = 2;
		gbc_spinnerMaxIterations.gridy = 3;
		add(spinnerMaxIterations, gbc_spinnerMaxIterations);
		
		spinnerClusters = new JSpinner();
		spinnerClusters.setModel(new SpinnerNumberModel(new Integer(3), new Integer(1), null, new Integer(1)));
		JComponent componentClusters = (JSpinner.DefaultEditor) spinnerClusters.getEditor();
		componentClusters.setPreferredSize(sizeMaxIter);
		
		JLabel lblClusters = new JLabel("Clusters");
		lblClusters.setFont(new Font("Tahoma", Font.PLAIN, 11));
		GridBagConstraints gbc_lblClusters = new GridBagConstraints();
		gbc_lblClusters.anchor = GridBagConstraints.WEST;
		gbc_lblClusters.insets = new Insets(0, 0, 5, 5);
		gbc_lblClusters.gridx = 1;
		gbc_lblClusters.gridy = 4;
		add(lblClusters, gbc_lblClusters);
		GridBagConstraints gbc_spinnerClusters = new GridBagConstraints();
		gbc_spinnerClusters.anchor = GridBagConstraints.WEST;
		gbc_spinnerClusters.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerClusters.gridx = 2;
		gbc_spinnerClusters.gridy = 4;
		add(spinnerClusters, gbc_spinnerClusters);
		
		spinnerSeed = new JSpinner();
		spinnerSeed.setEnabled(false);
		spinnerSeed.setModel(new SpinnerNumberModel(new Integer(1), new Integer(0), null, new Integer(1)));
		JComponent componentSeed = (JSpinner.DefaultEditor) spinnerSeed.getEditor();
		componentSeed.setPreferredSize(sizeMaxIter);
		
		manualSeed = new JCheckBox("Seed");
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
		gbc_manualSeed.gridy = 5;
		add(manualSeed, gbc_manualSeed);
		GridBagConstraints gbc_spinnerSeed = new GridBagConstraints();
		gbc_spinnerSeed.anchor = GridBagConstraints.WEST;
		gbc_spinnerSeed.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerSeed.gridx = 2;
		gbc_spinnerSeed.gridy = 5;
		add(spinnerSeed, gbc_spinnerSeed);
		GridBagConstraints gbc_btnRun = new GridBagConstraints();
		gbc_btnRun.gridwidth = 3;
		gbc_btnRun.insets = new Insets(0, 0, 5, 0);
		gbc_btnRun.anchor = GridBagConstraints.EAST;
		gbc_btnRun.gridx = 0;
		gbc_btnRun.gridy = 6;
		add(btnRun, gbc_btnRun);
		
		JLabel lblDataEditor = new JLabel("Information");
		lblDataEditor.setIcon(new ImageIcon(SidePanel.class.getResource("/icons/info.png")));
		lblDataEditor.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblDataEditor = new GridBagConstraints();
		gbc_lblDataEditor.anchor = GridBagConstraints.WEST;
		gbc_lblDataEditor.gridwidth = 2;
		gbc_lblDataEditor.gridx = 1;
		gbc_lblDataEditor.gridy = 7;
		add(lblDataEditor, gbc_lblDataEditor);

	}
	
	public void enableRun(){
		this.btnRun.setEnabled(true);
	}
	
	public boolean isManualSeed(){
		return this.manualSeed.isSelected();
	}
	
	public int getSeed(){
		return (int)spinnerSeed.getValue();
	}
	
	public int getClusterNumber(){
		return (int)spinnerClusters.getValue();
	}
	
	public int getIterations(){
		return (int)spinnerMaxIterations.getValue();
	}
	
	public Algorithms getSelectedAlgorithm(){
		if (comboBoxAlgo.getSelectedItem().equals(OLARY))
			return Algorithms.OLARY;
		else if (comboBoxAlgo.getSelectedItem().equals(KMEANS))
			return Algorithms.KMEANS;
		else
			return null;
	}

}
