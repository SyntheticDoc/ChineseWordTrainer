/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinesewordtrainer;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Deus
 */
@Getter
@Setter
public class SoundFile {
    private String soundFile;
    private String soundName;
    private String soundTone;
    
    public SoundFile(String soundFile, String soundName, String soundTone) {
	this.soundFile = soundFile;
	this.soundName = soundName;
	this.soundTone = soundTone;
    }
    
    @Override
    public String toString() {
	return "SoundFile[soundFile=" + soundFile + ",soundName=" + soundName + ",soundTone=" + soundTone + "]";
    }
}
