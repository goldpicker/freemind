/*
GNU Lesser General Public License

ListAutomationAction
Copyright (C) 2000-2003 Howard Kistler
changes to ListAutomationAction
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
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.StringTokenizer;

import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;

import de.xeinfach.kafenio.KafenioPanel;
import de.xeinfach.kafenio.component.HTMLUtilities;
import de.xeinfach.kafenio.component.dialogs.SimpleInfoDialog;
import de.xeinfach.kafenio.util.LeanLogger;

/** 
 * Description: Class for automatically creating bulleted lists from selected text
 * 
 * @author HowardKistler, Karsten Pawlik
 */
public class ListAutomationAction extends HTMLEditorKit.InsertHTMLTextAction {

	private static LeanLogger log = new LeanLogger("ListAutomationAction.class");
	
	private KafenioPanel parentKafenioPanel;
	private HTML.Tag baseTag;

	/**
	 * creates a new ListAutomationAction using the given parameters.
	 * @param kafenio a KafenioPanel instance
	 * @param sLabel a list label
	 * @param newListType type of the list (OL or UL)
	 */
	public ListAutomationAction(KafenioPanel kafenio, String sLabel, HTML.Tag newListType) {
		super(sLabel, "", newListType, HTML.Tag.LI);
		parentKafenioPanel = kafenio;
		baseTag    = newListType;
		log.debug("created new ListAutomationAction.");
	}

	/**
     * method that handles the given ActionEvent
     * @param ae the ActionEvent to handle
     */
    public void actionPerformed(ActionEvent ae) {
    	JEditorPane jepEditor = (parentKafenioPanel.getTextPane());
        HTMLDocument htmlDoc = (HTMLDocument)(jepEditor.getDocument());
        final int selectionStart = jepEditor.getSelectionStart();
        final int selectionEnd = jepEditor.getSelectionEnd();
        Element item = HTMLUtilities.getInstance().getOuterElement(htmlDoc, HTML.Tag.LI, selectionStart);            
        if(item != null){ 
            Element listRootElement = item.getParentElement();
            int listStartOffset = item.getStartOffset();
            int listEndOffset = item.getEndOffset();
            if(selectionStart >= listStartOffset && selectionEnd < listEndOffset){
                if (listRootElement.getName() == baseTag.toString()){
                    HTMLUtilities.getInstance().convertListToParagraphs(listRootElement);
                }
                else{
                    HTMLUtilities.getInstance().convertParagraphsToList(listRootElement, listRootElement, baseTag.toString());
                }
                return;
            }
        }
        if(item != null) {
            item = item.getParentElement();
        }
        else{
            item = HTMLUtilities.getInstance().getOuterElement(htmlDoc, HTML.Tag.P, selectionStart);
        }
        if(item == null)
            return;
        Element first = item;
        final Element listParent = item.getParentElement();
        int index = listParent.getElementIndex(item.getStartOffset());
        do{
            item = listParent.getElement(++index);
        }
        while(item != null && item.getStartOffset() <= selectionEnd);
        --index;
        item = listParent.getElement(index);
        HTMLUtilities.getInstance().convertParagraphsToList(first, item, baseTag.toString());
    }

	/**
	 * returns a translated representation of the given string.
	 * @param stringToTranslate the string to translate.
	 * @return returns a translated representation of the given string.
	 */
	private String getString(String stringToTranslate) {
		return parentKafenioPanel.getTranslation(stringToTranslate);
	}
	
}