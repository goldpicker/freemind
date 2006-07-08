/*
 * Created on 27.06.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package de.xeinfach.kafenio.component;

import java.io.Writer;

import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLWriter;

public class ExtendedHTMLWriter extends HTMLWriter {

    private ElementIterator it;

    public ExtendedHTMLWriter(Writer w, Element elem) {
        super(w, (HTMLDocument)elem.getDocument());
        it = new ElementIterator(elem);
    }

    public ExtendedHTMLWriter(Writer w, HTMLDocument doc) {
        super(w, doc);
        it = super.getElementIterator();
    }

    protected ElementIterator getElementIterator() {
        return it;
        }

    protected boolean inRange(Element next) {
        return true;
    }

}