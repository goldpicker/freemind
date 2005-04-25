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
/*$Id: KeyControler.as,v 1.1 2005-04-25 20:32:23 christianfoltin Exp $*/
//  Take into account all the KEY events.
class KeyControler{

	private var browser:Browser;

	//All the key  inputs are controles from here
	function KeyControler(browser:Browser){
		this.browser=browser;
		Key.addListener(this);
	}

	public function onKeyUp(){
			if(!Key.isDown(Key.SHIFT) && Node.currentOver!=null){
				browser.text_selectable.selectable=false;
				browser.floor.makeDraggable();
			}
	}

	public function onKeyDown(){
			Logger.trace("key:"+Key.getAscii());
			Logger.trace("code:"+Key.getCode());
			if(Key.isDown(Key.CONTROL) && Key.isDown(Key.SHIFT) && Key.isDown(Key.DOWN)){
				this.browser.mc_floor._xscale+=20;
				this.browser.mc_floor._yscale+=20;
			}else if(Key.isDown(Key.CONTROL) && Key.isDown(Key.SHIFT) && Key.isDown(Key.UP)){
				this.browser.mc_floor._xscale-=20;
				this.browser.mc_floor._yscale-=20;
			}else if(Key.isDown(Key.CONTROL) && Key.isDown(Key.SHIFT) && Key.isDown(Key.DOWN)){
				this.browser.mc_floor._xscale-=20;
				this.browser.mc_floor._yscale-=20;
			}else if(Key.isDown(Key.CONTROL) && Key.isDown(Key.SHIFT) && Key.isDown(Key.DOWN)){
				this.browser.mc_floor._xscale-=20;
				this.browser.mc_floor._yscale-=20;
			}
			/// HISTORY
			else if(Key.isDown(Key.CONTROL) && Key.isDown(Key.LEFT) && browser.posXmls>0){
				browser.posXmls--;
				browser.fileName=browser.visitedMM[browser.posXmls];
				browser.genMindMap(3);
			}else if(Key.isDown(Key.CONTROL) && Key.isDown(Key.RIGHT)){
				if(browser.posXmls<(browser.visitedMM.length-1)){
				browser.posXmls++;
				browser.fileName=browser.visitedMM[browser.posXmls];
				browser.genMindMap(3);
				}
			}else if(Key.isDown(Key.SHIFT) && Node.currentOver!=null){
				Node.currentOver.ref_mc.node_txt.node_txt.selectable=true;
				browser.text_selectable=Node.currentOver.ref_mc.node_txt.node_txt;
				browser.floor.notDraggable();
			}
		}
}
