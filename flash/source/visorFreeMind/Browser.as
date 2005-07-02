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
	Browser Class
	Author: Juan Pedro de Andres
	Will contain one mindmap in a time.
*/
class visorFreeMind.Browser {
	private var xmlData; // load mm files
	private var xmlCurrent; // The actual xml viewed
	public var visitedMM=[]; // for navigating among visited mm.
	public var fileName;
	private var dictVisitedMM={}; // Dictionary of visited mm.
	public var posXmls:Number=-1;
	private var mcl:MovieClipLoader=new MovieClipLoader();

	public var mc_container:MovieClip; //Movieclip where we are created
	public var mc_floor:MovieClip; // dragable.
	public var floor:Floor; //Class containing the mc where everything is draw
	private var first_node:Node=null;
	private var list_right_clouds:Array=[];
	private var list_left_clouds:Array=[];
	private var list_arrows:Array=[];
	public static var browser;
	private var listNodesR=[];
	private var listNodesL=[];
	private var list_edges=[];
	private var aux_ta=null;
	private var initialization:Boolean=true;
	private var numWaitingImages:Number=0; //When we have images, we have to wait for then loaded
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
	public  var withShadow=getStaticAtr("withShadow",false);

	public var text_selectable=null;

	var my_fmt:TextFormat = new TextFormat();
	var ant_floor_y=0;
	var ant_pnode_y=0;
	var ant_floor_x=0;
	var ant_pnode_x=0;

	var keyControler; //Atends key events
	var buttonsCreator;

	function Browser(file:String,_mc:MovieClip){
		Logger.trace("new Browser, shadow="+withShadow,0);
		browser=this;
		mcl.addListener(this); // For waiting for the load of all images
		PrototypesCreator.init();
		mc_container=_mc;
		loadXML(file);
		createFloor();
		buttonsCreator=new ButtonsCreator(this);
		recalcIfResize();
		createToolTip(); //node_txt en mc_container.
		createInfoWindow();
		buttonsCreator.addToolTipsButtons();
		keyControler=new  KeyControler(this);
	}



	// In case STAGE size change, recalc positions
	function recalcIfResize(){
		Stage.addListener (this);
		Logger.trace("redimensioning",0);
	}

	public function onResize(){
		Logger.trace("redimension",0);
		buttonsCreator.relocateAllButtons();
		floor.fillFondo();
		relocateMindMap();
	}

	/////////////////////// FOR LOADING IMAGES //////////////////
	public function loadImage(fileName,mc_target){
		mcl.loadClip(fileName,mc_target);
		numWaitingImages++;
	}

	public function onLoadInit(target){
		numWaitingImages--;
		validLoaded();
	}

	public function onLoadError(target,error){
		numWaitingImages--;
		validLoaded();
	}

	public function validLoaded(){
		if(numWaitingImages==0)
			relocateMindMap();
	}
	////////////////////////// END LOADING IMAGES ////////////////


	function loadXML(fn:String){
		//first validate if we have it
		if(dictVisitedMM[fn]==undefined){
			xmlData=new XML();
			fileName=fn;
			xmlData.ignoreWhite=true;
			xmlData.onLoad=this.loadedXML;
			xmlData.load(fn);
		} else{
			fileName=fn;
			genMindMap(1);
		}
	}



	function relocateMindMap(){
		relocateNodes(listNodesL[0],0,0,false);
		relocateNodes(listNodesR[0],0,0,true);
		relocateShifts(listNodesL[0],0,false);
		relocateShifts(listNodesR[0],0,true);
		adjustLeftNodes();
		relocateFloor();
		floor.clear();
		drawClouds();
		drawEdges();
		ArrowDrawer.drawArrows(list_arrows,mc_floor,first_node.ref_mc._x);
		listNodesL[0].ref_mc._visible=false; //main node duplicated, only show one (right)
	}

	function adjustLeftNodes(){
		var difx=listNodesR[0].ref_mc._x-listNodesL[0].ref_mc._x;
		var dify=listNodesR[0].ref_mc._y-listNodesL[0].ref_mc._y;
		for(var i=0;i<listNodesL.length;i++){
			listNodesL[i].ref_mc._x+=difx;
			listNodesL[i].ref_mc._y+=dify;
		}
	}


