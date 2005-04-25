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
/*$Id: Floor.as,v 1.1 2005-04-25 20:32:22 christianfoltin Exp $*/
//Container of the Browser.
class  Floor {

	private var mc_cont:MovieClip;
	private static var mc_floor:MovieClip=null;
	private static var counter:Number=4;

	function Floor(mc:MovieClip){
		mc_cont=mc; //father
		mc_floor=mc_cont.createEmptyMovieClip("floor",Floor.counter++);
		makeDraggable();
	}

	function makeDraggable(){
		mc_floor.onMouseDown=function(){
			this.startDrag();
		}

		mc_floor.onMouseUp=function(){
			this.stopDrag();
		}
	}

	function notDraggable(){
		mc_floor.onMouseDown=undefined;
		mc_floor.onMouseUp=undefined;
	}

	function getCanvas(){
		return mc_floor;
	}


	function clear(){
		mc_floor.clear();
	}

	function changeFloorColor(newColor){
		//Set colorBackground to new Color
	}
}
