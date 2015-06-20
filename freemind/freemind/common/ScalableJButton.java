/*
 * FreeMind - A Program for creating and viewing MindmapsCopyright (C) 2000-2015
 * Christian Foltin, Joerg Mueller, Daniel Polansky, Dimitri Polivaev and
 * others.
 * 
 * See COPYING for Details
 * 
 * This program is free software; you can redistribute it and/ormodify it under
 * the terms of the GNU General Public Licenseas published by the Free Software
 * Foundation; either version 2of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See theGNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public Licensealong with
 * this program; if not, write to the Free SoftwareFoundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package freemind.common;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JButton;

/**
 * @author foltin
 * @date 19.06.2015
 * @see http://stackoverflow.com/questions/8183949/swing-scale-a-text-font-of-component 
 */
public class ScalableJButton extends JButton {
	int mCurrentSize = 0;
	Font mInitialFont = null;
	int mInitialHeight;
	
	private static final long serialVersionUID = 1L;

	public ScalableJButton(String pString) {
		super(pString);
		init();
	}

	public ScalableJButton() {
		super();
		init();
	}

	private void init() {
		mInitialFont = getFont();
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (mInitialHeight == 0) {
			mInitialHeight = getHeight();
		}
		int resizal = this.getHeight() * mInitialFont.getSize() / mInitialHeight;
		if(resizal != mCurrentSize){
			setFont(mInitialFont.deriveFont((float) resizal));
			mCurrentSize = resizal;
		}
		super.paintComponent(g);
	}
}