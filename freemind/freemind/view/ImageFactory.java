/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2015 Christian Foltin, Joerg Mueller, Daniel Polansky, Dimitri Polivaev and others.
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
 */

package freemind.view;

import java.net.URL;

import javax.swing.ImageIcon;

import freemind.main.Tools;

/**
 * @author foltin
 * @date 24.05.2015
 */
public class ImageFactory {
	private static final float SCALING_FACTOR = 2.0f;
	private static ImageFactory mInstance = null;

	public static ImageFactory getInstance() {
		if (mInstance == null) {
			mInstance = new ImageFactory();
			Tools.scaleAllFonts(SCALING_FACTOR);
		}
		return mInstance;
	}

	public ImageIcon createIcon(URL pURL){
		ScalableImageIcon icon = new ScalableImageIcon(pURL);
		icon.setScale(SCALING_FACTOR);
		return icon;
	}

	/**
	 * All icons directly displayed in the mindmap view are scaled by the zoom.
	 */
	public ImageIcon createUnscaledIcon(URL pResource) {
		return new ImageIcon(pResource);
	}
}
