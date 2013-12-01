package net.rpeti.clusterdemo.gui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Gets the needed parameters for reading a CSV file.
 * Validates user input.
 */
public class ImportCSV extends JDialog {

	private static final String REGEX_TUTORIAL_LINK = "http://docs.oracle.com/javase/tutorial/essential/regex/";
	private static final long serialVersionUID = 3953180199911222020L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldSeparator;
	private String separator;
	private JCheckBox checkBoxAttributes;
	private JTextField textFieldPath;
	private File file;
	private boolean isOk;

	/**
	 * Create the dialog.
	 */
	public ImportCSV(final JFrame parent) {
		super(parent, true);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setTitle("Import CSV...");
		setSize(330, 160);
		setLocationRelativeTo(parent);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		
		isOk = false;
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] {61, 53, 90, 0};
		gbl_contentPanel.rowHeights = new int[]{23, 23, 21, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		
		JLabel lblBrowseFile = new JLabel("File");
		lblBrowseFile.setFont(new Font("Tahoma", Font.PLAIN, 11));
		GridBagConstraints gbc_lblBrowseFile = new GridBagConstraints();
		gbc_lblBrowseFile.anchor = GridBagConstraints.WEST;
		gbc_lblBrowseFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblBrowseFile.gridx = 0;
		gbc_lblBrowseFile.gridy = 0;
		contentPanel.add(lblBrowseFile, gbc_lblBrowseFile);
		
		textFieldPath = new JTextField();
		textFieldPath.setColumns(10);
		GridBagConstraints gbc_textFieldPath = new GridBagConstraints();
		gbc_textFieldPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldPath.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldPath.gridwidth = 2;
		gbc_textFieldPath.gridx = 1;
		gbc_textFieldPath.gridy = 0;
		contentPanel.add(textFieldPath, gbc_textFieldPath);
		
		JButton btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO: filefilter más platformokon?
				ImportCSV.this.setEnabled(false);
				FileDialog chooser = new FileDialog(ImportCSV.this, "Choose a file", FileDialog.LOAD);
				chooser.setFile("*.csv");
				chooser.setVisible(true);
				String filename = chooser.getDirectory() + chooser.getFile();
				if(chooser.getFile() != null){
					textFieldPath.setText(filename);
					file = new File(filename);
				}
				ImportCSV.this.setEnabled(true);
				ImportCSV.this.toFront();
			}
		});
		GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
		gbc_btnBrowse.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnBrowse.insets = new Insets(0, 0, 5, 0);
		gbc_btnBrowse.gridx = 3;
		gbc_btnBrowse.gridy = 0;
		contentPanel.add(btnBrowse, gbc_btnBrowse);
		
		JLabel lblSeparator = new JLabel("Separator");
		lblSeparator.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblSeparator.setToolTipText("Can be any character, sequence of characters, or Java Regular Expression.\r\nThe default separator is a semicolon (\";\").");
		GridBagConstraints gbc_lblSeparator = new GridBagConstraints();
		gbc_lblSeparator.anchor = GridBagConstraints.WEST;
		gbc_lblSeparator.insets = new Insets(0, 0, 5, 5);
		gbc_lblSeparator.gridx = 0;
		gbc_lblSeparator.gridy = 1;
		contentPanel.add(lblSeparator, gbc_lblSeparator);
		
		textFieldSeparator = new JTextField();
		textFieldSeparator.setToolTipText("Can be any character, sequence of characters, or Java Regular Expression.\r\nThe default separator is a semicolon (\";\").");
		textFieldSeparator.setText(";");
		textFieldSeparator.setColumns(10);
		GridBagConstraints gbc_textFieldSeparator = new GridBagConstraints();
		gbc_textFieldSeparator.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldSeparator.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldSeparator.gridwidth = 2;
		gbc_textFieldSeparator.gridx = 1;
		gbc_textFieldSeparator.gridy = 1;
		contentPanel.add(textFieldSeparator, gbc_textFieldSeparator);
		
		JButton btnHelp = new JButton("Help");
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
				if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE)){
					try {
						desktop.browse(new URI(REGEX_TUTORIAL_LINK));
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(ImportCSV.this, "Error happened while trying to open a link in the default browser.",  "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(ImportCSV.this, "Can't reach browser in the current platform.",  "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		GridBagConstraints gbc_btnHelp = new GridBagConstraints();
		gbc_btnHelp.anchor = GridBagConstraints.NORTH;
		gbc_btnHelp.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnHelp.insets = new Insets(0, 0, 5, 0);
		gbc_btnHelp.gridx = 3;
		gbc_btnHelp.gridy = 1;
		contentPanel.add(btnHelp, gbc_btnHelp);
		
		checkBoxAttributes = new JCheckBox();
		checkBoxAttributes.setSelected(true);
		checkBoxAttributes.setText("Attributes in first line");
		checkBoxAttributes.setToolTipText("Check if the attributes of the data set is present in the first line of the CSV file");
		GridBagConstraints gbc_checkBoxAttributes = new GridBagConstraints();
		gbc_checkBoxAttributes.gridwidth = 3;
		gbc_checkBoxAttributes.anchor = GridBagConstraints.NORTHWEST;
		gbc_checkBoxAttributes.insets = new Insets(0, 0, 0, 5);
		gbc_checkBoxAttributes.gridx = 0;
		gbc_checkBoxAttributes.gridy = 2;
		contentPanel.add(checkBoxAttributes, gbc_checkBoxAttributes);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						file = new File(textFieldPath.getText());
						if (!(file.isAbsolute() && file.isFile() && file.exists())){
							JOptionPane.showMessageDialog(ImportCSV.this, "Please correct the file path.", "Invalid file path", JOptionPane.ERROR_MESSAGE);
							return;
						}
						separator = textFieldSeparator.getText().trim();
						try{
							Pattern.compile(separator);
							isOk = true;
							setVisible(false);
							dispose();
						} catch (PatternSyntaxException e1){
							JOptionPane.showMessageDialog(ImportCSV.this, "Invalid regular expression provided.\nClick the Help button to see a tutorial on how to build Java Regular Expressions.", "Invalid regular expression", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		setVisible(true);
	}
	
	/**
	 * @return true if and only if the user clicked on OK button,
	 * and the data provided validates correctly.
	 */
	public boolean getIsOk(){
		return isOk;
	}
	
	/**
	 * @return a File object representing the file selected by the
	 * user. Contains the absolute path to the file which is specific
	 * to the platform being used.
	 */
	public File getSelectedFile(){
		return file;
	}
	
	/**
	 * @return the regular expression provided by the user
	 * as separator as a String
	 */
	public String getSeparator(){
		return textFieldSeparator.getText();
	}
	
	/**
	 * @return true if and only if the checkbox 
	 * 'attributes in first line' was checked by the user
	 */
	public boolean getIsAttributesInFirstLine(){
		return checkBoxAttributes.isSelected();
	}
}
