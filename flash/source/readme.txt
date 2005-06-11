Just a personal freemind flash browser.
It does only load jpg's images (limitation of flash),
 it will improve with time.
 
 For the easy of development flashout (http://www.potapenko.com/flashout/) have
 been used with Eclipse.

USE:
 - insert in any browser page like in the example.
 
 - indicate the file to load (default is "index.mm")
	 New way (as in the new mindmaps.html):
	 		fo.addVariable("initLoadFile", "index.mm");
	 		
	 OLD way (adding the embeded flash object):
	 - use the variable "initLoadFile" to indicate the file to load.
	   example:
	
	 <param name="FlashVars" value="initLoadFile=index.mm"/>
	 and 
	 <embed FlashVars="initLoadFile=index.mm" 