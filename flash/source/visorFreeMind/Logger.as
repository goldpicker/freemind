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
/**
 * @class			Logger
 * @version		1.0.0
 * @author			Sascha Balkau <sascha@hiddenresource.corewatch.net>
 *
 * @description	A simple logger class that sends trace actions to
 *					an output panel through a local connection.
 *
 * @usage			
 *					Logger.trace("Debug this message!", 0);
 *					Logger.setFilterLevel(0-4);
 *					Logger.suppressLevelDesc(true/false);
 */

class visorFreeMind.Logger
{
	// The local connection object:
	private static var send_lc:LocalConnection;
	// Determines if a connection is already established:
	private static var connected:Boolean = false;
	// Filter level. By default filter none (0):
	private static var fl:Number = 0;
	// Suppress level descriptions by default:
	private static var sld:Boolean = true;
	
	/**
	 * Constructor
	 */
	private function Logger()
	{
	}
	
	/**
	* trace()
	*/
	public static function trace(msg:Object, lvl:Number):Void
	{
		// Establish a connection at start,
		// and set buffer length to 6000:
		if (!connected)
		{
			send_lc = new LocalConnection();
			connected = true;
		}
		
		// Define variable which will hold message:
		var txt:String = "";
		
		// If no level was given, use 1 by default:
		if (lvl == undefined) lvl = 1;
		
		// Only show messages equal or higher
		// than current filter level:
		if (lvl >= fl && lvl < 5)
		{
			// Define level descriptions
			// if necessary:
			if (!sld)
			{
				if (lvl == 0) txt = "-DEBUG: ";
				else if (lvl == 1) txt = "--INFO: ";
				else if (lvl == 2) txt = "--WARN: ";
				else if (lvl == 3) txt = "-ERROR: ";
				else if (lvl == 4) txt = "-FATAL: ";
			}
			
			txt += String(msg);
			send_lc.send("logger_lc", "onMessage", txt, lvl);
			
			// If you want usual traces as well,
			// uncomment this line:
			//trace(txt);
		}
	}
	
	/**
	* setFilterLevel()
	*/
	public static function setFilterLevel(tmp_fl:Number):Void
	{
		if (tmp_fl != undefined && tmp_fl >= 0 && tmp_fl < 5) fl = tmp_fl;
	}
	
	/**
	* suppressLevelDesc()
	*/
	public static function suppressLevelDesc(tmp_sld:Boolean):Void
	{
		sld = tmp_sld;
	}
}
