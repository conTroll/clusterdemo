package net.rpeti.clusterdemo.gui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import javax.swing.JLabel;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
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
	public ImportCSV(JFrame parent) {
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
				JFileChooser chooser = new JFileChooser();
				chooser.setPreferredSize(new Dimension(640, 480));
				FileFilter ff = new FileFilter() {

					@Override
					public String getDescription() {
						return "CSV file";
					}

					@Override
					public boolean accept(File f) {
						if (f.isDirectory()) return true;
						String[] split = f.getName().trim().split(".");
						if (split.length == 0) return false;
						return split[split.length - 1].toLowerCase().equals("csv");
					}
				};
				chooser.addChoosableFileFilter(ff);
				if(chooser.showOpenDialog(ImportCSV.this) == JFileChooser.APPROVE_OPTION){
					try {
						file = chooser.getSelectedFile();
						textFieldPath.setText(file.getCanonicalPath());
					} catch (IOException e) {
						JOptionPane.showMessageDialog(ImportCSV.this, "Invalid file selected.\nPlease select a valid file.", "Error", JOptionPane.ERROR_MESSAGE);
					}
					
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
	
	public boolean getIsOk(){
		return isOk;
	}
	
	public File getSelectedFile(){
		return file;
	}
	
	public String getSeparator(){
		return textFieldSeparator.getText();
	}
	
	public boolean getIsAttributesInFirstLine(){
		return checkBoxAttributes.isSelected();
	}
}
