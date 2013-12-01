package net.rpeti.clusterdemo.gui.dialog;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.rpeti.clusterdemo.Main;
import net.rpeti.clusterdemo.gui.MainWindow;

public class NodeEditor {
	
	MainWindow mainWindow;
	int select;
	List<String> values;
	
	private static final int PANEL_WIDTH = 300;
	private static final int MAX_PANEL_HEIGHT = 400;
	
	public NodeEditor(String title, List<String> attributes){
		this(title, attributes, null);
	}
	
	public NodeEditor(String title, List<String> attributes, List<String> values){
		if(title == null || attributes == null){
			throw new IllegalArgumentException("Parameters title and attributes should be provided.");
		}
		
		this.mainWindow = Main.getController().getMainWindow();
		
		List<JTextField> textFields = new ArrayList<JTextField>();
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(attributes.size(), 2, 3, 3));
		for(String attr : attributes){
			panel.add(new JLabel(attr));
			JTextField textField = new JTextField();
			panel.add(textField);
			textFields.add(textField);
		}
		
		if(values != null){
			if(attributes.size() != values.size()){
				throw new IllegalArgumentException("Attributes and values lists should have the same size.");
			}
			
			int i = 0;
			for(JTextField textField : textFields){
				textField.setText(values.get(i));
				i++;
			}
		}
		
		//set scrollbars and maximum size of panel
		JScrollPane scrollPane = new JScrollPane(panel, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(PANEL_WIDTH, 
				MAX_PANEL_HEIGHT < 33 * attributes.size() ? MAX_PANEL_HEIGHT : 33 * attributes.size()));
		
		this.select = JOptionPane.showConfirmDialog(this.mainWindow.getFrame(), scrollPane,
				title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
				new ImageIcon(this.getClass().getResource("/icons/add.png")));
		
		if (isOk()){
			this.values = new ArrayList<String>(attributes.size());
			for(JTextField textField : textFields){
				this.values.add(textField.getText().trim());
			}
		}
	}
	
	public List<String> getValues(){
		return values;
	}
	
	public boolean isOk(){
		return this.select == JOptionPane.OK_OPTION;
	}
	
	
	
}
