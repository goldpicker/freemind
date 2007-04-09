/*
 * Created on 06.04.2007
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.view.mindmapview;

import freemind.modes.EdgeAdapter;

class EdgeViewFactory {

    private static EdgeViewFactory factory;
    private EdgeView sharpBezierEdgeView;
    private EdgeView sharpLinearEdgeView;
    private EdgeView bezierEdgeView;
    private EdgeView linearEdgeView;

    //Singleton
    private EdgeViewFactory(){
        
    }
    
    
    static EdgeViewFactory getInstance(){
        if(factory == null){
            factory = new EdgeViewFactory();
        }
        return factory;
    }
    
    EdgeView getEdge(NodeView newView) {
        if (newView.getModel().getEdge().getStyle().equals(EdgeAdapter.EDGESTYLE_LINEAR)) {
            return getLinearEdgeView();
        } else if (newView.getModel().getEdge().getStyle().equals(EdgeAdapter.EDGESTYLE_BEZIER)) {
            return getBezierEdgeView();
        } else if (newView.getModel().getEdge().getStyle().equals(EdgeAdapter.EDGESTYLE_SHARP_LINEAR)) {
            return getSharpEdgeView();
        } else if (newView.getModel().getEdge().getStyle().equals(EdgeAdapter.EDGESTYLE_SHARP_BEZIER)) {
            return getSharpBezierEdgeView();
        } else {
            System.err.println("Unknown Edge Type.");
            return getLinearEdgeView();
        }
    }

    private EdgeView getSharpBezierEdgeView() {
        if(sharpBezierEdgeView == null){
            sharpBezierEdgeView = new SharpBezierEdgeView();
        }
        return sharpBezierEdgeView;
    }

    private EdgeView getSharpEdgeView() {
        if(sharpLinearEdgeView == null){
            sharpLinearEdgeView = new SharpLinearEdgeView();
        }
        return sharpLinearEdgeView;
    }

    private EdgeView getBezierEdgeView() {
        if(bezierEdgeView == null){
            bezierEdgeView = new BezierEdgeView();
        }
        return bezierEdgeView;
    }

    private EdgeView getLinearEdgeView() {
        if(linearEdgeView == null){
            linearEdgeView = new LinearEdgeView();
        }
        return linearEdgeView;
    }
}
