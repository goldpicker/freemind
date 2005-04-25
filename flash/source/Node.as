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
/*$Id: Node.as,v 1.1 2005-04-25 20:32:25 christianfoltin Exp $*/
// Node of the hierarchical map
class Node {
	public static var num:Number=2000; // counter of nodes
	public static var colorSel:Number=0xBBBBBB; // select color
	public static var colorNoSel:Number=0xFFDD44; // unselect color

	public static var currentOver:Node=null;

	public var childNodes:Array;
	private var lastClicks:Number=0;
	public var text:String; // Text of the node
	private var coment:String;
	private var _x:Number; // xpos of the node
	private var _y:Number; // ypos of the node
	private var node_xml;
	public var ref_mc:MovieClip;
	private var over:Boolean;
	private var id:Number;
	private var lastclick:Number=0;
	private var edges_ar:Array=null;
	private var open_intv:Number=0;
	private var close_intv:Number=0;
	public var cf:Number=0xCC0000;
	public var cfont:Number=0x444444;
	public var cbg:Number=0;
	public var style:Number=1; // 0= elipse, 1=fork , 2=bubble
	public var lineWidth:Number=0;
	public var styleLine:Number=0; //0=llave, 1=Linea,2=sharp_llave,3=sharp_linea
	public var folded:Boolean;
	public var isRight:Boolean;
	public var haveEdge:Boolean;
	public var withCloud:Boolean;
	public var textSize:Number;
	public var italic:Boolean;
	public var bold:Boolean;
	public var font:String;
	private var link:MovieClip;
	private var withImage:Boolean=false;
	private var listElements:Array=null;
	private var counter=0;
	private var browser:Browser;
	private var box_txt;
	private var sombra=null;
	private var eventControler;

	function getNode_xml(){
		return node_xml;
	}

	function Node(x:Number,y:Number,node_xml,nom:String,coment:String,mc:MovieClip,
							yy,cf:Number,lineWidth:Number,style:Number,styleLine:Number,
							folded:Boolean,isRight:Boolean,withCloud:Boolean,
							textSize:Number,italic:Boolean,bold:Boolean,font:String,browser:Browser){
		this.cf=cf;
		this.lineWidth=lineWidth;
		this.style=style;
		this.styleLine=styleLine;
		this.folded=folded;
		this.isRight=isRight;
		this.withCloud=withCloud;
		this.textSize=textSize;
		this.italic=italic;
		this.bold=bold;
		this.font=font;
		this.browser=browser;
		this.node_xml=node_xml;
		this.haveEdge=node_xml.attributes.LINK!=undefined?true:false;
		text=nom;
		coment=coment;
		listElements=[];
		num+=2;
		//creation of asociated movieClip
		id=num;
		ref_mc=mc.createEmptyMovieClip(node_xml.attributes.ID,num);
		box_txt=ref_mc.createEmptyMovieClip("box_txt",11);
		ref_mc.node_txt=ref_mc.createEmptyMovieClip("node_txt",12);

		ref_mc._x=x;
		ref_mc._y=y;
		ref_mc._visible=false;

		ref_mc.inst=this; // add a reference to the Node class that create it
		ref_mc.box_txt.inst=this;

		if(node_xml.attributes.COLOR!=undefined){
			var cn:String=node_xml.attributes.COLOR;
			cfont=new Number("0x"+cn.substring(1));
		}
		if(node_xml.attributes.BACKGROUND_COLOR!=undefined){
			var cn:String=node_xml.attributes.BACKGROUND_COLOR;
			cbg=new Number("0x"+cn.substring(1));
		}

		eventControler=ref_mc;
		if(style==2 && cbg!=0)
			eventControler=ref_mc.box_txt;

		activateEvents();
	}

	public function deactivateEvents(){
		eventControler.onPress=null;
		eventControler.onRollOver=null;
		eventControler.onRollOut=null;
	}

