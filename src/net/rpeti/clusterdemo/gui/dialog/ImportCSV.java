package net.rpeti.clusterdemo.gui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;

import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
		setSize(330, 170);
		setLocationRelativeTo(parent);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		
		isOk = false;
		
		JLabel lblSeparator = new JLabel("Separator");
		lblSeparator.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSeparator.setToolTipText("Can be any character, sequence of characters, or Java Regular Expression.\r\nThe default separator is a comma (\",\").");
		
		textFieldSeparator = new JTextField();
		textFieldSeparator.setToolTipText("Can be any character, sequence of characters, or Java Regular Expression.\r\nThe default separator is a comma (\",\").");
		textFieldSeparator.setText(",");
		textFieldSeparator.setColumns(10);
		
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
		
		checkBoxAttributes = new JCheckBox("Attributes in first line");
		checkBoxAttributes.setToolTipText("Check if the attributes of the data set is present in the first line of the CSV file");
		
		JLabel lblBrowseFile = new JLabel("Browse file");
		lblBrowseFile.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		textFieldPath = new JTextField();
		textFieldPath.setColumns(10);
		
		JButton btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO: filefilter más platformokon?
				FileDialog chooser = new FileDialog(ImportCSV.this, "Choose a file", FileDialog.LOAD);
				chooser.setFile("*.csv");
				chooser.setVisible(true);
				String filename = chooser.getDirectory() + chooser.getFile();
				if(chooser.getFile() != null){
					textFieldPath.setText(filename);
					file = new File(filename);
				}
			}
		});
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(checkBoxAttributes)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(lblBrowseFile)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textFieldPath, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnBrowse))
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(lblSeparator)
					.addGap(5)
					.addComponent(textFieldSeparator, GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnHelp))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblBrowseFile)
						.addComponent(textFieldPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowse))
					.addGap(4)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSeparator)
						.addComponent(textFieldSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnHelp))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(checkBoxAttributes)
					.addGap(11))
		);
		contentPanel.setLayout(gl_contentPanel);
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
