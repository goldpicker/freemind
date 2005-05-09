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
* CloudDrawer, draw the clouds of the mindmap
* functions.
*/
class visorFreeMind.CloudDrawer {

	private static var _instance:CloudDrawer=null;

	private function CloudDrawer(){
	}

	public static function getInstance():CloudDrawer{
		if(CloudDrawer._instance==null){
			CloudDrawer._instance=new CloudDrawer();
		}
		return CloudDrawer._instance;
	}

	private function getCloudColor(xml_node:XMLNode):Number{
		if(xml_node.attributes.COLOR!=undefined)
			return new Number("0x"+xml_node.attributes.COLOR.substring(1));
		else
			return 0xEEEEEE;
	}

	function drawCloud(xml_cloud:XMLNode,node:Node,container,isRight){

		//var xml_node:XMLNode=node.getNode_xml();
		var cloudColor=getCloudColor(xml_cloud);
		var cloudBorderColor=cloudColor-0x555555; //Aprox

		var numSubClouds=0;
		//Obtain cloud points.
		var upPoints=[];
		getUpPoints(node,upPoints,numSubClouds,isRight);
		var downPoints=[];
		getDownPoints(node,downPoints,numSubClouds,isRight);
		var sidePoints=[];
		getSidePoints(node,sidePoints,numSubClouds,isRight);

		//////////////// DRAW CLOUD
		container.lineStyle(3,cloudBorderColor,100);
		container.beginFill(cloudColor,100);

		var pOrig=upPoints[0];
		container.moveTo(pOrig[0],pOrig[1]);

		// UP
		for (var i=1;i<upPoints.length;i++){
			drawCurveUpDown(pOrig,upPoints[i],container,1,-1);
			pOrig=upPoints[i];
		}

		//SIDE
		pOrig=upPoints[upPoints.length-1];
		var pDest=downPoints[downPoints.length-1];
		var listSelected=[];
		calcGoodSidePoints(pOrig,pDest,1,sidePoints.length,sidePoints,listSelected,isRight);

		for(var i=0;i<listSelected.length;i++){
			drawCurveSide(pOrig,listSelected[i],container,isRight?1:-1,isRight?-1:1);
			pOrig=listSelected[i];
		}

		//DOWN
		drawCurveSide(pOrig,downPoints[downPoints.length-1],container,isRight?1:-1,isRight?-1:1);

		pOrig=downPoints[downPoints.length-1];
		for (var  i=downPoints.length-2;i>=0;i--){
			drawCurveUpDown(pOrig,downPoints[i],container,-1,1);
			pOrig=downPoints[i];
		}

		drawCurveSide(pOrig,upPoints[0],container,isRight?-1:1,1)
		container.endFill();
	}

	function drawCurveSide(pOrig,pDest,container,sign_x,sign_y){
		var slope=(pDest[0]+0.25-pOrig[0])/(pOrig[1]-0.25-pDest[1]);
		var middlePoint=[(pDest[0]+pOrig[0])/2,(pDest[1]+pOrig[1])/2];
		var mod=Math.abs(30/Math.sqrt(1+slope*slope)); //30 pixels =  absolute displacement
		container.curveTo(middlePoint[0]+sign_x*mod,middlePoint[1]-sign_y*mod*slope,pDest[0],pDest[1]);
	}

	function drawCurveUpDown(pOrig,pDest,container,sign_x,sign_y){
		var slope=(pDest[1]+0.25-pOrig[1])/(pOrig[0]-0.25-pDest[0]);
		var middlePoint=[(pDest[0]+pOrig[0])/2,(pDest[1]+pOrig[1])/2];
		var mod=Math.abs(30/Math.sqrt(1+slope*slope)); //30 pixels =  absolute displacement
		container.curveTo(middlePoint[0]-sign_x*mod*slope,middlePoint[1]+sign_y*mod,pDest[0],pDest[1]);
	}

	function calcGoodSidePoints(maxsup,maxinf,ini,end,sidePoints,listSelected,isRight){
		var slope=(maxinf[0]-maxsup[0])/(maxsup[1]-maxinf[1]);
		var k1=maxsup[0]+slope*maxsup[1];
		var newmax=-1;
		for(var i=ini;i<end;i++){
			var p=sidePoints[i];
			var k2=p[0]+slope*p[1];
			if(( isRight && k1<k2) || (!isRight && k1>k2)){
				k1=k2;
				newmax=i;
			}
		}
		if(newmax>=0){
			calcGoodSidePoints(maxsup,sidePoints[newmax],ini,newmax,sidePoints,listSelected,isRight);
			listSelected.push(sidePoints[newmax]);
			calcGoodSidePoints(sidePoints[newmax],maxinf,newmax+1,end,sidePoints,listSelected,isRight);
		} else  if(((maxinf[0]-maxsup[0])*(maxinf[0]-maxsup[0]) + (maxinf[1]-maxsup[1])*(maxinf[1]-maxsup[1]))>30000){

			var middlePoint=[maxsup[0]+(maxinf[0]-maxsup[0])*0.5,maxsup[1]+(maxinf[1]-maxsup[1])*0.5];
			calcGoodSidePoints(maxsup,middlePoint,ini,ini,sidePoints,listSelected,isRight);
			listSelected.push(middlePoint);
			calcGoodSidePoints(middlePoint,maxinf,ini,ini,sidePoints,listSelected,isRight);

		}
	}

	function getSidePoints(node,sidePoints,numSubClouds,isRight){
		if(node.childNodes.length>0 && node.childNodes[0].ref_mc._visible){
			for(var i=0;i<node.childNodes.length;i++){
				var incNumSubClouds=node.childNodes[i].withCloud==true?numSubClouds+1:numSubClouds;
				getSidePoints(node.childNodes[i],sidePoints,incNumSubClouds,isRight);
			}
		}else{
			var aux=(isRight?node.ref_mc._width+8*numSubClouds:-numSubClouds*8);
			sidePoints.push([node.ref_mc._x+aux,node.ref_mc._y]);
			sidePoints.push([node.ref_mc._x+aux,node.ref_mc._y+node.ref_mc._height]);
		}
	}

	function getUpPoints(node,upPoints,numSubClouds,isRight){
		upPoints.push([node.ref_mc._x+(isRight?0:node.ref_mc._width),node.ref_mc._y-8*numSubClouds]);
		if(node.childNodes.length>0 && node.childNodes[0].ref_mc._visible){
			var incNumSubClouds=node.childNodes[0].withCloud==true?numSubClouds+1:numSubClouds;
			getUpPoints(node.childNodes[0],upPoints,incNumSubClouds,isRight);
		}else{
			upPoints.push([node.ref_mc._x+(isRight?node.ref_mc._width:0),node.ref_mc._y-8*numSubClouds]);
		}
	}

	function getDownPoints(node,downPoints,numSubClouds,isRight){
		downPoints.push([node.ref_mc._x+(isRight?0:node.ref_mc._width),node.ref_mc._y+node.ref_mc._height+8*numSubClouds]);
		if(node.childNodes.length>0 && node.childNodes[node.childNodes.length-1].ref_mc._visible){
			var incNumSubClouds=node.childNodes[node.childNodes.length-1].withCloud==true?numSubClouds+1:numSubClouds;
			getDownPoints(node.childNodes[node.childNodes.length-1],downPoints,incNumSubClouds,isRight);
		}else{
			downPoints.push([node.ref_mc._x+(isRight?node.ref_mc._width:0),node.ref_mc._y+node.ref_mc._height+8*numSubClouds]);
		}
	}



}