	function createInfoWindow(){
		Logger.trace("creating info creation");
		if(mc_container.info==null){
			Logger.trace("showing info creation");
			mc_container.info=mc_container.createEmptyMovieClip("info",8);
			mc_container.info.createEmptyMovieClip("tex_container",10);
			mc_container.info.tex_container.createTextField("txt", 1, 0, 0,170,10);
			var txt=mc_container.info.tex_container.txt;
			txt.type = "dynamic";
			txt.backgroundColor=0xFFCC77;
			txt._alpha=100
			txt.background=true;
			txt.autoSize = "left";
			txt.align = "left";
			txt.multiline = true;
			txt.wordWrap = true;
			txt.html = true;
			txt.htmlText="<font color='#996611'><b>This is a free</b><br>FREEMIND BROWSER<br> <br><b>shortcuts</b><br>"+
			"LEFT : move left<br>"+
			"RIGHT : move right<br>"+
			"UP : move up<br>"+
			"DOWN : move down<br>"+
			"CTRL LEFT : back history<br>"+
			"CTRL RIGHT : forward history<br>"+
			"CTRL '+' : increase<br>"+
			"CTRL '-' : shrink<br>"+
			"SHIFT : text selection<br>"+
			"CTRL 'c' : node to clipboard</font><br>";
			mc_container.info._visible=false;
		}
	}

	function showInfo(texto){
		Logger.trace("showing info"+mc_container.info);
		var sombra=mc_container.info.createEmptyMovieClip("sombra",9);
		mc_container.info.tex_container.dropShadow(8,4,4,0x777799,sombra);
		mc_container.info._x=_root._xmouse+14;
		mc_container.info._y=_root._ymouse+20;
		mc_container.info._visible=true;
	}

	function hideInfo(){
		mc_container.info._visible=false;
	}

	function createToolTip(){
		if(mc_container.tooltip==null){
			mc_container.tooltip=mc_container.createEmptyMovieClip("tooltip",10);
			mc_container.tooltip.createEmptyMovieClip("tex_container",10);
			mc_container.tooltip.tex_container.createTextField("textfield",7777,0,0,10,10);
			var txt=mc_container.tooltip.tex_container.textfield;
			txt.background=true;
			txt.backgroundColor=0xFFCC77;
			txt.autoSize=true;
			txt.selectable=false;
			txt.border=false;
			my_fmt = new TextFormat();
			my_fmt.color=0x002222;
			my_fmt.font="Arial";
		}
		my_fmt.size=12;
		mc_container.tooltip.tex_container.textfield.text="";
		mc_container.tooltip.tex_container.textfield.setTextFormat(my_fmt);
		mc_container.tooltip._visible=false;
	}

	function showTooltip(texto){
		mc_container.tooltip.tex_container.textfield.text=texto;
		var tt=mc_container.tooltip;
		var sombra=tt.createEmptyMovieClip("sombra",9);
		tt.tex_container.dropShadow(8,4,4,0x777799,sombra);
		
		tt._x=_root._xmouse+14;
		tt._y=_root._ymouse+20;
		reposObjForViewing(tt);
		tt._visible=true;
	}

	function hideTooltip(){
		mc_container.tooltip._visible=false;
	}
	

	function reposObjForViewing(tt){
		var bbox=tt.getBounds(_root);
		//Logger.trace(tt._x+" "+bbox.xMax+" "+Stage.width);
		if(bbox.xMax>Stage.width){
			var newval=Stage.width-bbox.xMax;
			tt._x+=newval;
			//Logger.trace("new x:"+tt._x+" newval:"+newval);
		}
		if(bbox.yMax>Stage.height){
			var newval=Stage.height-bbox.yMax;
			tt._y+=newval;
			//Logger.trace("new x:"+tt._x+" newval:"+newval);
		}
	}
	
	function resetData(){
		for(var j=0;j<list_edges.length;j++){
			list_edges[j].ref_mc.removeMovieClip();
		}
		for(var i=0;i<listNodesR.length;i++){
			listNodesR[i].ref_mc.removeMovieClip();
		}

		for(var i=0;i<listNodesL.length;i++){
			listNodesL[i].ref_mc.removeMovieClip();
		}

		floor.clear();
		list_edges=[];
		listNodesR=[];
		listNodesL=[];
		list_arrows=[];
		list_right_clouds=[];
		list_left_clouds=[];

		Node.currentOver=null;
		Node.num=2000;
		Edge.num=1000;
	}

