/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinesewordtrainer;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Deus
 */
public class ConsoleOutputStream extends OutputStream {
    private ConsoleHandler c;
    private boolean isErrStream = false;
    
    public ConsoleOutputStream(ConsoleHandler console, boolean isErrStream) {
        c = console;
        this.isErrStream = isErrStream;
    }
    
    @Override
    public void write(int b) throws IOException {
        if(isErrStream) {
            c.cprintErrChar(b);
        } else {
            c.cprintOutChar(b);
        }
    }
}
