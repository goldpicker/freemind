/*
 * Created on 07.04.2005
 */
package freemind.view.mindmapview;

import java.awt.Dimension;
import java.awt.Point;

import freemind.modes.MindMapNode;

/**
 * @author Dimitri
 */
public abstract class MoveableNodeView extends NodeView {

	private final int LISTENER_VIEW_WIDTH = 10; 
	private NodeMotionListenerView motionListenerView;
	
	protected MoveableNodeView(MindMapNode model, MapView map) {
		super(model, map);
		motionListenerView = new NodeMotionListenerView(this);
	}
	public NodeMotionListenerView getMotionListenerView() {
		return motionListenerView;
	}

	public void setLocation(int x, int y) {
		super.setLocation(x, y);
		motionListenerView.setLocation(x-LISTENER_VIEW_WIDTH, y);
	}
	public void setLocation(Point p) {
		throw new AssertionError("Do not use this method");
	}
	public void setSize(int w, int h) {
		super.setSize(w, h);
		motionListenerView.setSize(LISTENER_VIEW_WIDTH, h);
	}
}