	function createFloor(){
		floor=new Floor(mc_container);
		mc_floor=floor.getCanvas();
		/*
		mc_floor.onRollOver=function(){
				Logger.trace("entrando");
				//getURL("javascript:giveFocus()");
		}*/

	}


	function deleteForwardHistory(){
		while(visitedMM.length!=(posXmls+1)){
			visitedMM.pop(); //clean olds
		}
	}

	function gestHistory(jumpType){
		if(jumpType==0){ // 0=new
			dictVisitedMM[fileName]=xmlData.firstChild.firstChild;
			deleteForwardHistory();
			posXmls++;
			visitedMM.push(fileName);
		}
		if(jumpType==1){ // 1=visited
			deleteForwardHistory();
			posXmls++;
			visitedMM.push(fileName);
		}
	}

	function saveOldPosition(){
		ant_floor_y=mc_floor._y;
		ant_pnode_y=first_node.ref_mc._y;
		ant_floor_x=mc_floor._x;
		ant_pnode_x=first_node.ref_mc._x;
	}

	function genMindMap(jumpType){
		gestHistory(jumpType);
		saveOldPosition();
		//Clean old Data.
		if(jumpType!=2) {// 2=fold and unfold
			resetData();
			// clear floor.
			floor.clear();
			// generate Tree
			evalXML();
		}else{
			relocateMindMap();
		}
	}

	function relocateFloor(){
		if(initialization){
			mc_floor._x=0;
			mc_floor._y=0;
			mc_floor._x=Stage.width/2-first_node.ref_mc._x;
			mc_floor._y=Stage.height/2-first_node.ref_mc._y;
			initialization=false;
		}else{
			mc_floor._y=ant_floor_y+ant_pnode_y-first_node.ref_mc._y;
			mc_floor._x=ant_floor_x+ant_pnode_x-first_node.ref_mc._x;
		}
	}

	function evalXML(){
		// Initial values.
		var styleNode=1; // 0= elipse, 1=fork , 2=bubble
		var styleLine=0; //0=bezier, 1=linear,2=sharp_bezier,3=sharp_linear
		var lineWidth=0;
		var color_LineaIni=0x888888;
		var nodeXMLIni=dictVisitedMM[fileName];

		//asociate right left to nodes without it
		asociatePosition(nodeXMLIni);
		//Right nodes
		first_node=genNodes(true,nodeXMLIni,0,0,color_LineaIni,lineWidth,styleNode,styleLine,true,mc_floor);
		//Left nodes
		genNodes(false,nodeXMLIni,0,0,color_LineaIni,lineWidth,styleNode,styleLine,true,mc_floor);
		//IF there is no image to load we can show all.
		if(numWaitingImages==0)
			relocateMindMap();
	}

	function relocateShifts(node,incLevel,isRight){
		var m_decy=0;
		var m_incy=0;
		if(node.shift_y>0){
			m_incy=node.shift_y;
			incLevel+=m_incy;
		}else{
			m_decy=-node.shift_y;
		}

		node.ref_mc._y+=incLevel;
		var negvals=0;
		if(node.folded==false){
			for(var i=0;i<node.childNodes.length;i++){
				var mv=relocateShifts(node.childNodes[i],incLevel,isRight);
				incLevel=mv[0];
				negvals+=mv[1];
				
			}
		}
		node.ref_mc._y+=negvals;
		return [incLevel+m_decy,m_decy+negvals];
	}
	
