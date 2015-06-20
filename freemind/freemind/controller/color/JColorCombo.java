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
package freemind.controller.color;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.WindowConstants;

import freemind.modes.mindmapmode.MindMapToolBar;


public class JColorCombo extends JComboBox<ColorPair> {

	public class ColorIcon extends ImageIcon {

		private static final int ICON_SIZE = 30;
		private Color mColor;
		private BufferedImage mImage;

		public ColorIcon(Color pColor) {
			super(new BufferedImage(ICON_SIZE,ICON_SIZE,BufferedImage.TYPE_INT_RGB));
			mColor = pColor;
			mImage = (BufferedImage) getImage();
			Graphics g = mImage.getGraphics();
			g.setColor(pColor);
			g.fillRect(0, 0, ICON_SIZE, ICON_SIZE);
			g.dispose();
		}

		
		
	}

	public JColorCombo() {
		ColorPair[] colorList = sColorList;
		for (int i = 0; i < colorList.length; i++) {
			ColorPair colorPair = colorList[i];
			addItem(colorPair);
		}
		ComboBoxRenderer renderer = new ComboBoxRenderer();
		setRenderer(renderer);
		setMaximumRowCount(20);
	}
	/** See {@link MindMapToolBar} */
	public java.awt.Dimension getMaximumSize() {
		return getPreferredSize();
	}
	
	public class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		public ComboBoxRenderer() {
			setOpaque(true);
	        setHorizontalAlignment(LEFT);
	        setVerticalAlignment(CENTER);
		}
		
		/*
	     * This method finds the image and text corresponding
	     * to the selected value and returns the label, set up
	     * to display the text and image.
	     */
	    public Component getListCellRendererComponent(
	                                       JList list,
	                                       Object value,
	                                       int index,
	                                       boolean isSelected,
	                                       boolean cellHasFocus) {
	        if (isSelected) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        }

	        ColorPair pair = (ColorPair) value;
	        String colorName = pair.name;
	        ImageIcon icon = new ColorIcon(pair.color);
	        setIcon(icon);
	        if (icon != null) {
	            setText(colorName);
	        } else {
	        	System.err.println("baeh");
	        }

