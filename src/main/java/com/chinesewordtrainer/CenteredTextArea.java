/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinesewordtrainer;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;

/**
 *
 * @author Deus
 */
public class CenteredTextArea extends JTextArea {

    public CenteredTextArea() {
	super(1, 1); // verhindert "explodierende" preferred size
	setEditable(false);
	setFocusable(false);
	setOpaque(true);

	// Diese Flags sind optional, aber sinnvoll fürs "TextArea-Gefühl"
	setLineWrap(true);
	setWrapStyleWord(true);
    }

    private static class Line {

	TextLayout layout; // null = leere Zeile
	float height;
    }

    @Override
    protected void paintComponent(Graphics g) {

	// Hintergrund
	if (isOpaque()) {
	    g.setColor(getBackground());
	    g.fillRect(0, 0, getWidth(), getHeight());
	}

	// Border (falls vorhanden)
	paintBorder(g);

	Graphics2D g2 = (Graphics2D) g.create();
	try {
	    g2.setFont(getFont());
	    g2.setColor(getForeground());
	    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

	    Insets ins = getInsets();
	    float availW = getWidth() - ins.left - ins.right;
	    float availH = getHeight() - ins.top - ins.bottom;

	    if (availW <= 1 || availH <= 1) {
		return;
	    }

	    String text = getText();
	    if (text == null) {
		text = "";
	    }

	    FontRenderContext frc = g2.getFontRenderContext();
	    FontMetrics fm = g2.getFontMetrics(getFont());

	    // Text in "Umbruch-Zeilen" zerlegen
	    List<Line> lines = new ArrayList<>();

	    String[] paragraphs = text.split("\n", -1);

	    for (String p : paragraphs) {
		if (p.isEmpty()) {
		    // echte Leerzeile
		    Line ln = new Line();
		    ln.layout = null;
		    ln.height = fm.getHeight();
		    lines.add(ln);
		    continue;
		}

		AttributedString as = new AttributedString(p);
		as.addAttribute(TextAttribute.FONT, getFont());
		AttributedCharacterIterator it = as.getIterator();

		LineBreakMeasurer measurer = new LineBreakMeasurer(
			it,
			BreakIterator.getLineInstance(getLocale()),
			frc
		);

		while (measurer.getPosition() < it.getEndIndex()) {
		    TextLayout layout = measurer.nextLayout(availW);

		    Line ln = new Line();
		    ln.layout = layout;
		    ln.height = layout.getAscent() + layout.getDescent() + layout.getLeading(); // typografische Zeilenhöhe
		    lines.add(ln);
		}
	    }

	    // --- PASS 1: visuelle Bounds des gesamten Textblocks bei typografischem Zeilenabstand bestimmen
	    float yCursor = 0f;

	    float minTop = Float.POSITIVE_INFINITY;
	    float maxBottom = Float.NEGATIVE_INFINITY;

	    for (Line ln : lines) {
		if (ln.layout == null) {
		    // Leerzeile: zählt als reine typografische Box
		    minTop = Math.min(minTop, yCursor);
		    maxBottom = Math.max(maxBottom, yCursor + ln.height);
		    yCursor += ln.height;
		    continue;
		}

		// Baseline dieser Zeile (relativ zum Blockstart bei y=0)
		float baseline = yCursor + ln.layout.getAscent();

		// Visuelle Bounds relativ zur Baseline
		var b = ln.layout.getBounds(); // Rectangle2D, y ist meist negativ

		float top = baseline + (float) b.getY();
		float bottom = baseline + (float) (b.getY() + b.getHeight());

		minTop = Math.min(minTop, top);
		maxBottom = Math.max(maxBottom, bottom);

		yCursor += ln.height;
	    }

	    if (minTop == Float.POSITIVE_INFINITY) {
		// Nichts zu zeichnen
		return;
	    }

	    float visualH = maxBottom - minTop;

	    // Shift so, dass die visuellen Bounds mittig im verfügbaren Bereich liegen
	    float shiftY = ins.top + (availH - visualH) / 2f - minTop;

	    // --- PASS 2: Zeichnen (mit typografischem Zeilenabstand, aber visuell zentriert)
	    yCursor = 0f;

	    for (Line ln : lines) {
		if (ln.layout == null) {
		    yCursor += ln.height;
		    continue;
		}

		float baseline = shiftY + yCursor + ln.layout.getAscent();

		float lineW = ln.layout.getAdvance();
		float x = ins.left + (availW - lineW) / 2f;

		ln.layout.draw(g2, x, baseline);

		yCursor += ln.height;
	    }

	} finally {
	    g2.dispose();
	}
    }

    protected void paintComponent_old(Graphics g) {

	// Hintergrund
	if (isOpaque()) {
	    g.setColor(getBackground());
	    g.fillRect(0, 0, getWidth(), getHeight());
	}

	// Border (falls vorhanden)
	paintBorder(g);

	Graphics2D g2 = (Graphics2D) g.create();
	try {
	    g2.setFont(getFont());
	    g2.setColor(getForeground());
	    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

	    Insets ins = getInsets();
	    float availW = getWidth() - ins.left - ins.right;
	    float availH = getHeight() - ins.top - ins.bottom;

	    if (availW <= 1 || availH <= 1) {
		return;
	    }

	    String text = getText();
	    if (text == null) {
		text = "";
	    }

	    FontRenderContext frc = g2.getFontRenderContext();
	    FontMetrics fm = g2.getFontMetrics(getFont());

	    // Text in echte "Umbruch-Zeilen" zerlegen
	    List<Line> lines = new ArrayList<>();
	    float totalH = 0;

	    // \n manuell als Absatz/Zeilenbruch behandeln
	    String[] paragraphs = text.split("\n", -1);

	    for (String p : paragraphs) {
		if (p.isEmpty()) {
		    // echte Leerzeile
		    Line ln = new Line();
		    ln.layout = null;
		    ln.height = fm.getHeight();
		    lines.add(ln);
		    totalH += ln.height;
		    continue;
		}

		AttributedString as = new AttributedString(p);
		as.addAttribute(TextAttribute.FONT, getFont());
		AttributedCharacterIterator it = as.getIterator();

		LineBreakMeasurer measurer = new LineBreakMeasurer(
			it,
			BreakIterator.getLineInstance(getLocale()), // gut für CJK
			frc
		);

		while (measurer.getPosition() < it.getEndIndex()) {
		    TextLayout layout = measurer.nextLayout(availW);

		    Line ln = new Line();
		    ln.layout = layout;
		    ln.height = layout.getAscent() + layout.getDescent() + layout.getLeading();

		    lines.add(ln);
		    totalH += ln.height;
		}
	    }

	    // Vertikal zentrieren
	    float y = ins.top + (availH - totalH) / 2f;

	    // Zeichnen: jede Layout-Zeile horizontal zentriert
	    for (Line ln : lines) {
		if (ln.layout == null) {
		    y += ln.height;
		    continue;
		}

		y += ln.layout.getAscent();

		float lineW = ln.layout.getAdvance();
		float x = ins.left + (availW - lineW) / 2f;

		ln.layout.draw(g2, x, y);

		y += ln.layout.getDescent() + ln.layout.getLeading();
	    }

	} finally {
	    g2.dispose();
	}
    }
}
