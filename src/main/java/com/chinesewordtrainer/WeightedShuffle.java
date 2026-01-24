/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinesewordtrainer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Deus
 * 
 * Weighted shuffle after Efraimidis-Spirakis
 * 
 * Beta as bias exponent, expected results of values:
 *   Beta = 0: standard shuffle, completely ignores weights
 *   Beta = 0.5: slightly prefers smaller weights
 *   Beta = 1.0: standard weighted shuffle, prefers smaller weights
 *   Beta = 2 - Beta = 5: heavily prefers smaller weights
 *   Beta = 10+: practically sorted by weight, with maybe minimal randomness
 */
public class WeightedShuffle {
    private ConsoleHandler console;
    private boolean isDevMode;
    
    public WeightedShuffle(ConsoleHandler console, boolean isDevMode) {
	this.console = console;
	this.isDevMode = isDevMode;
    }
    
    public void shuffle(List<Word> words, double beta, LearningMode lm) {
	shuffle(words, new Random(), beta, lm);
    }
    
    public void shuffle(List<Word> words, Random rnd, double beta, LearningMode lm) {
	if (beta <= 0.0) {
	    // unbiased shuffle
	    Collections.shuffle(words, rnd);
	}
	
	words.sort(Comparator.comparingDouble(w -> weightedKey(w.getDifficulty(lm), rnd, beta)));
	
	if(isDevMode) {
	    console.logMsg("WeightedShuffle: Shuffling list with beta=" + beta);
	    
	    console.logMsg("Shuffle result: ");
	    
	    for(int i = 0; i < 30; i++) {
		Word w = words.get(i);
		console.logMsg("  " + w.getCSVString());
	    }
	}
    }
    
    private double weightedKey(double weight, Random rnd, double beta) {
	final double EPS = 1e-12; // Verhindert log-explosion und Fehler mit weight = 0
	
	// Je größer weight, desto weiter hinten in Liste
	
	if(weight <= 0.0) {
	    return Double.POSITIVE_INFINITY;
	}
	
	double wEff = Math.pow(Math.max(weight, EPS), beta);
	
	// U in (0,1], 0 exkludiert wg. log(0)
	double u = 1.0 - rnd.nextDouble();
	return -Math.log(u) / wEff;
    }
}