	public function activateEvents(){

		eventControler.onPress=function(){

		//var oldClick=this.inst.lastClick;
		//this.inst.lastClick=getTimer();

			if(this.inst.node_xml.attributes.LINK != undefined && this.inst.link.hitTest(_root._xmouse,_root._ymouse,false)){
				var url=this.inst.node_xml.attributes.LINK;
				if(url.indexOf("http://") > -1 || url.indexOf(".mm")==-1)
					getURL(url,"_blank");
				else{
					this.inst.browser.loadXML(url);
				}
				return;
			}

			if(this.inst.hasSubnodes()){
				if(this.inst.folded){
					this.inst.node_xml.attributes.FOLDED="false";
					this.inst.folded=false;
					this.inst.colorSelect();
				}else{
					this.inst.node_xml.attributes.FOLDED="true";
					this.inst.folded=true;
					this.inst.colorSelect();
				}
				this.inst.lastClick=0;
				this.inst.browser.genMindMap(2);
			}
			return;
		}

		eventControler.onRollOver=function(){
			if (Node.currentOver instanceof Node )
				Node.currentOver.colorNoSelect();
			Node.currentOver=this.inst;
			this.inst.colorSelect();
			if(this.inst.node_xml.attributes.LINK != undefined){
				this.inst.browser.showTooltip(this.inst.node_xml.attributes.LINK);
			}
		}

		eventControler.onRollOut=function(){
			this.inst.browser.hideTooltip();
			Node.currentOver.colorNoSelect();
		}

	}

	function hasSubnodes(){
		if(node_xml.childNodes.length==0) return false;
		for(var i=0;i<node_xml.childNodes.length;i++){
			if(node_xml.childNodes[i].nodeName=="node") return true;
		}
		return false;
	}


	public function addEdge(e:Edge){
		edges_ar.push(e);
	}

	public function colorSelect(){
		drawAroundNode(colorNoSel,100,true);
	}

	public function colorNoSelect(){
		drawAroundNode(cbg,100,false);
	}

	// draw ovals/circles
	private function circle2(x,y,width,height,color:Number){
		var a=width;
		var b=height;
		var j=a*0.70711;
		var n=b*0.70711;
		var i=j-(b-n)*a/b;
		var m=n-(a-j)*b/a;
		ref_mc.lineStyle(lineWidth,color,100);
		ref_mc.beginFill(color,0);
		ref_mc.moveTo(x+a,y);
		ref_mc.curveTo(x+a,y-m,x+j,y-n);
		ref_mc.curveTo(x+i,y-b,x,y-b);
		ref_mc.curveTo(x-i,y-b,x-j,y-n);
		ref_mc.curveTo(x-a,y-m,x-a,y);
		ref_mc.curveTo(x-a,y+m,x-j,y+n);
		ref_mc.curveTo(x-i,y+b,x,y+b);
		ref_mc.curveTo(x+i,y+b,x+j,y+n);
		ref_mc.curveTo(x+a,y+m,x+a,y);
		ref_mc.endFill();
	}

	private function circle(width,height,color:Number,alpha:Number,colorLine:Number){
		box_txt.clear();
		var x=width*0.5;
		var y=height*0.5;
		var a=x;
		var b=height*0.80;
		var j=a*0.70711;
		var n=b*0.70711;
		var i=j-(b-n)*a/b;
		var m=n-(a-j)*b/a;
		box_txt.lineStyle(2,colorLine,100);
		box_txt.beginFill(color,alpha);
		box_txt.moveTo(x+a,y);
		box_txt.curveTo(x+a,y-m,x+j,y-n);
		box_txt.curveTo(x+i,y-b,x,y-b);
		box_txt.curveTo(x-i,y-b,x-j,y-n);
		box_txt.curveTo(x-a,y-m,x-a,y);
		box_txt.curveTo(x-a,y+m,x-j,y+n);
		box_txt.curveTo(x-i,y+b,x,y+b);
		box_txt.curveTo(x+i,y+b,x+j,y+n);
		box_txt.curveTo(x+a,y+m,x+a,y);
		box_txt.endFill();
	}
	// draw rounded rectangle
	private function round_rectangle(w:Number,h:Number,color:Number,alpha:Number,colorLine:Number){
		box_txt.clear();

		var incx=2;
		var d=2;
		var a=2;
		box_txt.lineStyle(1,colorLine,100);
		box_txt.beginFill(color,alpha);
		/*
		// Temporal
		box_txt.roundRect(0,0,w,h,4);
		box_txt.endFill();
		return;
		//--------------- test ---------
		*/
		box_txt.moveTo(a-incx,a-d);
		box_txt.lineTo(w-a+incx,a-d);
		//box_txt.curveTo(w-a-0.5+d+incx,a+0.5-d,w-a+d+incx,a);
		box_txt.lineTo(w-a+d+incx,a);
		box_txt.lineTo(w-a+d+incx,h-a);
		box_txt.lineTo(w-a+d+incx,h-a+d);
		//box_txt.curveTo(w-a-0.5+d+incx,h-0.5-a+d,w-a+incx,h-a+d);
		box_txt.lineTo(w-a+incx,h-a+d);
		box_txt.lineTo(a-incx,h-a+d);
		//box_txt.curveTo(a-d-incx,h-a+d,a-d-incx,h-a);
		box_txt.lineTo(a-d-incx,h-a);
		box_txt.lineTo(a-d-incx,a);
		//box_txt.curveTo(a-d-incx,a-d,a-incx,a-d);
		box_txt.lineTo(a-incx,a-d);
		box_txt.endFill();
	}

