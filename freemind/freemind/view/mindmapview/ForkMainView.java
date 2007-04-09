/*
 * Created on 22.03.2007
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.view.mindmapview;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import freemind.modes.MindMapNode;

class ForkMainView extends MainView{
    public void paint(Graphics graphics) {
        Graphics2D g = (Graphics2D)graphics;
        
        final NodeView nodeView = getNodeView();
        final MindMapNode model = nodeView.getModel();
        if (model==null) return;
        
        paintSelected(g);
        paintDragOver(g);
        
        int edgeWidth = model.getEdge().getRealWidth();
        
        //Draw a standard node
        nodeView.setRendering(g);
        g.setColor(model.getEdge().getColor());
        g.setStroke(model.getEdge().getStroke());
        g.drawLine(0,          
                getHeight()-edgeWidth/2-1,
                getWidth(), 
                getHeight()-edgeWidth/2-1);
        super.paint(g);
    }

    void paintFoldingMark(Graphics2D g, Point p) {
        final int zoomedFoldingSymbolHalfWidth = getZoomedFoldingSymbolHalfWidth();
        if(p.x == -1){
            p.x -= zoomedFoldingSymbolHalfWidth;
        }
        else if(p.x == getWidth()){
            p.x += zoomedFoldingSymbolHalfWidth;
        }        
        super.paintFoldingMark(g, p);
    }
    
    protected int getMainViewWidthWithFoldingMark( )
    {
        int width = getWidth();
        if(getNodeView().getModel().isFolded()){
            width += getZoomedFoldingSymbolHalfWidth() * 2 + getZoomedFoldingSymbolHalfWidth();
        }
        return width;
    }

    protected int getMainViewHeightWithFoldingMark()
    {
        int height = getHeight();
        if(getNodeView().getModel().isFolded()){
            height += getZoomedFoldingSymbolHalfWidth();
        }
        return height;
    }

    public int getDeltaX(){
        if(getNodeView().getModel().isFolded() && getNodeView().isLeft()){
            return super.getDeltaX()+ getZoomedFoldingSymbolHalfWidth() * 3;
        }
        return super.getDeltaX();
    }
    
    /* (non-Javadoc)
     * @see freemind.view.mindmapview.NodeView#getStyle()
     */
    String getStyle() {
        return MindMapNode.STYLE_FORK;
    }
    /**
     * Returns the relative position of the Edge
     */
    int getAlignment() {
        return NodeView.ALIGN_BOTTOM;
    }

    Point getCenterPoint() {
        int edgeWidth = getNodeView().getModel().getEdge().getRealWidth();
        Point in= new Point(getWidth() / 2, getHeight() - edgeWidth/2 - 1);
        return in;
    }
    
}