/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinesewordtrainer;

import static com.chinesewordtrainer.LearningMode.*;
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
    int numWrong_simplified_hanzi;
    int numCorrect_simplified_hanzi;
    double difficulty_simplified_hanzi;
    int numWrong_traditional_hanzi;
    int numCorrect_traditional_hanzi;
    double difficulty_traditional_hanzi;
    int numWrong_pinyin;
    int numCorrect_pinyin;
    double difficulty_pinyin;
    int numWrong_translate_de_zh;
    int numCorrect_translate_de_zh;
    double difficulty_translate_de_zh;
    int numWrong_translate_zh_de;
    int numCorrect_translate_zh_de;
    double difficulty_translate_zh_de;
    static int statFieldCount = 15;
    
    public Word(String guid, String simpleHanzi, String traditionalHanzi, String pinyin, String translation, String examples, String notes, String lesson, String standardPronounciation, 
	    boolean createFindRadical, boolean createFindTraditionalRadical, boolean createWriteSimpleHanzi, String Zhuyin, boolean createReadTraditionalHanzi, 
	    boolean createWriteTraditionalHanzi, String learningStats) throws IllegalArgumentException {
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
	
	String[] learningStats_parts = learningStats.replace("[", "").replace("]", "").split("|");
	
	if(learningStats_parts.length != statFieldCount) {
	    throw new IllegalArgumentException("ERROR in converting stat fields in Word constructor, possible program version mismatch. Expected: " + statFieldCount + " parts, instead extracted: " + learningStats_parts.length + " parts. "
	    + " learningStats String: " + learningStats);
	}
	
	String[] ls_1 = learningStats_parts[0].split("-");
	
	numWrong_simplified_hanzi = Integer.parseInt(ls_1[0]);
	numCorrect_simplified_hanzi = Integer.parseInt(ls_1[1]);
	recalcDifficulty(SIMPLIFIED_HANZI);
	
	String[] ls_2 = learningStats_parts[1].split("-");
	
	numWrong_traditional_hanzi = Integer.parseInt(ls_2[0]);
	numCorrect_traditional_hanzi = Integer.parseInt(ls_2[1]);
	recalcDifficulty(TRADITIONAL_HANZI);
	
	String[] ls_3 = learningStats_parts[2].split("-");
	
	numWrong_pinyin = Integer.parseInt(ls_3[0]);
	numCorrect_pinyin = Integer.parseInt(ls_3[1]);
	recalcDifficulty(PINYIN);
	
	String[] ls_4 = learningStats_parts[3].split("-");
	
	numWrong_translate_de_zh = Integer.parseInt(ls_4[0]);
	numCorrect_translate_de_zh = Integer.parseInt(ls_4[1]);
	recalcDifficulty(TRANSLATE_DE_ZH);
	
	String[] ls_5 = learningStats_parts[4].split("-");
	
	numWrong_translate_zh_de = Integer.parseInt(ls_5[0]);
	numCorrect_translate_zh_de = Integer.parseInt(ls_5[1]);
	recalcDifficulty(TRANSLATE_ZH_DE);
    }
    
    private void recalcDifficulty(LearningMode lm) {
	switch(lm) {
	    case SIMPLIFIED_HANZI:
		if((numCorrect_simplified_hanzi + numWrong_simplified_hanzi) == 0) {
		    difficulty_simplified_hanzi = 0;
		} else {
		    difficulty_simplified_hanzi = numWrong_simplified_hanzi / (double) (numCorrect_simplified_hanzi + numWrong_simplified_hanzi);
		}
		break;
	    case TRADITIONAL_HANZI:
		if((numCorrect_traditional_hanzi + numWrong_traditional_hanzi) == 0) {
		    difficulty_traditional_hanzi = 0;
		} else {
		    difficulty_traditional_hanzi = numWrong_traditional_hanzi / (double) (numCorrect_traditional_hanzi + numWrong_traditional_hanzi);
		}
		break;
	    case PINYIN:
		if((numCorrect_pinyin + numWrong_pinyin) == 0) {
		    difficulty_pinyin = 0;
		} else {
		    difficulty_pinyin = numWrong_pinyin / (double) (numCorrect_pinyin + numWrong_pinyin);
		}
		break;
	    case TRANSLATE_DE_ZH:
		if((numCorrect_translate_de_zh + numWrong_translate_de_zh) == 0) {
		    difficulty_translate_de_zh = 0;
		} else {
		    difficulty_translate_de_zh = numWrong_translate_de_zh / (double) (numCorrect_translate_de_zh + numWrong_translate_de_zh);
		}
		break;
	    case TRANSLATE_ZH_DE:
		if((numCorrect_translate_zh_de + numWrong_translate_zh_de) == 0) {
		    difficulty_translate_zh_de = 0;
		} else {
		    difficulty_translate_zh_de = numWrong_translate_zh_de / (double) (numCorrect_translate_zh_de + numWrong_translate_zh_de);
		}
		break;
	}
	
    }
    
    public void setNumWrong_simplified_hanzi(int numWrong_simplified_hanzi) {
	this.numWrong_simplified_hanzi = numWrong_simplified_hanzi;
	recalcDifficulty(SIMPLIFIED_HANZI);
    }
    
    public void setNumCorrect_simplified_hanzi(int numCorrect_simplified_hanzi) {
	this.numCorrect_simplified_hanzi = numCorrect_simplified_hanzi;
	recalcDifficulty(SIMPLIFIED_HANZI);
    }
    
    public void setNumWrong_traditional_hanzi(int numWrong_traditional_hanzi) {
	this.numWrong_traditional_hanzi = numWrong_traditional_hanzi;
	recalcDifficulty(TRADITIONAL_HANZI);
    }
    
    public void setNumCorrect_traditional_hanzi(int numCorrect_traditional_hanzi) {
	this.numCorrect_traditional_hanzi = numCorrect_traditional_hanzi;
	recalcDifficulty(TRADITIONAL_HANZI);
    }
    
    public void setNumWrong_pinyin(int numWrong_pinyin) {
	this.numWrong_pinyin = numWrong_pinyin;
	recalcDifficulty(PINYIN);
    }
    
    public void setNumCorrect_pinyin(int numCorrect_pinyin) {
	this.numCorrect_pinyin = numCorrect_pinyin;
	recalcDifficulty(PINYIN);
    }
    
    public void setNumWrong_translate_de_zh(int numWrong_translate_de_zh) {
	this.numWrong_translate_de_zh = numWrong_translate_de_zh;
	recalcDifficulty(TRANSLATE_DE_ZH);
    }
    
    public void setNumCorrect_translate_de_zh(int numCorrect_translate_de_zh) {
	this.numCorrect_translate_de_zh = numCorrect_translate_de_zh;
	recalcDifficulty(TRANSLATE_DE_ZH);
    }
    
    public void setNumWrong_translate_zh_de(int numWrong_translate_zh_de) {
	this.numWrong_translate_zh_de = numWrong_translate_zh_de;
	recalcDifficulty(TRANSLATE_ZH_DE);
    }
    
    public void setNumCorrect_translate_zh_de(int numCorrect_translate_zh_de) {
	this.numCorrect_translate_zh_de = numCorrect_translate_zh_de;
	recalcDifficulty(TRANSLATE_ZH_DE);
    }
    
    public double getDifficulty(LearningMode lm) {
	switch(lm) {
	    case SIMPLIFIED_HANZI:
		return difficulty_simplified_hanzi;
	    case TRADITIONAL_HANZI:
		return difficulty_traditional_hanzi;
	    case PINYIN:
		return difficulty_pinyin;
	    case TRANSLATE_DE_ZH:
		return difficulty_translate_de_zh;
	    case TRANSLATE_ZH_DE:
		return difficulty_translate_zh_de;
	    default:
		return 0.0d;
	}
    }
    
    public String getCSVString() {
	return simpleHanzi + ";" + traditionalHanzi + ";" + pinyin + ";\"" + translation + "\";" + lesson + ";" + standardPronounciation + ";" + Zhuyin + ";"  + "[" + numWrong_simplified_hanzi + "-" + numCorrect_simplified_hanzi 
		+ "|" + numWrong_traditional_hanzi + "-" + numCorrect_traditional_hanzi 
		+ "|" + numWrong_pinyin + "-" + numCorrect_pinyin 
		+ "|" + numWrong_translate_de_zh + "-" + numCorrect_translate_de_zh
		+ "|" + numWrong_translate_zh_de + "-" + numCorrect_translate_zh_de
		+ "]";
    }
    
    public String getCSVString_old() {
	return guid + ";" + simpleHanzi + ";" + traditionalHanzi + ";" + pinyin + ";\"" + translation + "\";" + examples + ";" + notes + ";" + lesson + ";" + standardPronounciation + ";" + 
		createFindRadical + ";" + createFindTraditionalRadical + ";" + createWriteSimpleHanzi + ";" + Zhuyin + ";" + createReadTraditionalHanzi + ";" + createWriteTraditionalHanzi + ";" + 
		numWrong_simplified_hanzi + ";" + numCorrect_simplified_hanzi + ";";
    }
}