	function relocateNodes(node,x,y,isRight){
		var incy:Number=y;
		var numE:Number=0;
		var y1:Node=node;
		var yn:Node=node;
		var resize:Number=0;

		if(node.withCloud) {
			incy+=18;
		}

		if(node.withImage){
			node.withImage=false;
			node.posElements();
			node.drawAroundNode(node.cbg,100,false);
		}
		
		//hgap added
		node.ref_mc._x=x-(isRight?-node.hgap:node.ref_mc._width+node.hgap);
		var incx:Number=getIncX(isRight,node,node.ref_mc._x);

		if(node.folded==false){
			for(var i=0;i<node.childNodes.length;i++){
				var mv=relocateNodes(node.childNodes[i],incx,incy,isRight);
				numE++;
				incy=mv[0];
				if(i==0) {
					y1=mv[1];//take first
				}
				yn=mv[2]; // take last
			}
		} else {
			hideSubNodes(node);
		}

		if(numE>=1)
			node.ref_mc._y=y1.ref_mc._y+((yn.ref_mc._y-y1.ref_mc._y)/2);
		else
			node.ref_mc._y=incy;
			
		//for solving diferent fonts sizes
		incy=Math.max(incy,node.ref_mc._y+node.ref_mc._height); 

		node.ref_mc._visible=true;

		if(node.withCloud==true )
			return [incy+18,y1,yn];
		else
			return [incy+2,y1,yn];
	}

	function hideSubNodes(node){
		node.ref_mc._visible=false;
		for(var i=0;i<node.childNodes.length;i++){
			hideSubNodes(node.childNodes[i]);
		}
	}

	function drawEdges(){
		for(var i=0;i<list_edges.length;i++){
			if(list_edges[i]._dest.ref_mc._visible==true)
				list_edges[i].draw();
			else
				list_edges[i].ref_mc.clear();
		}
	}

	function drawClouds(){
		var lizq=list_left_clouds;
		for (var i=lizq.length-1;i>=0;i--){
			if(lizq[i][1].ref_mc._visible==true)
				CloudDrawer.getInstance().drawCloud(lizq[i][0],lizq[i][1],lizq[i][2],false);
		}
		var ld=list_right_clouds;
		for (var i=ld.length-1;i>=0;i--){
			if(ld[i][1].ref_mc._visible==true)
				CloudDrawer.getInstance().drawCloud(ld[i][0],ld[i][1],ld[i][2],true);
		}
	}


	function getLineStyle(edge_xml,styleLine){
		if(edge_xml!=null){
			if(edge_xml.attributes.STYLE!=null){
				if(edge_xml.attributes.STYLE=="sharp_bezier") styleLine=2;
				else if(edge_xml.attributes.STYLE=="bezier") styleLine=0;
				else if(edge_xml.attributes.STYLE=="linear") styleLine=1;
				else if(edge_xml.attributes.STYLE=="sharp_linear") styleLine=3;
			}
		}
		return styleLine;
	}

	function getLineWidth(edge_xml,lineWidth){
		if(edge_xml!=null){
			if(edge_xml.attributes.WIDTH!=null){
				if(edge_xml.attributes.WIDTH=="thin")
					lineWidth=0;
				else
				lineWidth=new Number(edge_xml.attributes.WIDTH);
			}
		}
		return lineWidth;
	}

	function getLineColor(edge_xml,colorFloor){
		if(edge_xml!=null){
			if(edge_xml.attributes.COLOR!=undefined){
				var cn:String=edge_xml.attributes.COLOR;
				colorFloor=new Number("0x"+cn.substring(1));
			}
		}
		return colorFloor;
	}

	private function getNodeColor(xml_node:XMLNode):Number{
		if(xml_node!=null && xml_node.attributes.COLOR!=undefined)
			return new Number("0x"+xml_node.attributes.COLOR.substring(1));
		else
			return 0xEEEEEE;
	}


	function getNodeStyle(node_xml,styleNode){
		if(node_xml.attributes.STYLE=="bubble")
		  styleNode=2;
		if(node_xml.attributes.STYLE=="fork")
		  styleNode=1;
		return styleNode;
	}

	function getIncX(isRight,node,x){
		if(isRight)
		   return  x+22+node.ref_mc._width;
		else
			return node.ref_mc._x-22;
	}

	function getTextSize(font){
		if(font.attributes.SIZE!=null)
			return new Number(font.attributes.SIZE);
		return 12;
	}

	function getItalic(font){
		if(font.attributes.ITALIC!=null && font.attributes.ITALIC=="true")
			return true;
		return false;
	}

	function getBold(font){
		if(font.attributes.BOLD!=null && font.attributes.BOLD=="true")
			return true;
		return false;
	}

