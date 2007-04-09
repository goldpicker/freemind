/*
 * Created on 22.03.2007
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.view.mindmapview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;

import freemind.modes.MindMapNode;

class BubbleMainView extends MainView{
    private final static Stroke BOLD_STROKE =
        new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    1f, new float[] {2f, 2f}, 0f);  
    private final static Stroke DEF_STROKE = new BasicStroke();
    

    /* (non-Javadoc)
     * @see freemind.view.mindmapview.NodeView.MainView#getPreferredSize()
     */
    public Dimension getPreferredSize() {
        Dimension prefSize = super.getPreferredSize();
        prefSize.width  += getNodeView().getMap().getZoomed(5);
        return prefSize;
    }
    
    public void paint(Graphics graphics) {
        Graphics2D g = (Graphics2D)graphics;
        final NodeView nodeView = getNodeView();
        final MindMapNode model = nodeView.getModel();
        if (model==null) return;

            paintSelected(g);
            paintDragOver(g);

        // change to bold stroke
        //g.setStroke(BOLD_STROKE);                     // Changed by Daniel

            nodeView.setRendering(g);

        //Draw a standard node
        g.setColor(model.getEdge().getColor());
        //g.drawOval(0,0,size.width-1,size.height-1);   // Changed by Daniel

            if (nodeView.getMap().getController().getAntialiasEdges()) {
               g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); }
            g.drawRoundRect(0,0, getWidth()-1, getHeight()-1,10,10);
            // this disables the font antialias if only AntialiasEdges is requested.
            if (nodeView.getMap().getController().getAntialiasEdges()) {
               g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF); }

        // return to std stroke
        g.setStroke(DEF_STROKE);

        super.paint(g);
        }

        public void paintSelected(Graphics2D graphics) {
            super.paintSelected(graphics);
            if (getNodeView().isSelected()) {
                graphics.setColor(NodeView.standardSelectColor);
                graphics.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            }
        }
        
        protected void paintBackground(
            Graphics2D graphics,
            Color color) {
                graphics.setColor(color);
                graphics.fillRoundRect(0,0, getWidth()-1, getHeight()-1,10,10);
        }

        Point getCenterPoint() {
            Point in= new Point(getWidth() / 2, getHeight() / 2);
            return in;
        }

        protected int getMainViewWidthWithFoldingMark()
        {
            int width = getWidth();
            int dW = getZoomedFoldingSymbolHalfWidth() * 2;
            if(getNodeView().getModel().isFolded()){
                width += dW;
            }
            return width + dW;
        }
      
        public int getDeltaX()
        {
            if(getNodeView().getModel().isFolded() && getNodeView().isLeft()){
                    return super.getDeltaX()+getZoomedFoldingSymbolHalfWidth() * 2;
            }
            return super.getDeltaX();
        }
      
        /* (non-Javadoc)
         * @see freemind.view.mindmapview.NodeView#getStyle()
         */
        String getStyle() {
            return MindMapNode.STYLE_BUBBLE;
        }

        /**
         * Returns the relative position of the Edge
         */
        int getAlignment() {
            return NodeView.ALIGN_CENTER;
        }
     
        /* (non-Javadoc)
         * @see freemind.view.mindmapview.NodeView#getTextWidth()
         */
        public int getTextWidth() {
            return super.getTextWidth() + getNodeView().getMap().getZoomed(5);
        }


        /* (non-Javadoc)
         * @see freemind.view.mindmapview.NodeView#getTextX()
         */
        public int getTextX() {
            return super.getTextX() + getNodeView().getMap().getZoomed(2);
        }


}