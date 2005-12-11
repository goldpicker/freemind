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
	private var bColor;
	private var mc_Color;
	private var mc_Color_rollout;
	private var mainColor;
	private var alfa=100;
	private var browser:Browser;
	public  static var colors=[0xFFFFFF,0xEEEEEE,0xDDDDDD,
							   0xEEFFFF,0xFFEEFF,0xFFFFEE,
								0xFFEEEE,0xEEFFEE,0xEEEEFF];

	public function ButtonsCreator(browser:Browser){
		this.browser=browser;
		resetMainColor();
		trace("ButtonsCreator created");
	}

	function resetMainColor(){
		var color=browser.floor.getBackgroundColor();
		var nRed = (color >> 16)-0x33;
		nRed=nRed>=0?nRed:0;
		var nGreen= ((color >> 8) & 0xff)-0x33;
		nGreen=nGreen>=0?nGreen:0;
		var nBlue= (color & 0xff)-0x33;
		nBlue=nBlue>=0?nBlue:0;
		this.mainColor=(nRed<<16 | nGreen<<8 |nBlue);
		//trace(color+"("+nRed+","+nGreen+","+nBlue+")->"+this.mainColor+"\n");
		createSizeButtons(browser.mc_container);
		createNavigationButtons(browser.mc_container);
		relocateAllButtons();
		addToolTipsButtons();
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
		bColor._x=newCenter+80;
		bColor._y=10;
		createColorSelector();
	}

	function createNavigationButtons(mc_container){
		bBack=mc_container.createEmptyMovieClip("navBack",7778);
		bBack.browser=browser;
		bBack.tooltip="BACK";

		bForward=mc_container.createEmptyMovieClip("navForward",7779);
		bForward.browser=browser;
		bForward.tooltip="FORWARD";

		bBack.lineStyle(16,mainColor,alfa);
		bBack.moveTo(0,0);
		bBack.lineTo(1,0);
		bBack.lineStyle(3,0xFFFFFF,90);
		bBack.moveTo(3,-4);
		bBack.lineTo(-4,0);
		bBack.lineTo(3,4);
		bForward.lineStyle(16,mainColor,alfa);
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

	function createColorSelector(){
		mc_Color=browser.mc_container.createEmptyMovieClip("colSel",8000);
		var mc_ColorCanvas=createCube(mc_Color,0,0,0xFFFFFF,12,38);
		mc_Color_rollout=createCube(browser.mc_container,0,0,0xEEEEEE,7793,80);
		for(var i=0;i<9;i++){
			createCube(mc_ColorCanvas,1+(i%3)*12,1+Math.floor(i/3)*12,colors[i],i,12);
		}
		mc_Color._x=bColor._x+0;
		mc_Color._y=bColor._y+0;
		mc_Color._visible=false;
		mc_Color_rollout._x=bColor._x-15;
		mc_Color_rollout._y=bColor._y-15;
		mc_Color_rollout._visible=false;
		mc_Color_rollout._alpha=0;

		mc_Color_rollout.mc_Color=mc_Color;
		bColor.mc_Color=mc_Color;
		bColor.mc_Color_rollout=mc_Color_rollout;
		mc_Color.browser=browser;
		mc_Color.bc=this;
		bColor.onRollOver=function(){
			this.mc_Color._visible=true;
			this.mc_Color_rollout._visible=true;
		}
		mc_Color_rollout.onRollOver=function(){
			this.mc_Color._visible=false;
			this._visible=false;
		}

		mc_Color.onPress=function(){
			//Calculamos el punto de hit
			var x=_root._xmouse-this._x-1;
			var y=_root._ymouse-this._y-1;
			var i=Math.floor(x/12)+Math.floor(y/12)*3;
			//trace("x:"+x+" y:"+y +" i: "+i);
			if(i>=0 && i<9){
				this.browser.floor.changeBgColor(ButtonsCreator.colors[i]);
				this.bc.resetMainColor();
			}
			this._visible=false;
		}

		
		mc_ColorCanvas.dropShadow(8,4,4,0x777799,mc_Color);
	}


	function createCube(mc_container,posx,posy,color,deep,side){
		var cubo=mc_container.createEmptyMovieClip("color_"+deep,deep);
		cubo.lineStyle(1,color,100);
		cubo.beginFill(color,100);
		cubo.moveTo(0,0);
		cubo.lineTo(side,0);
		cubo.lineTo(side,side);
		cubo.lineTo(0,side);
		cubo.lineTo(0,0);
		cubo.endFill();
		cubo._x=posx;
		cubo._y=posy;
		return cubo;
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
		bShadow.bc=this;
		bShadow.browser=browser;
		bShadow.tooltip="SHADOW ON";
		bInfo=mc_container.createEmptyMovieClip("bInfo",7792);
		bInfo.browser=browser;
		bInfo.info_text="hola desde Flash FreeMind Browser";
		bColor=mc_container.createEmptyMovieClip("bColor",7794);
		bColor.browser=browser;
		bColor.info_text="change background color";

		bGrow.lineStyle(16,mainColor,alfa);
		bGrow.moveTo(0,0);
		bGrow.lineTo(1,0);
		bGrow.lineStyle(3,0xFFFFFF,90);
		bGrow.moveTo(0,-4);
		bGrow.lineTo(0,4);
		bGrow.moveTo(-4,0);
		bGrow.lineTo(4,0);
		bShrink.lineStyle(16,mainColor,alfa);
		bShrink.moveTo(0,0);
		bShrink.lineTo(1,0);
		bShrink.lineStyle(3,0xFFFFFF,90);
		bShrink.moveTo(-4,0);
		bShrink.lineTo(4,0);

		bReset.lineStyle(16,mainColor,alfa);
		bReset.moveTo(0,0);
		bReset.lineTo(1,0);
		bReset.lineStyle(8,0xFFFFFF,90);
		bReset.moveTo(0,0);
		bReset.lineTo(1,0);

		bShadow.lineStyle(16,mainColor,alfa);
		bShadow.moveTo(0,0);
		bShadow.lineTo(1,0);
		bShadow.lineStyle(14,0xFFFFFF,90);
		bShadow.moveTo(0,0);
		bShadow.lineTo(1,0);
		if(Browser.getStaticAtr("withShadow",false)){
			bShadow.lineStyle(10,mainColor,alfa);
			bShadow.moveTo(0,0);
			bShadow.lineTo(1,0);
			bShadow.tooltip="SHADOW OFF";
		}
		
		bInfo.lineStyle(16,mainColor,alfa);
		bInfo.moveTo(0,0);
		bInfo.lineTo(1,0);
		bInfo.lineStyle(3,0xFFFFFF,90);
		bInfo.moveTo(0,-4);
		bInfo.lineTo(0,-3.8);
		bInfo.moveTo(0,0);
		bInfo.lineTo(0,5);

		bColor.lineStyle(16,mainColor,alfa);
		bColor.moveTo(0,0);
		bColor.lineTo(1,0);


		bGrow.onPress=function(){
			/*
			for(var i=0;i<this.browser.listNodesR.length;i++){
			this.browser.listNodesR[i].delShadow();
			}
			*/
			this.browser.upscale();
			//this.browser.genMindMap(3);
			/*
			for(var i=0;i<this.browser.listNodesR.length;i++){
			this.browser.listNodesR[i].genShadow();
			}
			*/
		}
		bShrink.onPress=function(){
			this.browser.downscale();
			//this.browser.genMindMap(3);
		}
		bReset.onPress=function(){
			this.browser.mc_floor._xscale=100;
			this.browser.mc_floor._yscale=100;
			this.browser.mc_container.tooltip._xscale=100;
			this.browser.mc_container.tooltip._yscale=100;
			//Center View
			this.browser.initialization=true;
			//this.browser.relocateFloor();
			this.browser.relocateMindMap();
		}

		bShadow.onPress=function(){
			this.clear();
			this.lineStyle(16,this.bc.mainColor,this.bc.alfa);
			this.moveTo(0,0);
			this.lineTo(1,0);
			this.lineStyle(14,0xFFFFFF,90);
			this.moveTo(0,0);
			this.lineTo(1,0);
			this.browser.withShadow= this.browser.withShadow?false:true;
			Browser.setStaticAtr("withShadow",this.browser.withShadow);
			trace("withShadow:"+Browser.getStaticAtr("withShadow","hi"));
			if(this.browser.withShadow==true){
				this.lineStyle(10,this.bc.mainColor,this.bc.alfa);
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
