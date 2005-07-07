/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
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
 *
 * Created on 25.04.2005
 */
import visorFreeMind.*;
/**
* Edge are required for drawing the "edges" between Nodes.
* They have their own movieclip
*/
class visorFreeMind.Edge {
	public static var num:Number=1000; // counter of edges
	private var nombre:String; // Not used
	private var ref_mc:MovieClip;
	private var gap:Number=0; //linked with size of Nodes
	private var _orig:Node;
	private var _dest:Node;

	function Edge(orig:Node,dest:Node,nom:String,mc:MovieClip){
		nombre=nom;
		_orig=orig;
		_dest=dest;
		num++;
		ref_mc=mc.createEmptyMovieClip("link"+num,num);
		// add to nodes origin and dest
		_orig.addEdge(this);
		_dest.addEdge(this);
	}


	private function drawEdge(or_x,or_y,ddx,ddy,color,alpha,colorOrig){
		var thickness=_dest.lineWidth;
		var h_thickness=_dest.lineWidth*0.5;
		//0=bezier, 1=linear,2=sharp_bezier,3=sharp_linear;4=rectangular
		switch(_dest.styleLine){
		case 0:
			ref_mc.lineStyle(thickness,color,alpha);
			ref_mc.moveTo(or_x,or_y);
			ref_mc.curveTo(ddx*0.3,0,ddx*0.5,ddy*0.5);
			ref_mc.curveTo(ddx*0.7,ddy,ddx,ddy);
			break;
		case 1:
			ref_mc.lineStyle(thickness,color,alpha);
			ref_mc.moveTo(or_x,or_y);
			ref_mc.lineTo(ddx,ddy);
			//ref_mc.dashTo(or_x,or_y, ddx,ddy, 1, 2,thickness);
			break;
		case 2:
			ref_mc.beginFill(_dest.cf,alpha);
			ref_mc.lineStyle(0,color,alpha);
			ref_mc.moveTo(or_x,or_y+h_thickness);
			ref_mc.curveTo(ddx*0.3,h_thickness,ddx*0.5,(ddy+h_thickness)*0.5);
			ref_mc.curveTo(ddx*0.7,ddy,ddx,ddy);
			ref_mc.curveTo(ddx*0.7,ddy,ddx*0.5,(ddy-h_thickness)*0.5);
			ref_mc.curveTo(ddx*0.3,-h_thickness,0,-h_thickness);
			ref_mc.lineTo(or_x,or_y+h_thickness);
			ref_mc.endFill();
			break;
		case 3:
			ref_mc.beginFill(color,alpha);
			ref_mc.lineStyle(or_x,color,alpha);
			ref_mc.moveTo(or_x,or_y+h_thickness);
			ref_mc.lineTo(ddx,ddy);
			ref_mc.lineTo(or_x,or_y-h_thickness);
			ref_mc.lineTo(or_x,or_y+h_thickness);
			ref_mc.endFill();
			break;
		case 4:
			//ref_mc.lineStyle(_orig.lineWidth,colorOrig,alpha);
			ref_mc.lineStyle(thickness,color,alpha);
			ref_mc.moveTo(or_x,or_y);
			ref_mc.lineTo(or_x+10*Math.abs(ddx)/ddx,or_y);
			ref_mc.lineTo(or_x+10*Math.abs(ddx)/ddx,ddy);
			ref_mc.lineTo(ddx,ddy);
			break;
		}
	}

	public function draw(){
		ref_mc.clear();
		var ddx,ddy;
		var destThickness=0;
		var origThickness=0;
		if(_orig.ref_mc._x < _dest.ref_mc._x){ // RIGHT
			ref_mc._x=_orig.ref_mc._x+_orig.ref_mc.node_txt._width+((_orig.style==1)?0:(_orig.ref_mc.box_txt._width-_orig.ref_mc.node_txt._width)/2);
			ref_mc._y=_orig.ref_mc._y+((_orig.style==1)? _orig.ref_mc.node_txt._height -origThickness: _orig.ref_mc.node_txt._height/2);
			ddx=_dest.ref_mc._x-((_dest.style==1)?0:(_dest.ref_mc.box_txt._width-_dest.ref_mc.node_txt._width)/2)- ref_mc._x;
			 ddy=_dest.ref_mc._y -  ref_mc._y +((_dest.style==1)? _dest.ref_mc.node_txt._height -destThickness: _dest.ref_mc.node_txt._height/2);

			drawEdge(0,0,ddx,ddy,_dest.cf,100,_orig.cf);
		}else { // LEFT
			ref_mc._x=_orig.ref_mc._x-((_orig.style==1)?0:(_orig.ref_mc.box_txt._width-_orig.ref_mc.node_txt._width)/2);
			ref_mc._y=_orig.ref_mc._y+((_orig.style==1)? _orig.ref_mc.node_txt._height -origThickness: _orig.ref_mc.node_txt._height/2);
			ddx=_dest.ref_mc._x+_dest.ref_mc.node_txt._width+((_dest.style==1)?0:(_dest.ref_mc.box_txt._width-_dest.ref_mc.node_txt._width)/2)- ref_mc._x;
			ddy=_dest.ref_mc._y -  ref_mc._y +((_dest.style==1)? _dest.ref_mc.node_txt._height -destThickness: _dest.ref_mc.node_txt._height/2);

			drawEdge(0,0,ddx,ddy,_dest.cf,100,_orig.cf);
		}

	}
}
