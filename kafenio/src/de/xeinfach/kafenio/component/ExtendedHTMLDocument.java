package de.xeinfach.kafenio.component;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.Element;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.HTML;

import javax.swing.undo.UndoableEdit;

import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;

import de.xeinfach.kafenio.util.LeanLogger;

import java.io.IOException;
import java.util.Enumeration;
/**
 * Description: Adds new Features to the standard Java HTMLDocument class.
 * 
 * @author Howard Kistler, Karsten Pawlik
 */
public class ExtendedHTMLDocument extends HTMLDocument {
	
	private static LeanLogger log = new LeanLogger("ExtendedHTMLDocument.class");
	
	/**
	 * Constructs a new ExtendedHTMLDocument using the given values.
	 */
	public ExtendedHTMLDocument() {
		log.debug("new ExtendedHTMLDocument created.");
	}
	
	/**
	 * Constructs a new ExtendedHTMLDocument using the given values.
	 * @param content document content
	 * @param styles document css-styles
	 */
	public ExtendedHTMLDocument(AbstractDocument.Content content, StyleSheet styles) {
		super(content, styles);
	}
	
	/**
	 * Constructs a new ExtendedHTMLDocument using the given values.
	 * @param styles document css-styles.
	 */
	public ExtendedHTMLDocument(StyleSheet styles) {
		super(styles);
	}
	
    /**
     * removes the given element between index and index+count.
     * @param element element to delete
     * @param index text index
     * @param count character count
     * @throws BadLocationException if thrown if index or index+count does not exist.
     */
    public void removeElement(Element element) throws BadLocationException {
        Element parent = element.getParentElement();
        final int index = parent.getElementIndex(element.getStartOffset());
        removeElements(element, index, 1);
    }
    /**
     * removes the given element between index and index+count.
     * @param element element to delete
     * @param index text index
     * @param count character count
     * @throws BadLocationException if thrown if index or index+count does not exist.
     */
    public void removeElements(Element element, int index, int count) throws BadLocationException {
        writeLock();
        int start = element.getElement(index).getStartOffset();
        int end = element.getElement(index + count - 1).getEndOffset();
        try {
            Element[] removed = new Element[count];
            Element[] added = new Element[0];
    
            for (int counter = 0; counter < count; counter++) {
                removed[counter] = element.getElement(counter + index);
            }
    
            DefaultDocumentEvent dde = new DefaultDocumentEvent(start, end - start, DocumentEvent.EventType.REMOVE);
            ((AbstractDocument.BranchElement)element).replace(index, removed.length,added);
            dde.addEdit(new ElementEdit(element, index, removed, added));
            UndoableEdit u = getContent().remove(start, end - start);
    
            if(u != null) {
                dde.addEdit(u);
            }
    
            postRemoveUpdate(dde);
            dde.end();
            fireRemoveUpdate(dde);
    
            if(u != null) {
                fireUndoableEditUpdate(new UndoableEditEvent(this, dde));
            }
        } finally {
            writeUnlock();
        }
    }
     public void replaceHTML(Element firstElement, int number, String htmlText) throws
     BadLocationException, IOException {
         if(number > 1){
             if (firstElement != null && firstElement.getParentElement() != null &&
                     htmlText != null) {
                 int start = firstElement.getStartOffset();
                 Element parent = firstElement.getParentElement();
                 int removeIndex = parent.getElementIndex(start);
                 removeElements(parent, removeIndex, number - 1);
                 setOuterHTML(parent.getElement(removeIndex), htmlText);
             }
         }
         else if (number == 1) {             
             setOuterHTML(firstElement, htmlText);
         }
    }

}