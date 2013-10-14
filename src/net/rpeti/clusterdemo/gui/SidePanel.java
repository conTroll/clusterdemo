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

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.DefaultComboBoxModel;

import net.rpeti.clusterdemo.Controller;
import net.rpeti.clusterdemo.Main;
import net.rpeti.clusterdemo.algorithms.Algorithms;

public class SidePanel extends JPanel {

	private static final long serialVersionUID = 7654943576215466209L;
	
	private Controller controller = Main.getController();
	private JComboBox<String> comboBoxAlgo;

	/**
	 * Create the panel.
	 */
	public SidePanel() {
		this.setMinimumSize(new Dimension(275, 400));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
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
				controller.runClustering(SidePanel.this.getSelectedAlgorithm());
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
		
		JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(new Integer(200), null, null, new Integer(10)));
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.anchor = GridBagConstraints.WEST;
		gbc_spinner.insets = new Insets(0, 0, 5, 0);
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 3;
		add(spinner, gbc_spinner);
		
		JLabel lblThreshold = new JLabel("Threshold");
		lblThreshold.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblThreshold = new GridBagConstraints();
		gbc_lblThreshold.anchor = GridBagConstraints.WEST;
		gbc_lblThreshold.insets = new Insets(0, 0, 5, 5);
		gbc_lblThreshold.gridx = 0;
		gbc_lblThreshold.gridy = 4;
		add(lblThreshold, gbc_lblThreshold);
		
		JSpinner spinner_1 = new JSpinner();
		GridBagConstraints gbc_spinner_1 = new GridBagConstraints();
		gbc_spinner_1.anchor = GridBagConstraints.WEST;
		gbc_spinner_1.insets = new Insets(0, 0, 5, 0);
		gbc_spinner_1.gridx = 1;
		gbc_spinner_1.gridy = 4;
		add(spinner_1, gbc_spinner_1);
		GridBagConstraints gbc_btnRun = new GridBagConstraints();
		gbc_btnRun.insets = new Insets(0, 0, 5, 0);
		gbc_btnRun.anchor = GridBagConstraints.EAST;
		gbc_btnRun.gridx = 1;
		gbc_btnRun.gridy = 5;
		add(btnRun, gbc_btnRun);
		
		JLabel lblDataEditor = new JLabel("Data Editor");
		lblDataEditor.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblDataEditor = new GridBagConstraints();
		gbc_lblDataEditor.gridwidth = 2;
		gbc_lblDataEditor.gridx = 0;
		gbc_lblDataEditor.gridy = 6;
		add(lblDataEditor, gbc_lblDataEditor);

	}
	
	public Algorithms getSelectedAlgorithm(){
		if (comboBoxAlgo.getSelectedItem().equals("Olary"))
			return Algorithms.OLARY;
		else
			return null;
	}

}
