/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinesewordtrainer;

import java.io.InputStream;
import javazoom.jl.player.Player;

/**
 *
 * @author Deus
 */
public class SoundPlayer {
    private ConsoleHandler console;
    
    public SoundPlayer(ConsoleHandler console) {
	this.console = console;
    }
    
    public void play(String resourcePath) {
        try {
            InputStream in = getClass().getResourceAsStream(resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }

            Player player = new Player(in);   // JLayer-Player
            player.play();                    // Blockiert, bis MP3 fertig

        } catch (Exception e) {
            e.printStackTrace();
	    console.cerrprintln("Could not play sound file " + resourcePath + ": " + e.getMessage());
        }
    }
}