	private function sel_subline(w:Number,h:Number,color:Number,alpha:Number,colorLine:Number){
		ref_mc.clear();

		var incx=2;
		var d=2;
		var a=4;
		ref_mc.lineStyle(1,colorLine,100);
		ref_mc.beginFill(color,alpha);
		ref_mc.moveTo(a-incx,a-d);
		ref_mc.lineTo(w-a+incx,a-d);
		ref_mc.curveTo(w-a+d+incx,a-d,w-a+d+incx,a);
		ref_mc.lineTo(w-a+d+incx,h-a);
		ref_mc.curveTo(w-a+d+incx,h-a+d,w-a+incx,h-a+d);
		ref_mc.lineTo(a-incx,h-a+d);
		ref_mc.curveTo(a-d-incx,h-a+d,a-d-incx,h-a);
		ref_mc.lineTo(a-d-incx,a);
		ref_mc.curveTo(a-d-incx,a-d,a-incx,a-d);
		ref_mc.endFill();
		a=0;
		ref_mc.lineStyle(lineWidth,cf,100);
		ref_mc.moveTo(0,h-a);
		ref_mc.lineTo(w,h-a);
	}

	private function subline(w:Number,h:Number,color:Number,alpha){
		var a=0;
		ref_mc.clear();
		ref_mc.lineStyle(lineWidth,cf,100);
		ref_mc.moveTo(0,h-a);
		ref_mc.lineTo(w,h-a);
		//ref_mc.dashTo(0,h-a, w,h-a, 1, 2,lineWidth);
	}

	public function crearTextField(name_txt:String){
		if(text.indexOf("<html>")>=0 && text.indexOf(".jpg")>=0){
			var start=text.indexOf("img src=")+9;
			var length=text.indexOf(".jpg")+4-start;
			// have to wait for the image load.
			var cont_image=ref_mc.node_txt.createEmptyMovieClip(name_txt,2);
			browser.loadImage(text.substr(start,length),cont_image);
			withImage=true;
			return;

		}

		if(text.indexOf("<html>")>=0 && text.indexOf(".swf")>=0){
			var start=text.indexOf("img src=")+9;
			var length=text.indexOf(".swf")+4-start;
			// have to wait for the image load.
			var cont_image=ref_mc.node_txt.createEmptyMovieClip(name_txt,2);
			browser.loadImage(text.substr(start,length),cont_image);
			withImage=true;
			return;

		}

		ref_mc.node_txt.createTextField(name_txt,3,0,0,10,10);
		var my_fmt:TextFormat = new TextFormat();
		if(text.indexOf("<html>")>=0){
			ref_mc.node_txt.node_txt.html=true;
			ref_mc.node_txt.node_txt.htmlText=text;
		}else{
			ref_mc.node_txt.node_txt.text=text;
		}
		var txt=ref_mc.node_txt.node_txt;
		txt.background=false;
		txt.backgroundColor=0xFFAAAA;
		txt.autoSize=true;
		txt.selectable=false;
		txt.border=false;
		my_fmt.color=cfont;
		my_fmt.font=font;
		my_fmt.size=textSize;
		my_fmt.bold=bold;
		my_fmt.italic=italic;
		txt.setTextFormat(my_fmt);

	}

