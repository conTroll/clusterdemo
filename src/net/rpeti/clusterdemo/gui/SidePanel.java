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

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.DefaultComboBoxModel;

import net.rpeti.clusterdemo.Controller;
import net.rpeti.clusterdemo.Main;
import net.rpeti.clusterdemo.algorithms.Algorithms;
import javax.swing.JCheckBox;

public class SidePanel extends JPanel {
	
	// TODO set it dynamic to algorithm selection
	// TODO ability to select attributes to cluster on
	// TODO validate parameters
	// TODO implement data editor (for a single data)
	// TODO comments

	private static final long serialVersionUID = 7654943576215466209L;
	
	private Controller controller = Main.getController();
	private JComboBox<String> comboBoxAlgo;
	private JSpinner spinnerMaxIterations;
	private JSpinner spinnerClusters;
	private JSpinner spinnerSeed;

	/**
	 * Create the panel.
	 */
	public SidePanel() {
		this.setMinimumSize(new Dimension(200, 400));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblSettingsAndData = new JLabel("Settings and Data");
		lblSettingsAndData.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblSettingsAndData = new GridBagConstraints();
		gbc_lblSettingsAndData.gridwidth = 2;
		gbc_lblSettingsAndData.insets = new Insets(0, 0, 5, 0);
		gbc_lblSettingsAndData.gridx = 0;
		gbc_lblSettingsAndData.gridy = 0;
		add(lblSettingsAndData, gbc_lblSettingsAndData);
		
		JLabel lblAlgorithm = new JLabel("Algorithm");
		lblAlgorithm.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblAlgorithm = new GridBagConstraints();
		gbc_lblAlgorithm.insets = new Insets(0, 0, 5, 5);
		gbc_lblAlgorithm.anchor = GridBagConstraints.WEST;
		gbc_lblAlgorithm.gridx = 0;
		gbc_lblAlgorithm.gridy = 1;
		add(lblAlgorithm, gbc_lblAlgorithm);
		
		comboBoxAlgo = new JComboBox<>();
		comboBoxAlgo.setModel(new DefaultComboBoxModel<String>(new String[] {"Olary"}));
		comboBoxAlgo.setSelectedIndex(0);
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 1;
		add(comboBoxAlgo, gbc_comboBox);
		
		JButton btnRun = new JButton("Run Clustering");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.runClustering(SidePanel.this.getSelectedAlgorithm(), 
						(int)SidePanel.this.spinnerClusters.getValue(), (int)SidePanel.this.spinnerSeed.getValue(),
						(int)SidePanel.this.spinnerMaxIterations.getValue());
			}
		});
		
		JLabel lblParameters = new JLabel("Parameters");
		lblParameters.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblParameters = new GridBagConstraints();
		gbc_lblParameters.gridwidth = 2;
		gbc_lblParameters.insets = new Insets(0, 0, 5, 0);
		gbc_lblParameters.gridx = 0;
		gbc_lblParameters.gridy = 2;
		add(lblParameters, gbc_lblParameters);
		
		JLabel lblMaxIterations = new JLabel("Max iterations");
		lblMaxIterations.setToolTipText("The algorithm will stop after this number of iterations even if the convergence criteria haven't met.");
		lblMaxIterations.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblMaxIterations = new GridBagConstraints();
		gbc_lblMaxIterations.anchor = GridBagConstraints.WEST;
		gbc_lblMaxIterations.insets = new Insets(0, 0, 5, 5);
		gbc_lblMaxIterations.gridx = 0;
		gbc_lblMaxIterations.gridy = 3;
		add(lblMaxIterations, gbc_lblMaxIterations);
		
		spinnerMaxIterations = new JSpinner();
		spinnerMaxIterations.setModel(new SpinnerNumberModel(new Integer(50), new Integer(1), null, new Integer(5)));
		JComponent componentMaxIter = (JSpinner.DefaultEditor) spinnerMaxIterations.getEditor();
		Dimension sizeMaxIter = componentMaxIter.getPreferredSize();
		sizeMaxIter = new Dimension(45, sizeMaxIter.height);
		componentMaxIter.setPreferredSize(sizeMaxIter);
		
		GridBagConstraints gbc_spinnerMaxIterations = new GridBagConstraints();
		gbc_spinnerMaxIterations.anchor = GridBagConstraints.WEST;
		gbc_spinnerMaxIterations.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerMaxIterations.gridx = 1;
		gbc_spinnerMaxIterations.gridy = 3;
		add(spinnerMaxIterations, gbc_spinnerMaxIterations);
		
		JLabel lblClusters = new JLabel("Clusters");
		lblClusters.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblClusters = new GridBagConstraints();
		gbc_lblClusters.anchor = GridBagConstraints.WEST;
		gbc_lblClusters.insets = new Insets(0, 0, 5, 5);
		gbc_lblClusters.gridx = 0;
		gbc_lblClusters.gridy = 4;
		add(lblClusters, gbc_lblClusters);
		
		spinnerClusters = new JSpinner();
		spinnerClusters.setModel(new SpinnerNumberModel(new Integer(3), new Integer(1), null, new Integer(1)));
		JComponent componentClusters = (JSpinner.DefaultEditor) spinnerClusters.getEditor();
		componentClusters.setPreferredSize(sizeMaxIter);
		GridBagConstraints gbc_spinnerClusters = new GridBagConstraints();
		gbc_spinnerClusters.anchor = GridBagConstraints.WEST;
		gbc_spinnerClusters.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerClusters.gridx = 1;
		gbc_spinnerClusters.gridy = 4;
		add(spinnerClusters, gbc_spinnerClusters);
		
		JCheckBox chckbxSeed = new JCheckBox("Seed");
		chckbxSeed.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_chckbxSeed = new GridBagConstraints();
		gbc_chckbxSeed.anchor = GridBagConstraints.WEST;
		gbc_chckbxSeed.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxSeed.gridx = 0;
		gbc_chckbxSeed.gridy = 5;
		add(chckbxSeed, gbc_chckbxSeed);
		
		spinnerSeed = new JSpinner();
		spinnerSeed.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		JComponent componentSeed = (JSpinner.DefaultEditor) spinnerSeed.getEditor();
		componentSeed.setPreferredSize(sizeMaxIter);
		GridBagConstraints gbc_spinnerSeed = new GridBagConstraints();
		gbc_spinnerSeed.anchor = GridBagConstraints.WEST;
		gbc_spinnerSeed.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerSeed.gridx = 1;
		gbc_spinnerSeed.gridy = 5;
		add(spinnerSeed, gbc_spinnerSeed);
		GridBagConstraints gbc_btnRun = new GridBagConstraints();
		gbc_btnRun.insets = new Insets(0, 0, 5, 0);
		gbc_btnRun.anchor = GridBagConstraints.EAST;
		gbc_btnRun.gridx = 1;
		gbc_btnRun.gridy = 6;
		add(btnRun, gbc_btnRun);
		
		JLabel lblDataEditor = new JLabel("Data Editor");
		lblDataEditor.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblDataEditor = new GridBagConstraints();
		gbc_lblDataEditor.gridwidth = 2;
		gbc_lblDataEditor.gridx = 0;
		gbc_lblDataEditor.gridy = 7;
		add(lblDataEditor, gbc_lblDataEditor);

	}
	
	public Algorithms getSelectedAlgorithm(){
		if (comboBoxAlgo.getSelectedItem().equals("Olary"))
			return Algorithms.OLARY;
		else
			return null;
	}

}
