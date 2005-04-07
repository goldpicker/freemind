/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
/*$Id: NodeMotionListener.java,v 1.1.2.1 2005-04-07 20:51:41 dpolivaev Exp $*/

package freemind.controller;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Timer;
import java.util.TimerTask;

import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.view.mindmapview.NodeMotionListenerView;
import freemind.view.mindmapview.NodeView;
import freemind.view.mindmapview.RootNodeView;

/**
 * The MouseMotionListener which belongs to every
 * NodeView
 */
public class NodeMotionListener extends MouseAdapter implements MouseMotionListener, MouseListener {

    private final Controller c;
    // Logging:
    private static java.util.logging.Logger logger;
    
    public NodeMotionListener(Controller controller) {
       c = controller;
       if(logger == null)
           logger = c.getFrame().getLogger(this.getClass().getName());
    }

    public void mouseMoved(MouseEvent e) {
    }
    Point dragStartingPoint = null;
        /** Invoked when a mouse button is pressed on a component and then dragged.  */
	    public void mouseDragged(MouseEvent e) {
	        logger.fine("Event: mouseDragged");
	        NodeView nodeV = ((NodeMotionListenerView)e.getSource()).getMovedView();

	        if ((e.getModifiersEx() & (InputEvent.BUTTON1_DOWN_MASK | InputEvent.CTRL_DOWN_MASK))
	         		 == (InputEvent.BUTTON1_DOWN_MASK)
					 ) {
		        if (!isActive()){
		   	      dragStartingPoint = e.getPoint();
		        }
		        else if(isActive()){
		        	Point dragNextPoint = e.getPoint();
		        	MindMapNode node = nodeV.getModel();
		        	int shiftYChange = (int)((dragNextPoint.y - dragStartingPoint.y) / c.getView().getZoom());
		        	node.setShiftY(node.getShiftY() + shiftYChange);
		        	int hGapChange = (int)((dragNextPoint.x - dragStartingPoint.x) / c.getView().getZoom());
		        	if (nodeV.isLeft()) hGapChange = -hGapChange;
		        	int oldHGap = node.getHGap();
		        	node.setHGap(oldHGap + hGapChange);

		        	// Bad hack for keeping root node unmoved
		        	nodeV.setLocation(0, 0);

		        	c.getModel().nodeChanged(node);
		        }
	   	      	return;
	         }
	        if ( (e.getModifiersEx() & (InputEvent.BUTTON1_DOWN_MASK | InputEvent.CTRL_DOWN_MASK))
	         		 == (InputEvent.BUTTON1_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)) {
		        if (!isActive()){
		   	      dragStartingPoint = e.getPoint();
		        }
		        else if (isActive()){
		        	Point dragNextPoint = e.getPoint();
		        	MindMapNode node = nodeV.getModel();
		        	int vGapChange = (int)((dragNextPoint.y - dragStartingPoint.y) / c.getView().getZoom());
		        	int oldVGap = node.calcVGap();
		        	node.setVGap(Math.max(0, oldVGap - vGapChange));
		        	dragStartingPoint = dragNextPoint;
		        	c.getModel().nodeChanged(node);
		        }
	   	      	return;
	         }
	    }

    public void mouseClicked(MouseEvent e) {
        if ( e.getButton() == 1 && e.getClickCount() == 2){
        	if(e.getModifiersEx() == 0) {
	            NodeView nodeV = ((NodeMotionListenerView)e.getSource()).getMovedView();
	        	MindMapNode node = nodeV.getModel();
	        	node.setShiftY(0);
	        	node.setHGap(0);
	        	nodeV.setLocation(0, 0);
	        	c.getModel().nodeChanged(node);
	        	return;
        	}
        	if(e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK) {
	            NodeView nodeV = ((NodeMotionListenerView)e.getSource()).getMovedView();
	        	MindMapNode node = nodeV.getModel();
	        	node.setVGap(MindMapNode.AUTO);
	        	c.getModel().nodeChanged(node);
	        	return;
        	}
        }
    }

	public void mouseEntered(MouseEvent e) {
        logger.fine("Event: mouseEntered");
        if (! isActive())
        {
        	NodeMotionListenerView v = (NodeMotionListenerView)e.getSource();
        	v.setMouseEntered();
        }
	}
	
	public void mouseExited(MouseEvent e) {
        logger.fine("Event: mouseExited");
        if (! isActive())
        {
        	NodeMotionListenerView v = (NodeMotionListenerView)e.getSource();
        	v.setMouseExited();
        }
	}
	
    public void mouseReleased( MouseEvent e ) {
        logger.fine("Event: mouseReleased");
		NodeMotionListenerView v = (NodeMotionListenerView)e.getSource();
		if (! v.contains(e.getX(), e.getY()))
        	v.setMouseExited();			
         dragStartingPoint = null;
    }

    public boolean isActive(){return dragStartingPoint != null;}

}


