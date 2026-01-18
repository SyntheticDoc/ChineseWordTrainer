/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinesewordtrainer;

import java.awt.Color;
import java.util.HashMap;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Deus
 */
public class ConsoleHandler {
    JTextPane console;
    StyledDocument doc;
    Style outStyle;
    Style errStyle;
    
    private final String divider = "-------------------------------------------------------------------------" +
                             "-------------------------------------------------------------------------";
    
    ConsoleHandler(JTextPane console) {
        this.console = console;
        
        doc = console.getStyledDocument();
        
        outStyle = console.addStyle("outStyle", null);
        errStyle = console.addStyle("errStyle", null);
        
        StyleConstants.setForeground(outStyle, Color.black);
        StyleConstants.setForeground(errStyle, Color.red);
    }
    
    public void cPrintHashmap(String hashmapName, HashMap<String, Boolean> hashmap) {
        cprintdiv();
        cprintln("HashMap " + hashmapName + " contains:");
        
        for(String s : hashmap.keySet()) {
            cprintln(s + " = " + hashmap.get(s));
        }
        
        cprintdiv();
    }
    
    public void cprint(String s) {
        try {
	    System.out.print(s);
            doc.insertString(doc.getLength(), s, outStyle);
        } catch(BadLocationException ex) {
            ex.printStackTrace();
        }
        //console.setText(console.getText() + s);
    }
    
    public void cprintln(String s) {
        try {
	    System.out.println(s);
            doc.insertString(doc.getLength(), s + "\n", outStyle);
        } catch(BadLocationException ex) {
            ex.printStackTrace();
        }
        //console.setText(console.getText() + s + "\n");
    }
    
    public void cerrprintln(String s) {
	try {
	    System.err.println(s);
            doc.insertString(doc.getLength(), s + "\n", errStyle);
        } catch(BadLocationException ex) {
            ex.printStackTrace();
        }
    }
    
    public void cprintdiv() {
        try {
            doc.insertString(doc.getLength(), divider + "\n", outStyle);
        } catch(BadLocationException ex) {
            ex.printStackTrace();
        }
        //console.setText(console.getText() + divider + "\n");
    }
    
    public void cprintOutChar(int c) {
        try {
	    System.out.print(c);
            doc.insertString(doc.getLength(), String.valueOf((char) c), outStyle);
        } catch(BadLocationException ex) {
            ex.printStackTrace();
        }
    }
    
    public void cprintErrChar(int c) {
        try {
	    System.err.print(c);
            doc.insertString(doc.getLength(), String.valueOf((char) c), errStyle);
        } catch(BadLocationException ex) {
            ex.printStackTrace();
        }
    }
    
    public void logMsg(String msg) {
	System.out.println(msg);
	cprintln(msg);
    }
    
    public void logErr(String err) {
	System.err.println(err);
	cerrprintln(err);
    }
}
