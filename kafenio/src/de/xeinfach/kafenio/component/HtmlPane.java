/*
 * Created on 24.06.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package de.xeinfach.kafenio.component;

import java.awt.event.KeyEvent;

import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import de.xeinfach.kafenio.KafenioPanel;

public class HtmlPane extends JTextPane {
    KafenioPanel kafenioPanel;
    public HtmlPane(KafenioPanel kafenioPanel) {
        super();
        this.kafenioPanel = kafenioPanel;
    }
    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
        if(e.getID() == KeyEvent.KEY_PRESSED && kafenioPanel.keyPressed(e)){
            return true;
        }
        return super.processKeyBinding(ks, e, condition, pressed);
    }
    public void setCaretPosition(int position) {
        if(position == 0 && getDocument().getLength() > 0) {
            super.setCaretPosition(1);
        }
        else{
            super.setCaretPosition(position);
        }
    }
    public int getCaretPosition() {
        // TODO Auto-generated method stub
        return super.getCaretPosition();
    }
}
