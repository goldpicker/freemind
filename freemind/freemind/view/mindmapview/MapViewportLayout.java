package freemind.view.mindmapview;


import java.awt.AWTError;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.ViewportLayout;

import freemind.main.Tools;

/**
 * The default layout manager for <code>JViewport</code>. 
 * <code>ViewportLayout</code> defines
 * a policy for layout that should be useful for most applications.
 * The viewport makes its view the same size as the viewport,
 * however it will not make the view smaller than its minimum size.
 * As the viewport grows the view is kept bottom justified until
 * the entire view is visible, subsequently the view is kept top
 * justified.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @version 1.36 01/23/03
 * @author Hans Muller
 */
public class MapViewportLayout extends ViewportLayout
{
    /**
     * Called by the AWT when the specified container needs to be laid out.
     *
     * @param parent  the container to lay out
     *
     * @exception AWTError  if the target isn't the container specified to the
     *                      <code>BoxLayout</code> constructor
     */
    public void layoutContainer(Container parent)
    {        
        JViewport vp = (JViewport)parent;
        Component view = vp.getView();
        if(! (view instanceof MapView)){
            super.layoutContainer(parent);
            return;
        }
        
        MapView mapView = (MapView) view;
        Dimension viewPrefSize = mapView.getPreferredSize();
        vp.setViewSize(viewPrefSize);
        
        Point viewPosition = vp.getViewPosition();    
        Point oldRootContentLocation = mapView.getRootContentLocation();
        final NodeView root = mapView.getRoot();
        Point rootContentLocation = root.getContent().getLocation();
        SwingUtilities.convertPointToScreen(rootContentLocation, root);
        
        final int deltaX = rootContentLocation.x - oldRootContentLocation.x ;
        final int deltaY = rootContentLocation.y - oldRootContentLocation.y;
        if(deltaX != 0 || deltaY != 0)
        {
            viewPosition.x += deltaX;
            viewPosition.y += deltaY;
            final int scrollMode = vp.getScrollMode();
            //avoid immediate scrolling here:
            vp.setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
            vp.setViewPosition(viewPosition);
            vp.setScrollMode(scrollMode);
        }
        else
        {
            vp.repaint();
        }
    }
}

