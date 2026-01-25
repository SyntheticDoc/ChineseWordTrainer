/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinesewordtrainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Deus
 *
 * Weighted shuffle after Efraimidis-Spirakis
 *
 * Beta as bias exponent, expected results of values: Beta = 0: standard shuffle, completely ignores weights Beta = 0.5: slightly prefers smaller weights Beta = 1.0: standard weighted shuffle, prefers
 * smaller weights Beta = 2 - Beta = 5: heavily prefers smaller weights Beta = 10+: practically sorted by weight, with maybe minimal randomness
 */
public class WeightedShuffle {

    private ConsoleHandler console;
    private boolean isDevMode;

    public WeightedShuffle(ConsoleHandler console, boolean isDevMode) {
	this.console = console;
	this.isDevMode = isDevMode;
    }

    public void interleavingShuffle(List<Word> words, double beta, LearningMode lm) {
	interleavingShuffle(words, new Random(), beta, lm);
    }
    
    public void interleavingShuffle(List<Word> words, Random rnd, double beta, LearningMode lm) {
	if (words.size() <= 1) {
	    return;
	}

	// 1) split  word lists into known/unknown
	List<Word> known = new ArrayList<>();
	List<Word> unknown = new ArrayList<>();

	for (Word w : words) {
	    double d = w.getDifficulty(lm);
	    
	    if (d <= 0.0) {
		unknown.add(w);
	    } else {
		known.add(w);
	    }
	}

	// 2) word unknown (not yet tested): pure random
	Collections.shuffle(unknown, rnd);

	// 3) word known: weighted ordering
	if (beta <= 0.0) {
	    Collections.shuffle(known, rnd);
	} else {
	    Map<Word, Double> keys = new IdentityHashMap<>(known.size());
	    
	    for (Word w : known) {
		keys.put(w, weightedKey(w.getDifficulty(lm), rnd, beta));
	    }
	    
	    known.sort(Comparator.comparingDouble(keys::get));
	}

	// 4) merge word lists: random slot pattern over full list
	int n = words.size();
	int u = unknown.size();

	List<Boolean> slots = new ArrayList<>(n);
	
	for (int i = 0; i < u; i++) {
	    slots.add(true);        // true = take unknown
	}
	
	for (int i = 0; i < n - u; i++) {
	    slots.add(false);   // false = take known
	}
	Collections.shuffle(slots, rnd);

	// rebuild original list
	words.clear();
	
	int ik = 0, iu = 0;
	
	for (boolean takeUnknown : slots) {
	    if (takeUnknown) {
		words.add(unknown.get(iu++));
	    } else {
		words.add(known.get(ik++));
	    }
	}
    }

    public void shuffle(List<Word> words, double beta, LearningMode lm) {
	shuffle(words, new Random(), beta, lm);
    }

    public void shuffle(List<Word> words, Random rnd, double beta, LearningMode lm) {

	if (beta <= 0.0) {
	    Collections.shuffle(words, rnd);
	    return; // <-- wichtig!
	}

	// Key genau einmal pro Word berechnen (deterministisch für diesen Sort-Vorgang)
	Map<Word, Double> keys = new IdentityHashMap<>(words.size());
	for (Word w : words) {
	    keys.put(w, weightedKey(w.getDifficulty(lm), rnd, beta));
	}

	words.sort(Comparator.comparingDouble(keys::get));

	if (isDevMode) {
	    console.logMsg("WeightedShuffle: Shuffling list with beta=" + beta);
	    console.logMsg("Shuffle result: ");

	    for (int i = 0; i < Math.min(30, words.size()); i++) {
		Word w = words.get(i);
		console.logMsg("  " + w.getCSVString());
	    }
	}
    }

    public void shuffle_old(List<Word> words, Random rnd, double beta, LearningMode lm) {
	if (beta <= 0.0) {
	    // unbiased shuffle
	    Collections.shuffle(words, rnd);
	}

	words.sort(Comparator.comparingDouble(w -> weightedKey(w.getDifficulty(lm), rnd, beta)));

	if (isDevMode) {
	    console.logMsg("WeightedShuffle: Shuffling list with beta=" + beta);

	    console.logMsg("Shuffle result: ");

	    for (int i = 0; i < 30; i++) {
		Word w = words.get(i);
		console.logMsg("  " + w.getCSVString());
	    }
	}
    }

    private double weightedKey(double weight, Random rnd, double beta) {
	final double EPS = 1e-12;

	if (Double.isNaN(weight) || weight <= 0.0) {
	    return Double.POSITIVE_INFINITY;
	}

	double wEff = Math.pow(Math.max(weight, EPS), beta);
	double u = 1.0 - rnd.nextDouble();
	return -Math.log(u) / wEff;
    }

    private double weightedKey_old(double weight, Random rnd, double beta) {
	final double EPS = 1e-12; // Verhindert log-explosion und Fehler mit weight = 0

	// Je größer weight, desto weiter hinten in Liste
	if (weight <= 0.0) {
	    return Double.POSITIVE_INFINITY;
	}

	double wEff = Math.pow(Math.max(weight, EPS), beta);

	// U in (0,1], 0 exkludiert wg. log(0)
	double u = 1.0 - rnd.nextDouble();
	return -Math.log(u) / wEff;
    }
}
