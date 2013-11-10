package net.rpeti.clusterdemo.gui.visualization;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;

public class ContextMenuPlugin<V,E> extends AbstractPopupGraphMousePlugin {
	
	private VisualizationViewer<V,E> canvas;
	private JPopupMenu menu;
	
	public ContextMenuPlugin(VisualizationViewer<V,E> canvas){
		super(MouseEvent.BUTTON3_MASK);
		menu = new JPopupMenu();
		//TODO icons and identification for updateVertex
		menu.add(new JMenuItem("Add"));
		menu.addSeparator();
		menu.add(new JMenuItem("Delete"));
		menu.add(new JMenuItem("Edit..."));
		this.canvas = canvas;
	}

	@Override
	protected void handlePopup(MouseEvent e) {
		
		Point2D p = e.getPoint();
		
		GraphElementAccessor<V,E> pickSupport = canvas.getPickSupport();
        if(pickSupport != null) {
            final V v = pickSupport.getVertex(canvas.getGraphLayout(), p.getX(), p.getY());
            if(v != null) {
                updateVertexMenu(v);
                menu.show(canvas, e.getX(), e.getY());
            }
        }
	}
	
	private void updateVertexMenu(V v) {
        if (menu == null) return;
        Component[] menuComps = menu.getComponents();
        for (Component comp: menuComps) {
            //TODO
        }
        
    }
	
	/**
     * Getter for the menu.
     */
    public JPopupMenu getVertexPopup() {
        return menu;
    }
    
    /**
     * Setter for the menu. 
     */
    public void setVertexPopup(JPopupMenu menu) {
        this.menu = menu;
    }

}