	public function drawAroundNode(colorNoSel,alpha,isSelected){
		ref_mc.clear();
		if (colorNoSel==0)
			alpha=0;
		// 0= elipse, 1=fork , 2=bubble
		var n=ref_mc.node_txt;
		if  (style==2){
			round_rectangle(n._width, n._height,colorNoSel,alpha,cf);

		if  ( alpha==100 && cbg!=0 && Browser.withShadow){
			if(isSelected){
				round_rectangle(n._width, n._height,cbg,alpha,cf);
				sombra._visible=false;
			}else{
				sombra._visible=true;
			}
		}

		}
		else if(style==0){
			circle(n._width, n._height,colorNoSel,alpha,cf);
		}
		else{
			if(alpha==100)//selected
				sel_subline(n._width, n._height,colorNoSel,alpha,colorSel);
			else
				subline(n._width,n._height,colorNoSel,alpha);
		}
		if (folded==true){
		    drawCircle(n._width,n._height,cf);
		}
	}



	private function drawCircle(w,h,color){
		if(!isRight) w=-4;
		if(style==1)
		    circle2(w+2,h,2,2,color);
		else
			circle2(w+2,h/2,2,2,color);
	}

	public function draw(){
		counter++;
		crearTextField("node_txt");
		if(haveEdge && !isRight) {
			addLinkIcon();
			link._x=0;
		}
		var iconsList=getIcons(node_xml);
		for(var i=0;i<iconsList.length;i++){
			if(Icons["get_"+iconsList[i]]!=null){
				addIcon(Icons["get_"+iconsList[i]](ref_mc.node_txt,i));
			}
		}
		if(haveEdge&& isRight)
			addLinkIcon();

		if(withImage==false)
			posElements();

		drawAroundNode(cbg,100,false);
	}

	function posElements(){
		if(isRight){
			//First icons
			var initX=0;
			var initY=(Math.max(ref_mc.node_txt.node_txt._height,16)-16)/2;
			//Logger.trace(text+" "+ref_mc.node_txt.node_txt._height);
			for(var i=0;i<listElements.length;i++){
				listElements[i]._x=initX;
				listElements[i]._y=initY;
				initX+=listElements[i]._width;
			}

			ref_mc.node_txt.node_txt._x=initX;
			initX+=ref_mc.node_txt.node_txt._width;
			if(link){
				link._x=initX;
				link._y=initY;
			}
		}else{
			var initX=0;
			var initY=(Math.max(ref_mc.node_txt.node_txt._height,16)-16)/2;
			if(link){
				link._x=initX;
				link._y=initY;
				initX+=link._width;
			}
			ref_mc.node_txt.node_txt._x=initX;
			initX+=ref_mc.node_txt.node_txt._width;

			for(var i=0;i<listElements.length;i++){
				listElements[i]._x=initX;
				listElements[i]._y=initY;
				initX+=listElements[i]._width;
			}

		}
		if(style==2 && cbg!=0 && Browser.withShadow){
			sombra=ref_mc.createEmptyMovieClip("sombra",10);
			ref_mc.node_txt.dropShadow(8,6,4,0x555555,sombra);
		}

	}

	public function addIcon(icon){
		listElements.push(icon);
		return;
		var width=ref_mc.node_txt._width;
		if(isRight){
			icon._x=ref_mc.node_txt.node_txt._x;
			ref_mc.node_txt.node_txt._x+=icon._width;
		}else{
			icon._x=width;
		}

		icon._y=(ref_mc.node_txt._height-icon._height)/2;
	}

	public function addLinkIcon(){
		var width=ref_mc.node_txt._width;
		if(node_xml.attributes.LINK.indexOf(".mm")>-1)
			link=Icons.get_mm_link(ref_mc.node_txt);
		else
			link=Icons.genLink(ref_mc.node_txt);
	}


	function getIcons(node_xml){
		var iconsList=[];
		for(var i=0;i<node_xml.childNodes.length;i++){
			var n=node_xml.childNodes[i];
			if (n.nodeName=="icon" && n.attributes.BUILTIN!=null)
				iconsList.push(n.attributes.BUILTIN);
		}
		return iconsList;
	}

}
