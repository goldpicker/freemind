/*
GNU Lesser General Public License

FormatAction
Copyright (C) 2000-2003 Howard Kistler
changes to FormatAction
Copyright (C) 2003-2004 Karsten Pawlik

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package de.xeinfach.kafenio.action;

import java.awt.event.ActionEvent;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import de.xeinfach.kafenio.KafenioPanel;
import de.xeinfach.kafenio.util.LeanLogger;
/** 
 * Description: Class for implementing HTML formatting actions
 * NOTE : Does not toggle. User must use the "Clear Format" option to remove formatting correctly.
 * 
 * @author Howard Kistler, Karsten Pawlik
 */
public class FormatAction extends StyledEditorKit.StyledTextAction {
	
	private static LeanLogger log = new LeanLogger("FormatAction.class");
	
	private KafenioPanel parentKafenio;
	private HTML.Tag htmlTag;
	private String htmlAttr;
	
	/**
	 * creates a new FormatAction Object based on the given values.
	 * @param kafenio the kafenioPanel object to apply the changes to.
	 * @param actionName the name of the action
	 * @param inTag the HTML-Tag to edit.
	 */
	public FormatAction(KafenioPanel kafenio, String actionName, HTML.Tag inTag) {
		super(actionName);
		parentKafenio = kafenio;
		htmlTag = inTag;
		htmlAttr = null;
		log.debug("new FormatAction created.");
	}
	
	/**
	 * creates a new FormatAction Object based on the given values.
	 * @param kafenio the kafenioPanel object to apply the changes to.
	 * @param actionName the name of the action
	 * @param inTag the HTML-Tag to edit.
	 * @param inAttr attributes for the action.
	 */
	public FormatAction(KafenioPanel kafenio, String actionName, HTML.Tag inTag, String inAttr) {
		super(actionName);
		parentKafenio = kafenio;
		htmlTag = inTag;
		htmlAttr = inAttr;
	}

	/**
	 * handles the performed action.
	 * @param ae the ActionEvent to handle
	 */
	public void actionPerformed(ActionEvent ae) {
		/* Begin Change by fc on 13.06.2006: 
		 * insertion of <p> did not work properly, 
		 * so I removed all this unnecessary selection stuff.*/
		JTextPane parentTextPane = parentKafenio.getTextPane();
		SimpleAttributeSet sasText = new SimpleAttributeSet(parentTextPane
				.getCharacterAttributes());

		SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
		if (htmlAttr != null) {
			simpleAttributeSet.addAttribute(HTML.Attribute.ALIGN, htmlAttr);
		}
		sasText.addAttribute(htmlTag, simpleAttributeSet);
		parentTextPane.setCharacterAttributes(sasText, true);
		/* End Change by fc on 13.06.2006*/
	}
}

