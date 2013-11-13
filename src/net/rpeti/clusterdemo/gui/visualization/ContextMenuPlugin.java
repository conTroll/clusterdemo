package net.rpeti.clusterdemo.gui.visualization;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.rpeti.clusterdemo.Main;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;

public class ContextMenuPlugin extends AbstractPopupGraphMousePlugin {

	private VisualizationViewer<Integer,Integer> canvas;
	private JPopupMenu menu;
	private JMenuItem addMenuItem, deleteMenuItem, editMenuItem;
	private Integer selectedNode;

	public ContextMenuPlugin(VisualizationViewer<Integer,Integer> canvas) {
		super(MouseEvent.BUTTON3_MASK);
		menu = new JPopupMenu();
		Image addImage;
		try {
			addImage = ImageIO.read(this.getClass().getResource("/icons/add.png"));
			Image deleteImage = ImageIO.read(this.getClass().getResource("/icons/delete.png"));
			Image editImage = ImageIO.read(this.getClass().getResource("/icons/edit.png"));
			addMenuItem = new JMenuItem("Add...", new ImageIcon(addImage.getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
			deleteMenuItem = new JMenuItem("Delete",  new ImageIcon(deleteImage.getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
			editMenuItem = new JMenuItem("Edit...",  new ImageIcon(editImage.getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
		} catch (IOException e1) {
			Main.getController().getMainWindow().showUnhandledException(e1);
		}

		addMenuItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Main.getController().addNode();
			}

		});

		deleteMenuItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Main.getController().deleteNode(selectedNode);
			}

		});

		editMenuItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Main.getController().editNode(selectedNode);
			}

		});

		menu.add(addMenuItem);
		menu.addSeparator();
		menu.add(deleteMenuItem);
		menu.add(editMenuItem);
		deleteMenuItem.setVisible(false);
		editMenuItem.setVisible(false);
		this.canvas = canvas;
	}

	@Override
	protected void handlePopup(MouseEvent e) {

		Point2D p = e.getPoint();

		GraphElementAccessor<Integer,Integer> pickSupport = canvas.getPickSupport();
		if(pickSupport != null) {
			Integer v = pickSupport.getVertex(canvas.getGraphLayout(), p.getX(), p.getY());
			updateVertexMenu(v);
			menu.show(canvas, e.getX(), e.getY());
		}
	}

	private void updateVertexMenu(Integer v) {
		if(menu == null) return;
		if(v == null){
			editMenuItem.setVisible(false);
			deleteMenuItem.setVisible(false);
		}
		else{
			selectedNode = v;
			editMenuItem.setVisible(true);
			deleteMenuItem.setVisible(true);
		}
	}
}
