package net.rpeti.clusterdemo.gui.dialog;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.rpeti.clusterdemo.Controller;
import net.rpeti.clusterdemo.Main;

//TODO iterációk számát kiíratni

/**
 * Gets the needed parameters for reading a CSV file.
 * Validates user input.
 */
public class ClusteringProgress extends JDialog {

	private static final long serialVersionUID = -6923122498143969365L;
	private JProgressBar progressBar;
	private Controller controller;
	private JFrame parent;

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
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane);
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
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
	

		
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
	public void setProgress(int value){
		progressBar.setIndeterminate(false);
		progressBar.setValue(value);
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