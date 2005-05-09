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
*	Create al the buttons used in the Browser
*/
class visorFreeMind.ButtonsCreator{
	//Buttons
	private var resizer;
	private var bBack;
	private var bForward;
	private var bGrow;
	private var bShrink;
	private var bReset;
	private var bShadow;
	private var bInfo;
	private var browser:Browser;

	public function ButtonsCreator(browser:Browser){
		this.browser=browser;
		createSizeButtons(browser.mc_container);
		createNavigationButtons(browser.mc_container);
		relocateAllButtons();
		addToolTipsButtons();
		Logger.trace("ButtonsCreator created");
	}

	function addToolTipsButtons(){
		var over=function(){
			this.browser.showTooltip(this.tooltip);
		}

		var out=function(){
			this.browser.hideTooltip();
		}
		var overInfo=function(){
			this.browser.showInfo(this.info_text);
		}

		var outInfo=function(){
			this.browser.hideInfo();
		}
		bBack.onRollOver=over;
		bBack.onRollOut=out;
		bForward.onRollOver=over;
		bForward.onRollOut=out;
		bGrow.onRollOver=over;
		bGrow.onRollOut=out;
		bShrink.onRollOver=over;
		bShrink.onRollOut=out;
		bReset.onRollOver=over;
		bReset.onRollOut=out;
		bShadow.onRollOver=over;
		bShadow.onRollOut=out;
		bInfo.onRollOver=overInfo;
		bInfo.onRollOut=outInfo;
	}

	// For resize of Stage
	function relocateAllButtons(){
		var newCenter=Stage.width/2;
		bBack._x=newCenter-60;
		bBack._y=10;
		bForward._x=newCenter-40;
		bForward._y=10;
		bGrow._x=newCenter-20;
		bGrow._y=10;
		bShrink._x=newCenter;
		bShrink._y=10;
		bReset._x=newCenter+20;
		bReset._y=10;
		bShadow._x=newCenter+40;
		bShadow._y=10;
		bInfo._x=newCenter+60;
		bInfo._y=10;
	}

	function createNavigationButtons(mc_container){
		bBack=mc_container.createEmptyMovieClip("navBack",7778);
		bBack.browser=browser;
		bBack.tooltip="BACK";

		bForward=mc_container.createEmptyMovieClip("navForward",7779);
		bForward.browser=browser;
		bForward.tooltip="FORWARD";

		bBack.lineStyle(16,0x9999ff,90);
		bBack.moveTo(0,0);
		bBack.lineTo(1,0);
		bBack.lineStyle(3,0xFFFFFF,90);
		bBack.moveTo(3,-4);
		bBack.lineTo(-4,0);
		bBack.lineTo(3,4);
		bForward.lineStyle(16,0x9999ff,90);
		bForward.moveTo(0,0);
		bForward.lineTo(1,0);
		bForward.lineStyle(3,0xFFFFFF,90);
		bForward.moveTo(-3,-4);
		bForward.lineTo(4,0);
		bForward.lineTo(-3,4);
		bBack.onPress=function(){
			var v=this.browser;
			if(v.posXmls>0){
				v.posXmls--;
				v.fileName=v.visitedMM[v.posXmls];
				v.genMindMap(3);
			}
		}
		bForward.onPress=function(){
			var v=this.browser;
			if(v.posXmls<(v.visitedMM.length-1)){
			v.posXmls++;
			v.fileName=v.visitedMM[v.posXmls];
			v.genMindMap(3);
			}
		}
	}

	function createSizeButtons(mc_container){
		bGrow=mc_container.createEmptyMovieClip("navBack",7788);
		bGrow.browser=browser;
		bGrow.tooltip="INCREASE";
		bShrink=mc_container.createEmptyMovieClip("navForward",7789);
		bShrink.browser=browser;
		bShrink.tooltip="SHRINK";
		bReset=mc_container.createEmptyMovieClip("reset",7790);
		bReset.browser=browser;
		bReset.tooltip="RESET";
		bShadow=mc_container.createEmptyMovieClip("shadow",7791);
		bShadow.browser=browser;
		bShadow.tooltip="SHADOW ON";
		bInfo=mc_container.createEmptyMovieClip("bInfo",7792);
		bInfo.browser=browser;
		bInfo.info_text="hola desde Flash FreeMind Browser";

		bGrow.lineStyle(16,0x9999ff,90);
		bGrow.moveTo(0,0);
		bGrow.lineTo(1,0);
		bGrow.lineStyle(3,0xFFFFFF,90);
		bGrow.moveTo(0,-4);
		bGrow.lineTo(0,4);
		bGrow.moveTo(-4,0);
		bGrow.lineTo(4,0);
		bShrink.lineStyle(16,0x9999ff,90);
		bShrink.moveTo(0,0);
		bShrink.lineTo(1,0);
		bShrink.lineStyle(3,0xFFFFFF,90);
		bShrink.moveTo(-4,0);
		bShrink.lineTo(4,0);

		bReset.lineStyle(16,0x9999ff,90);
		bReset.moveTo(0,0);
		bReset.lineTo(1,0);
		bReset.lineStyle(8,0xFFFFFF,90);
		bReset.moveTo(0,0);
		bReset.lineTo(1,0);

		bShadow.lineStyle(16,0x9999ff,90);
		bShadow.moveTo(0,0);
		bShadow.lineTo(1,0);
		bShadow.lineStyle(14,0xFFFFFF,90);
		bShadow.moveTo(0,0);
		bShadow.lineTo(1,0);

		bInfo.lineStyle(16,0x9999ff,90);
		bInfo.moveTo(0,0);
		bInfo.lineTo(1,0);
		bInfo.lineStyle(3,0xFFFFFF,90);
		bInfo.moveTo(0,-4);
		bInfo.lineTo(0,-3.8);
		bInfo.moveTo(0,0);
		bInfo.lineTo(0,5);

		bGrow.onPress=function(){
			this.browser.mc_floor._xscale+=20;
			this.browser.mc_floor._yscale+=20;
			//this.browser.genMindMap(3);
		}
		bShrink.onPress=function(){
			this.browser.mc_floor._xscale-=20;
			this.browser.mc_floor._yscale-=20;
			//this.browser.genMindMap(3);
		}
		bReset.onPress=function(){
			this.browser.mc_floor._xscale=100;
			this.browser.mc_floor._yscale=100;
			//Center View
			this.browser.initialization=true;
			//this.browser.relocateFloor();
			this.browser.relocateMindMap();
		}

		bShadow.onPress=function(){
			this.clear();
			this.lineStyle(16,0x9999ff,90);
			this.moveTo(0,0);
			this.lineTo(1,0);
			this.lineStyle(14,0xFFFFFF,90);
			this.moveTo(0,0);
			this.lineTo(1,0);
			this.browser.withShadow= this.browser.withShadow?false:true;
			if(this.browser.withShadow==true){
				this.lineStyle(10,0xFF0000,90);
				this.moveTo(0,0);
				this.lineTo(1,0);
				this.tooltip="SHADOW OFF";
			}else{
				this.tooltip="SHADOW ON";
			}
			this.browser.genMindMap(3);;
		}
	}
}
