/*
 * Created on 24.11.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.view.mindmapview;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author Dimitri
 *	
 *	Singleton with static method scrollNodeToVisible(MapView map, NodeView node, int extraWidth),
 *	which is intended to be called from MapView.scrollNodeToVisible(MapView map, NodeView node, int extraWidth),
 *  if its argument node is still not valid. The delayed scrolling will be set 
 *  for this node.
 *   
 */
class DelayedScroller extends ComponentAdapter{
	/**
     * the only one interface method.
     * @param map
     * @param node
     * @param extraWidth
     */
    public static void scrollNodeToVisible(MapView map, NodeView node, int extraWidth){
		if (m_SingletonDelayedScroller == null) m_SingletonDelayedScroller = new DelayedScroller();
		m_SingletonDelayedScroller.Set(map, node, extraWidth);			
	}
	private DelayedScroller() {}

	public void componentMoved(ComponentEvent e){
		e.getComponent().removeComponentListener(this);
		m_map.scrollNodeToVisible(m_node, m_extraWidth);
		m_map = null;
		m_node = null;		
	}		

	private void Set(MapView map, NodeView node, int extraWidth){
		if (node != null) 
			node.removeComponentListener(this);
		m_map = map;
		m_node = node;
		m_extraWidth = extraWidth;
		node.addComponentListener(this);
	}
	MapView m_map = null;
	NodeView m_node = null;
	int m_extraWidth = 0;
	private static DelayedScroller m_SingletonDelayedScroller = null;
}