	function getFontType(font){
		if(font.attributes.NAME!=null && font.attributes.NAME!="SansSerif")
			return font.attributes.NAME;
		return "_sans";
	}

	function asociatePosition(node_xml){
		var cont=0;
		// n.attributes.POSITION!=(isRight?"left":"right")
		for(var i=0;i<node_xml.childNodes.length;i++){

			var n=node_xml.childNodes[i];
			//subnodes
			 if(n.nodeName=="node" ){
			 	if(n.attributes.POSITION===undefined)
			 		n.attributes.POSITION=(cont % 2==1?"left":"right");
					cont++;
			}
		}
	}
	
	function genNodes(isRight,node_xml,x,y,lineColor,lineWidth,styleNode,styleLine,first,container){
		var n:XMLNode=null;

		//get edge style.
		var edge=getFirstNodeType("edge",node_xml);
		var font=getFirstNodeType("font",node_xml);
		styleLine=getLineStyle(edge,styleLine);
		lineWidth=getLineWidth(edge,lineWidth);
		lineColor=getLineColor(edge,lineColor);
		styleNode=getNodeStyle(node_xml,styleNode);
		var cloudNode=getFirstNodeType("cloud",node_xml);
		var arrows=getNodesType("arrowlink",node_xml);
		var cloudColor:Number=getNodeColor(cloudNode);
		var textSize=getTextSize(font);
		var italic=getItalic(font);
		var bold=getBold(font);
		var type=getFontType(font);
		var withCloud:Boolean=cloudNode!=null?true:false;

		var folded=true;
		if(node_xml.attributes.FOLDED!="true")
		  folded=false;

		var node:Node=new Node(0,0,node_xml,node_xml.attributes.TEXT,"",container,
							   3,lineColor,lineWidth,(first?0:styleNode),styleLine,folded,isRight,withCloud,
							   textSize,italic,bold,type,this);
		node.draw();

		if(isRight)
			listNodesR.push(node);
		else
			listNodesL.push(node);

		var childNodes:Array=[];

		//creation of all nodes, folded or not.
		for(var i=0;i<node_xml.childNodes.length;i++){

			n=node_xml.childNodes[i];
			//subnodes
			 if(n.nodeName=="node" && n.attributes.POSITION!=(isRight?"left":"right")){
				var subnode=genNodes(isRight,n,0,0,lineColor,lineWidth,styleNode,styleLine,false,container);
				childNodes.push(subnode);
			}

		}

		node.childNodes=childNodes;

		for (var i=0; i<childNodes.length;i++){
			var enl:Edge=new Edge(node,childNodes[i],"",container);
			list_edges.push(enl);
		}

		if(cloudNode!=undefined ){
			if(isRight)
				list_right_clouds.push([cloudNode,node,container]);
			else
				list_left_clouds.push([cloudNode,node,container]);
		}

		for(var i=0;i<arrows.length;i++){
			list_arrows.push([node.getID(),
							arrows[i].attributes.DESTINATION,
							arrows[i].attributes.STARTARROW,
							arrows[i].attributes.ENDARROW,
							getNodeColor(arrows[i])]);
		}

		return node;
	}


	function getFirstNodeType(type,node_xml){
		for(var i=0;i<node_xml.childNodes.length;i++){
			var n=node_xml.childNodes[i];
			if (n.nodeName==type )
			   return n;
		}
		return null;
	}

	function getNodesType(type,node_xml){
		var aux=[];
		for(var i=0;i<node_xml.childNodes.length;i++){
			var n=node_xml.childNodes[i];
			if (n.nodeName==type )
			   aux.push(n);
		}
		return aux;
	}

	function loadedXML(loaded) {
		if (loaded) {
		Browser.browser.genMindMap(0);
		} else {
			Logger.trace("file not loaded!");
		}
	}


	static	function setStaticAtr(nameAtr,value){
		var freeMindVars = SharedObject.getLocal("freeMindBrowser");
		freeMindVars.data[nameAtr]=value;
		freeMindVars.flush();
	}


	static function getStaticAtr(nameAtr,defaultVal){
		var freeMindVars = SharedObject.getLocal("freeMindBrowser");
		if(freeMindVars.data[nameAtr]==null){
			return defaultVal;
		}
		return freeMindVars.data[nameAtr];
	}

}
