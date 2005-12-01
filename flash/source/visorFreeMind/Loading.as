import visorFreeMind.*;

class visorFreeMind.Loading {
	
	var cont_load:MovieClip;
	var browser:Browser;
	var mainColor;
	
	function show(){
		cont_load._x=(Stage.width-40)/2;
		cont_load._y=(Stage.height-cont_load._height)/2;
		getMainColor();
		cont_load._visible=true;
		Mouse.hide();
	}
	
	function hide(){
		cont_load._visible=false;
		Mouse.show();
	}
	
	function Loading(browser,father){
		createLoadingWindow(browser,father);
	}
	
	function createLoadingWindow(browser,father){
		this.browser=browser;
		cont_load=father.createEmptyMovieClip("loading",12);
		cont_load._visible=false;
		cont_load.loading=this;
		cont_load.pos=0;
		cont_load.onEnterFrame=function(){
			trace("f1");
			if(this._visible==true){
				this.clear();
				var x=0;
				for(var i=0;i<5;i++){
					if(i==this.pos)
						this.loading.drawCircle(x,100);
					else
						this.loading.drawCircle(x,50);
					x+=22;
				}
				this.pos=(this.pos+1)%5;
			}
		}
		trace("CCCCCCCCCCCCCRRRRRRRRRRRR2");
	}

	function drawCircle(x,alfa){
		trace("dibujando LOADING");
		cont_load.lineStyle(16,mainColor,alfa);
		cont_load.moveTo(x+0,0);
		cont_load.lineTo(x+1,0);
	}
	
	function getMainColor(){
		var color=browser.floor.getBackgroundColor();
		var nRed = (color >> 16)-0x33;
		nRed=nRed>=0?nRed:0;
		var nGreen= ((color >> 8) & 0xff)-0x33;
		nGreen=nGreen>=0?nGreen:0;
		var nBlue= (color & 0xff)-0x33;
		nBlue=nBlue>=0?nBlue:0;
		this.mainColor=(nRed<<16 | nGreen<<8 |nBlue);

	}
}