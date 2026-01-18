/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinesewordtrainer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Deus
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Word {
    String guid;
    String simpleHanzi;
    String traditionalHanzi;
    String pinyin;
    String translation;
    String examples;
    String notes;
    String lesson;
    String standardPronounciation;
    boolean createFindRadical;
    boolean createFindTraditionalRadical;
    boolean createWriteSimpleHanzi;
    String Zhuyin;
    boolean createReadTraditionalHanzi;
    boolean createWriteTraditionalHanzi;
    int timesGottenWrong;
    int timesGottenRight;
    double difficulty;
    
    public Word(String guid, String simpleHanzi, String traditionalHanzi, String pinyin, String translation, String examples, String notes, String lesson, String standardPronounciation, 
	    boolean createFindRadical, boolean createFindTraditionalRadical, boolean createWriteSimpleHanzi, String Zhuyin, boolean createReadTraditionalHanzi, 
	    boolean createWriteTraditionalHanzi, int timesGottenWrong, int timesGottenRight) {
	this.guid = guid;
	this.simpleHanzi = simpleHanzi;
	this.traditionalHanzi = traditionalHanzi;
	this.pinyin = pinyin;
	this.translation = translation;
	this.examples = examples;
	this.notes = notes;
	this.lesson = lesson;
	this.standardPronounciation = standardPronounciation;
	this.createFindRadical = createFindRadical;
	this.createFindTraditionalRadical = createFindTraditionalRadical;
	this.createWriteSimpleHanzi = createWriteSimpleHanzi;
	this.Zhuyin = Zhuyin;
	this.createReadTraditionalHanzi = createReadTraditionalHanzi;
	this.createWriteTraditionalHanzi = createWriteTraditionalHanzi;
	this.timesGottenWrong = timesGottenWrong;
	this.timesGottenRight = timesGottenRight;
	recalcDifficulty();
    }
    
    private void recalcDifficulty() {
	if((timesGottenRight + timesGottenWrong) == 0) {
	    difficulty = 0;
	} else {
	    difficulty = timesGottenWrong / (double) (timesGottenRight + timesGottenWrong);
	}
    }
    
    public void setTimesGottenWrong(int timesGottenWrong) {
	this.timesGottenWrong = timesGottenWrong;
	recalcDifficulty();
    }
    
    public void setTimesGottenRight(int timesGottenRight) {
	this.timesGottenRight = timesGottenRight;
	recalcDifficulty();
    }
    
    public String getCSVString() {
	return guid + ";" + simpleHanzi + ";" + traditionalHanzi + ";" + pinyin + ";\"" + translation + "\";" + examples + ";" + notes + ";" + lesson + ";" + standardPronounciation + ";" + 
		createFindRadical + ";" + createFindTraditionalRadical + ";" + createWriteSimpleHanzi + ";" + Zhuyin + ";" + createReadTraditionalHanzi + ";" + createWriteTraditionalHanzi + ";" + 
		timesGottenWrong + ";" + timesGottenRight + ";";
    }
}