	        return this;
	    }
	}

	public static void main(String[] s) {
		JFrame frame = new JFrame("JColorChooser");
		JColorCombo colorChooser = new JColorCombo();

		frame.getContentPane().add(colorChooser);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public static ColorPair[] sColorList = new ColorPair[] {
		// taken from http://wiki.selfhtml.org/wiki/Grafik/Farbpaletten#Farbnamen
		// default 16bit colors
		new ColorPair(new Color(0x000000), "Black"),
		new ColorPair(new Color(0x808080), "Gray"),
		new ColorPair(new Color(0x800000), "Maroon"),
		new ColorPair(new Color(0xFF0000), "Red"),

		new ColorPair(new Color(0x008000), "Green"),
		new ColorPair(new Color(0x00FF00), "Lime"),

		new ColorPair(new Color(0x808000), "Olive"),
		new ColorPair(new Color(0xFFFF00), "Yellow"),

		new ColorPair(new Color(0x000080), "Navy"),
		new ColorPair(new Color(0x0000FF), "Blue"),

		new ColorPair(new Color(0x800080), "Purple"),
		new ColorPair(new Color(0xFF00FF), "Fuchsia"),
		new ColorPair(new Color(0x008080), "Teal"),
		new ColorPair(new Color(0x00FFFF), "Aqua"),

		new ColorPair(new Color(0xC0C0C0), "Silver"),
		new ColorPair(new Color(0xFFFFFF), "White"),

		// netscape colors
		new ColorPair(new Color(0xFFC0CB), "Pink"),
		new ColorPair(new Color(0xFFB6C1), "LightPink"),
		new ColorPair(new Color(0xFF69B4), "HotPink"),
		new ColorPair(new Color(0xFF1493), "DeepPink"),
		new ColorPair(new Color(0xDB7093), "PaleVioletRed"),
		new ColorPair(new Color(0xC71585), "MediumVioletRed"),
		new ColorPair(new Color(0xFFA07A), "LightSalmon"),
		new ColorPair(new Color(0xFA8072), "Salmon"),
		new ColorPair(new Color(0xE9967A), "DarkSalmon"),
		new ColorPair(new Color(0xF08080), "LightCoral"),
		new ColorPair(new Color(0xCD5C5C), "IndianRed"),
		new ColorPair(new Color(0xDC143C), "Crimson"),
		new ColorPair(new Color(0xB22222), "FireBrick"),
		new ColorPair(new Color(0x8B0000), "DarkRed"),
		new ColorPair(new Color(0xFF0000), "Red"),
		new ColorPair(new Color(0xFF4500), "OrangeRed"),
		new ColorPair(new Color(0xFF6347), "Tomato"),
		new ColorPair(new Color(0xFF7F50), "Coral"),
		new ColorPair(new Color(0xFF8C00), "DarkOrange"),
		new ColorPair(new Color(0xFFA500), "Orange"),
		new ColorPair(new Color(0xFFFF00), "Yellow"),
		new ColorPair(new Color(0xFFFFE0), "LightYellow"),
		new ColorPair(new Color(0xFFFACD), "LemonChiffon"),
		new ColorPair(new Color(0xFFEFD5), "PapayaWhip"),
		new ColorPair(new Color(0xFFE4B5), "Moccasin"),
		new ColorPair(new Color(0xFFDAB9), "PeachPuff"),
		new ColorPair(new Color(0xEEE8AA), "PaleGoldenrod"),
		new ColorPair(new Color(0xF0E68C), "Khaki"),
		new ColorPair(new Color(0xBDB76B), "DarkKhaki"),
		new ColorPair(new Color(0xFFD700), "Gold"),
		new ColorPair(new Color(0xFFF8DC), "Cornsilk"),
		new ColorPair(new Color(0xFFEBCD), "BlanchedAlmond"),
		new ColorPair(new Color(0xFFE4C4), "Bisque"),
		new ColorPair(new Color(0xFFDEAD), "NavajoWhite"),
		new ColorPair(new Color(0xF5DEB3), "Wheat"),
		new ColorPair(new Color(0xDEB887), "BurlyWood"),
		new ColorPair(new Color(0xD2B48C), "Tan"),
		new ColorPair(new Color(0xBC8F8F), "RosyBrown"),
		new ColorPair(new Color(0xF4A460), "SandyBrown"),
		new ColorPair(new Color(0xDAA520), "Goldenrod"),
		new ColorPair(new Color(0xB8860B), "DarkGoldenrod"),
		new ColorPair(new Color(0xCD853F), "Peru"),
		new ColorPair(new Color(0xD2691E), "Chocolate"),
		new ColorPair(new Color(0x8B4513), "SaddleBrown"),
		new ColorPair(new Color(0xA0522D), "Sienna"),
		new ColorPair(new Color(0xA52A2A), "Brown"),
		new ColorPair(new Color(0x800000), "Maroon"),
		new ColorPair(new Color(0x556B2F), "DarkOliveGreen"),
		new ColorPair(new Color(0x808000), "Olive"),
		new ColorPair(new Color(0x6B8E23), "OliveDrab"),
		new ColorPair(new Color(0x9ACD32), "YellowGreen"),
		new ColorPair(new Color(0x32CD32), "LimeGreen"),
		new ColorPair(new Color(0x00FF00), "Lime"),
		new ColorPair(new Color(0x7CFC00), "LawnGreen"),
		new ColorPair(new Color(0x7FFF00), "Chartreuse"),
		new ColorPair(new Color(0xADFF2F), "GreenYellow"),
		new ColorPair(new Color(0x00FF7F), "SpringGreen"),
		new ColorPair(new Color(0x00FA9A), "MediumSpringGreen"),
		new ColorPair(new Color(0x90EE90), "LightGreen"),
		new ColorPair(new Color(0x98FB98), "PaleGreen"),
		new ColorPair(new Color(0x8FBC8F), "DarkSeaGreen"),
		new ColorPair(new Color(0x3CB371), "MediumSeaGreen"),
		new ColorPair(new Color(0x2E8B57), "SeaGreen"),
		new ColorPair(new Color(0x228B22), "ForestGreen"),
		new ColorPair(new Color(0x008000), "Green"),
		new ColorPair(new Color(0x006400), "DarkGreen"),
		new ColorPair(new Color(0x66CDAA), "MediumAquamarine"),
		new ColorPair(new Color(0x00FFFF), "Aqua"),
		new ColorPair(new Color(0x00FFFF), "Cyan"),
		new ColorPair(new Color(0xE0FFFF), "LightCyan"),
		new ColorPair(new Color(0xAFEEEE), "PaleTurquoise"),
		new ColorPair(new Color(0x7FFFD4), "Aquamarine"),
		new ColorPair(new Color(0x40E0D0), "Turquoise"),
		new ColorPair(new Color(0x48D1CC), "MediumTurquoise"),
		new ColorPair(new Color(0x00CED1), "DarkTurquoise"),
		new ColorPair(new Color(0x20B2AA), "LightSeaGreen"),
		new ColorPair(new Color(0x5F9EA0), "CadetBlue"),
		new ColorPair(new Color(0x008B8B), "DarkCyan"),
		new ColorPair(new Color(0x008080), "Teal"),
		new ColorPair(new Color(0xB0C4DE), "LightSteelBlue"),
		new ColorPair(new Color(0xB0E0E6), "PowderBlue"),
		new ColorPair(new Color(0xADD8E6), "LightBlue"),
		new ColorPair(new Color(0x87CEEB), "SkyBlue"),
		new ColorPair(new Color(0x87CEFA), "LightSkyBlue"),
		new ColorPair(new Color(0x00BFFF), "DeepSkyBlue"),
		new ColorPair(new Color(0x1E90FF), "DodgerBlue"),
		new ColorPair(new Color(0x6495ED), "CornflowerBlue"),
		new ColorPair(new Color(0x4682B4), "SteelBlue"),
		new ColorPair(new Color(0x4169E1), "RoyalBlue"),
		new ColorPair(new Color(0x0000FF), "Blue"),
		new ColorPair(new Color(0x0000CD), "MediumBlue"),
		new ColorPair(new Color(0x00008B), "DarkBlue"),
		new ColorPair(new Color(0x000080), "Navy"),
		new ColorPair(new Color(0x191970), "MidnightBlue"),
		new ColorPair(new Color(0xE6E6FA), "Lavender"),
		new ColorPair(new Color(0xD8BFD8), "Thistle"),
		new ColorPair(new Color(0xDDA0DD), "Plum"),
		new ColorPair(new Color(0xEE82EE), "Violet"),
		new ColorPair(new Color(0xDA70D6), "Orchid"),
		new ColorPair(new Color(0xFF00FF), "Fuchsia"),
		new ColorPair(new Color(0xFF00FF), "Magenta"),
		new ColorPair(new Color(0xBA55D3), "MediumOrchid"),
		new ColorPair(new Color(0x9370DB), "MediumPurple"),
		new ColorPair(new Color(0x8A2BE2), "BlueViolet"),
		new ColorPair(new Color(0x9400D3), "DarkViolet"),
		new ColorPair(new Color(0x9932CC), "DarkOrchid"),
		new ColorPair(new Color(0x8B008B), "DarkMagenta"),
		new ColorPair(new Color(0x800080), "Purple"),
		new ColorPair(new Color(0x4B0082), "Indigo"),
		new ColorPair(new Color(0x483D8B), "DarkSlateBlue"),
		new ColorPair(new Color(0x6A5ACD), "SlateBlue"),
		new ColorPair(new Color(0x7B68EE), "MediumSlateBlue"),
		new ColorPair(new Color(0xFFFFFF), "White"),
		new ColorPair(new Color(0xFFFAFA), "Snow"),
		new ColorPair(new Color(0xF0FFF0), "Honeydew"),
		new ColorPair(new Color(0xF5FFFA), "MintCream"),
		new ColorPair(new Color(0xF0FFFF), "Azure"),
		new ColorPair(new Color(0xF0F8FF), "AliceBlue"),
		new ColorPair(new Color(0xF8F8FF), "GhostWhite"),
		new ColorPair(new Color(0xF5F5F5), "WhiteSmoke"),
		new ColorPair(new Color(0xFFF5EE), "Seashell"),
		new ColorPair(new Color(0xF5F5DC), "Beige"),
		new ColorPair(new Color(0xFDF5E6), "OldLace"),
		new ColorPair(new Color(0xFFFAF0), "FloralWhite"),
		new ColorPair(new Color(0xFFFFF0), "Ivory"),
		new ColorPair(new Color(0xFAEBD7), "AntiqueWhite"),
		new ColorPair(new Color(0xFAF0E6), "Linen"),
		new ColorPair(new Color(0xFFF0F5), "LavenderBlush"),
		new ColorPair(new Color(0xFFE4E1), "MistyRose"),
		new ColorPair(new Color(0xDCDCDC), "Gainsboro"),
		new ColorPair(new Color(0xD3D3D3), "LightGray"),
		new ColorPair(new Color(0xC0C0C0), "Silver"),
		new ColorPair(new Color(0xA9A9A9), "DarkGray"),
		new ColorPair(new Color(0x808080), "Gray"),
		new ColorPair(new Color(0x696969), "DimGray"),
		new ColorPair(new Color(0x778899), "LightSlateGray"),
		new ColorPair(new Color(0x708090), "SlateGray"),
		new ColorPair(new Color(0x2F4F4F), "DarkSlateGray"),
		new ColorPair(new Color(0x000000), "Black"),
	};
	
}
