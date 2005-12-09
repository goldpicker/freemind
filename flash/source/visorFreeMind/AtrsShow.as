import visorFreeMind.*;

class visorFreeMind.AtrsShow {
	
	private static var tf:TextFormat = new TextFormat();
	private static var initialized:Boolean=false;
	private static var mc:MovieClip=null;
	
	public static function showAtrs(node:Node,browser:Browser){
		if(mc==null)
		  mc=getAtrsShow(node,browser);
		mc._x=_root._xmouse+14;
		mc._y=_root._ymouse+20;
	}
 
 	public static function hideAtrs(node:Node,browser:Browser){
 		if(mc)
 			mc.removeMovieClip();
 		mc=null;
 	}
 		public static function getAtrsShow(node:Node,browser:Browser){
		init();
		var mc:MovieClip=browser.mc_container.createEmptyMovieClip("atrShow",16);
		var mcAtr:MovieClip=mc.createEmptyMovieClip("atrs",mc.getNextHighestDepth());
		//Icons.get_gohome(mcAtr,20);
		var mcValues=mc.createEmptyMovieClip("values",mc.getNextHighestDepth());
		var mcLineas=mc.createEmptyMovieClip("lineas",mc.getNextHighestDepth());
		//Icons.get_gohome(mcValues,20);
		var latrs:Array=node.getAtributes();
		var despy=0;
		mcAtr.createTextField("name",0,0,despy,0,0);
		var atrName:TextField=mcAtr["name"];
		mcValues.createTextField("name",0,0,despy,0,0);
		var atrValue:TextField=mcValues["name"];
		atrName.autoSize=true;
		atrName.background=true;
		atrName.border=true;

		atrName.backgroundColor=0xFFDD88;
		atrValue.autoSize=true;
		atrValue.background=true;
		atrValue.border=true;
		atrValue.backgroundColor=0xFFe9a5;
		var ret="";
		var i=0;
		for(i=0;i<latrs.length;i++){
			var atr:XMLNode=latrs[i];
			atrName.text+=ret+atr.attributes.NAME;
			//atrName.setTextFormat(tf);
			atrValue.text+=ret+atr.attributes.VALUE;
			//atrValue.setTextFormat(tf);
			ret="\n";
		}
		mcValues._x=mcAtr._width;
		
		mcLineas.lineStyle(1,0x00000,20);
		for(var j=1;j<i;j++){
			mcLineas.moveTo(1,j*mc._height/i);
			mcLineas.lineTo(mc._width-1,j*mc._height/i);
		}
		return mc;
	}
	
	private static function init(){
		if(initialized)return;
		tf.color=0x444444;
		tf.font="arial";
		tf.size=12;
		tf.bold=false;
		tf.italic=false;
		initialized=true;
	}
}