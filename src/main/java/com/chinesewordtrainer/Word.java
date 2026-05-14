/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinesewordtrainer;

import static com.chinesewordtrainer.LearningMode.*;
import java.util.regex.Pattern;
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
    int numWrong_tingxie;
    int numCorrect_tingxie;
    double difficulty_tingxie;
    static int statFieldCount = 18;

    public Word(String simpleHanzi, String traditionalHanzi, String pinyin, String translation, String lesson, String standardPronounciation, String zhuyin, String learningStats) throws IllegalArgumentException {
	this.simpleHanzi = simpleHanzi;
	this.traditionalHanzi = traditionalHanzi;
	this.pinyin = pinyin;
	this.translation = translation;
	this.lesson = lesson;
	this.standardPronounciation = standardPronounciation;

	parseLearningStats(learningStats);
    }

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

	parseLearningStats(learningStats);
    }

    private void parseLearningStats(String learningStats) {
	String[] parts = learningStats.replace("[", "").replace("]", "").split(Pattern.quote("|"));

	// Backward compatibility: old files have only 5 learning modes.
	if (parts.length == 5) {
	    String[] extended = new String[6];
	    System.arraycopy(parts, 0, extended, 0, parts.length);
	    extended[5] = "0-0"; // Tingxie starts fresh.
	    parts = extended;
	}

	if (parts.length != 6) {
	    throw new IllegalArgumentException(
		    "ERROR in converting stat fields in Word constructor, possible program version mismatch. "
		    + "Expected 6 parts, instead extracted: " + parts.length
		    + ". learningStats String: " + learningStats
	    );
	}

	int[] s1 = parseStatPair(parts[0]);
	numWrong_simplified_hanzi = s1[0];
	numCorrect_simplified_hanzi = s1[1];
	recalcDifficulty(SIMPLIFIED_HANZI);

	int[] s2 = parseStatPair(parts[1]);
	numWrong_traditional_hanzi = s2[0];
	numCorrect_traditional_hanzi = s2[1];
	recalcDifficulty(TRADITIONAL_HANZI);

	int[] s3 = parseStatPair(parts[2]);
	numWrong_pinyin = s3[0];
	numCorrect_pinyin = s3[1];
	recalcDifficulty(PINYIN);

	int[] s4 = parseStatPair(parts[3]);
	numWrong_translate_de_zh = s4[0];
	numCorrect_translate_de_zh = s4[1];
	recalcDifficulty(TRANSLATE_DE_ZH);

	int[] s5 = parseStatPair(parts[4]);
	numWrong_translate_zh_de = s5[0];
	numCorrect_translate_zh_de = s5[1];
	recalcDifficulty(TRANSLATE_ZH_DE);

	int[] s6 = parseStatPair(parts[5]);
	numWrong_tingxie = s6[0];
	numCorrect_tingxie = s6[1];
	recalcDifficulty(TINGXIE);
    }

    private int[] parseStatPair(String statPart) {
	String[] split = statPart.split("-");
	if (split.length != 2) {
	    throw new IllegalArgumentException("Invalid stat pair: " + statPart);
	}

	return new int[]{
	    Integer.parseInt(split[0]),
	    Integer.parseInt(split[1])
	};
    }

    private void recalcDifficulty(LearningMode lm) {
	switch (lm) {
	    case SIMPLIFIED_HANZI:
		if ((numCorrect_simplified_hanzi + numWrong_simplified_hanzi) == 0) {
		    difficulty_simplified_hanzi = 0;
		} else {
		    difficulty_simplified_hanzi = numWrong_simplified_hanzi / (double) (numCorrect_simplified_hanzi + numWrong_simplified_hanzi);

		    if (difficulty_simplified_hanzi < (1d / 100000d)) {
			difficulty_simplified_hanzi = 1d / 100000d;
		    }
		}
		break;
	    case TRADITIONAL_HANZI:
		if ((numCorrect_traditional_hanzi + numWrong_traditional_hanzi) == 0) {
		    difficulty_traditional_hanzi = 0;
		} else {
		    difficulty_traditional_hanzi = numWrong_traditional_hanzi / (double) (numCorrect_traditional_hanzi + numWrong_traditional_hanzi);

		    if (difficulty_traditional_hanzi < (1d / 100000d)) {
			difficulty_traditional_hanzi = 1d / 100000d;
		    }
		}
		break;
	    case PINYIN:
		if ((numCorrect_pinyin + numWrong_pinyin) == 0) {
		    difficulty_pinyin = 0;
		} else {
		    difficulty_pinyin = numWrong_pinyin / (double) (numCorrect_pinyin + numWrong_pinyin);

		    if (difficulty_pinyin < (1d / 100000d)) {
			difficulty_pinyin = 1d / 100000d;
		    }
		}
		break;
	    case TRANSLATE_DE_ZH:
		if ((numCorrect_translate_de_zh + numWrong_translate_de_zh) == 0) {
		    difficulty_translate_de_zh = 0;
		} else {
		    difficulty_translate_de_zh = numWrong_translate_de_zh / (double) (numCorrect_translate_de_zh + numWrong_translate_de_zh);

		    if (difficulty_translate_de_zh < (1d / 100000d)) {
			difficulty_translate_de_zh = 1d / 100000d;
		    }
		}
		break;
	    case TRANSLATE_ZH_DE:
		if ((numCorrect_translate_zh_de + numWrong_translate_zh_de) == 0) {
		    difficulty_translate_zh_de = 0;
		} else {
		    difficulty_translate_zh_de = numWrong_translate_zh_de / (double) (numCorrect_translate_zh_de + numWrong_translate_zh_de);

		    if (difficulty_translate_zh_de < (1d / 100000d)) {
			difficulty_translate_zh_de = 1d / 100000d;
		    }
		}
		break;
	    case TINGXIE:
		if ((numCorrect_tingxie + numWrong_tingxie) == 0) {
		    difficulty_tingxie = 0;
		} else {
		    difficulty_tingxie = numWrong_tingxie / (double) (numCorrect_tingxie + numWrong_tingxie);

		    if (difficulty_tingxie < (1d / 100000d)) {
			difficulty_tingxie = 1d / 100000d;
		    }
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

    public void setNumWrong_tingxie(int numWrong_tingxie) {
	this.numWrong_tingxie = numWrong_tingxie;
	recalcDifficulty(TINGXIE);
    }

    public void setNumCorrect_tingxie(int numCorrect_tingxie) {
	this.numCorrect_tingxie = numCorrect_tingxie;
	recalcDifficulty(TINGXIE);
    }

    public double getDifficulty(LearningMode lm) {
	switch (lm) {
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
	    case TINGXIE:
		return difficulty_tingxie;
	    default:
		return 0.0d;
	}
    }

    public void resetWordStats() {
	numWrong_simplified_hanzi = 0;
	numCorrect_simplified_hanzi = 0;
	numWrong_traditional_hanzi = 0;
	numCorrect_traditional_hanzi = 0;
	numWrong_pinyin = 0;
	numCorrect_pinyin = 0;
	numWrong_translate_de_zh = 0;
	numCorrect_translate_de_zh = 0;
	numWrong_translate_zh_de = 0;
	numCorrect_translate_zh_de = 0;
	numWrong_tingxie = 0;
	numCorrect_tingxie = 0;
	recalcDifficulty(SIMPLIFIED_HANZI);
	recalcDifficulty(TRADITIONAL_HANZI);
	recalcDifficulty(PINYIN);
	recalcDifficulty(TRANSLATE_DE_ZH);
	recalcDifficulty(TRANSLATE_ZH_DE);
	recalcDifficulty(TINGXIE);
    }

    public String getCSVString() {
	return simpleHanzi + ";" + traditionalHanzi + ";" + pinyin + ";\"" + translation + "\";" + lesson + ";" + standardPronounciation + ";" + Zhuyin + ";"
		+ "[" + numWrong_simplified_hanzi + "-" + numCorrect_simplified_hanzi
		+ "|" + numWrong_traditional_hanzi + "-" + numCorrect_traditional_hanzi
		+ "|" + numWrong_pinyin + "-" + numCorrect_pinyin
		+ "|" + numWrong_translate_de_zh + "-" + numCorrect_translate_de_zh
		+ "|" + numWrong_translate_zh_de + "-" + numCorrect_translate_zh_de
		+ "|" + numWrong_tingxie + "-" + numCorrect_tingxie
		+ "]";
    }

    public String getCSVString_old() {
	return guid + ";" + simpleHanzi + ";" + traditionalHanzi + ";" + pinyin + ";\"" + translation + "\";" + examples + ";" + notes + ";" + lesson + ";" + standardPronounciation + ";"
		+ createFindRadical + ";" + createFindTraditionalRadical + ";" + createWriteSimpleHanzi + ";" + Zhuyin + ";" + createReadTraditionalHanzi + ";" + createWriteTraditionalHanzi + ";"
		+ numWrong_simplified_hanzi + ";" + numCorrect_simplified_hanzi + ";";
    }
}
