/*
GNU Lesser General Public License

HTMLUtilities - Special Utility Functions For Ekit
Copyright (C) 2003 Rafael Cieplinski, modified by Howard Kistler
changes to HTMLUtilities
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
package de.xeinfach.kafenio.component;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import de.xeinfach.kafenio.KafenioPanel;
import de.xeinfach.kafenio.util.LeanLogger;

/**
 * Description: HTMLUtilities - Special Utility Functions For Kafenio 
 * Copyright (C) 2003 Rafael Cieplinski, modified by Howard Kistler
 * @author Copyright (C) 2003 Rafael Cieplinski, modified by Howard Kistler, Karsten Pawlik
 */
public class HTMLUtilities {

	private static LeanLogger log = new LeanLogger("HTMLUtilities.class");
	
	private Hashtable tags = new Hashtable();

    static private HTMLUtilities htmlUtilitiesInstance;

	/**
	 * creates a new HTMLUtilities Object using the given value.
	 * @param newParent a reference to an instance of the KafenioPanel class.
	 */
	private HTMLUtilities() {
		HTML.Tag[] tagList = HTML.getAllTags();
		for(int i = 0; i < tagList.length; i++) {
			tags.put(tagList[i].toString(), tagList[i]);
		}
		log.debug("new HTMLUtilities created.");
	}
    
    static public HTMLUtilities getInstance(){
        if(htmlUtilitiesInstance == null){
            htmlUtilitiesInstance = new HTMLUtilities();
        }
        return htmlUtilitiesInstance;
    }

    public void convertListsToParagraphs(Element firstList, Element lastList, String listName) {
        Element parent = firstList.getParentElement();
        int index = parent.getElementIndex(firstList.getStartOffset());
        Element element;
        do {
            element = parent.getElement(index);
            if(element.getName() == listName){
                convertListToParagraphs(element);
            }
        } while(element != lastList);
    }
    
    public void convertListToParagraphs(Element list) {
        try {
            String newHtml = ""; 
            for(int i = 0; i < list.getElementCount(); i++){
                Element element = list.getElement(i);
                Writer writer = new StringWriter();
                HTMLWriter htmlWriter = new ExtendedHTMLWriter(writer, element);
                htmlWriter.write();
                String text = writer.toString();
                if(element.getName().equals("li")){
                        newHtml += "<p align=\"left\">" + removeTagInfo(text) + "</p>\n";
                }
                else{
                    newHtml += text;
                }
            }
            getExtendedHtmlDoc(list).setOuterHTML(list, newHtml);
//            parentKafenioPanel.getExtendedHtmlDoc().setOuterHTML(element, "<p>\n</p>");
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ExtendedHTMLDocument getExtendedHtmlDoc(Element elem) {
        return ((ExtendedHTMLDocument)elem.getDocument());
    }

    public void insertList(Element item, String listName) {
        try {
            Writer writer = new StringWriter();
            HTMLWriter htmlWriter = new ExtendedHTMLWriter(writer, item);
            htmlWriter.write();
            String text = writer.toString();
            String newHtml = "<" + listName + ">\n<li>" + removeTagInfo(text) + "</li></" + listName + ">\n";
            getExtendedHtmlDoc(item).setInnerHTML(item, newHtml);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    public void convertParagraphsToList(Element firstParagraph, Element lastParagraph, String listName) {
        try {
            String newHtml = ""; 
            Element parent = firstParagraph.getParentElement();
            int index = parent.getElementIndex(firstParagraph.getStartOffset());
            int startIndex = index;
            Element element;
            do {
                element = parent.getElement(index);
                if(element == null){
                    break;
                }
                Writer writer = new StringWriter();
                HTMLWriter htmlWriter = new ExtendedHTMLWriter(writer, element);
                htmlWriter.write();
                String text = writer.toString();
                if(element.getName().equals("p")){
                    newHtml += "<li>" + removeTagInfo(text) + "</li>\n";
                }
                else if((element.getName().equals("ul") || element.getName().equals("ol"))){
                    newHtml += removeTagInfo(text);
                }
                else{
                    newHtml += "</" + listName + ">\n" + text + "<" + listName + ">\n" ;
                }
                index++;
            } while(element != lastParagraph);
            index--;
            newHtml = "<" + listName + ">\n" + newHtml + "</" + listName + ">\n";
            getExtendedHtmlDoc(firstParagraph).replaceHTML(firstParagraph, index - startIndex + 1, newHtml);
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private String removeTagInfo(String text) {
        int first = text.indexOf('>') + 1;
        if(first == -1)
            return text;
        int last = text.lastIndexOf('<') - 1;
        if(last == -1)
            return text;
        if (last <= first)
            return "";        
        return text.substring(first, last);
    }

    /**
	 * Method to check if the given tag is in the hierarchy above the current position or not.
	 * @param tag HTML.Tag to search for.
	 * @return returns true or false. 
	 */
	public boolean checkParentsTag(HTMLDocument doc, HTML.Tag tag, int pos) {
		return getOuterElement(doc, tag,pos) != null;
	}

    public Element getOuterElement(HTMLDocument doc, HTML.Tag tag, int pos) {
        Element e = doc.getParagraphElement(pos);
        String tagString = tag.toString();
        if(e.getName().equalsIgnoreCase(tag.toString())) {
            return e;
        }
        do {
            if((e = e.getParentElement()).getName().equalsIgnoreCase(tagString)) {
                return e;
            }
        } while(!(e.getName().equalsIgnoreCase("html")));
        return null;
    }

	/**
	 * liefert den entsprechenden HTML.Tag zum Element zurück
	 * 
	 * @param elem element to get html tag for
	 * @return returns the element's HTML tag
	 */
	public HTML.Tag getHTMLTag(Element elem) {
		if(tags.containsKey(elem.getName())) {
			return (HTML.Tag)tags.get(elem.getName());
		} else {
			return null;
		}
	}

}