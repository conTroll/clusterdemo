package net.rpeti.clusterdemo.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JLabel;

import net.rpeti.clusterdemo.Controller;
import net.rpeti.clusterdemo.Main;


/**
 * Gets the needed parameters for reading a CSV file.
 * Validates user input.
 */
public class ClusteringProgress extends JDialog {
	
	private static final String ITERATIONS = "Iterations";

	private static final long serialVersionUID = -6923122498143969365L;
	private JProgressBar progressBar;
	private Controller controller;
	private JFrame parent;
	private JLabel status;

	/**
	 * Create the dialog.
	 */
	public ClusteringProgress(JFrame parent) {
		super(parent, false);
		this.parent = parent;
		this.controller = Main.getController();
		
		//do not block program execution, but block main window
		setModalityType(ModalityType.MODELESS);
		this.parent.setEnabled(false);
		
		setTitle("Clustering...");
		setSize(280, 100);
		setLocationRelativeTo(parent);
		setResizable(false);
		getContentPane().setLayout(null);
	
	
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 40, 264, 33);
			getContentPane().add(buttonPane);
				GridBagLayout gbl_buttonPane = new GridBagLayout();
				gbl_buttonPane.columnWidths = new int[] {5, 0, 0};
				gbl_buttonPane.rowHeights = new int[]{33, 0};
				gbl_buttonPane.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0};
				gbl_buttonPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
				buttonPane.setLayout(gbl_buttonPane);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.cancelClustering();
				setVisible(false);
				dispose();
				ClusteringProgress.this.parent.setEnabled(true);
				ClusteringProgress.this.parent.toFront();
			}
		});
		
		status = new JLabel("Initializing...");
		status.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_status = new GridBagConstraints();
		gbc_status.anchor = GridBagConstraints.WEST;
		gbc_status.fill = GridBagConstraints.VERTICAL;
		gbc_status.insets = new Insets(0, 0, 0, 5);
		gbc_status.gridx = 1;
		gbc_status.gridy = 0;
		buttonPane.add(status, gbc_status);
		cancelButton.setActionCommand("Cancel");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.WEST;
		gbc_cancelButton.fill = GridBagConstraints.VERTICAL;
		gbc_cancelButton.gridx = 3;
		gbc_cancelButton.gridy = 0;
		buttonPane.add(cancelButton, gbc_cancelButton);
		//status.setHorizontalAlignment(SwingConstants.LEFT);

		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setBounds(6, 7, 258, 27);
		getContentPane().add(progressBar);
		setVisible(true);
		
		
	}
	
	/**
	 * Set the progress of clustering.
	 * @param value
	 * 		a number between 0 and 100 (indicating progress)
	 */
	public void setProgress(int iteration, int maxIterations){
		progressBar.setIndeterminate(false);
		progressBar.setValue((iteration * 100) / maxIterations);
		status.setText(ITERATIONS + ": " + iteration + "/" + maxIterations);
	}
	
	/**
	 * Closes the dialog.
	 */
	public void close(){
		setVisible(false);
		dispose();
		this.parent.setEnabled(true);
		this.parent.toFront();
	}
}