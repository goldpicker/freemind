/*
 * Created on 30.03.2007
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.view.mindmapview;

import java.awt.LayoutManager;
import java.awt.Point;

public interface NodeViewLayout extends LayoutManager {
    void layoutNodeMotionListenerView(NodeMotionListenerView view);

    Point getOutPoint(NodeView view, Point destinationPoint);

    Point getInPoint(NodeView view);
}
