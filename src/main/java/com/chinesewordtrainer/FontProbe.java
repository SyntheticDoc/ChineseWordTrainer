/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.chinesewordtrainer;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TheDoctor
 */
public class FontProbe {

    private static final String SAMPLE_DE = "ÄÖÜäöüß";

    // Ein paar gezielte Trad/Simpl + häufige Radikale/Komponenten
    private static final String CORE_ZH =
            "起的一是不在人有我他这中大来上国个到说们为子和你地出道也时年得就那要下以生会自着去之过家学对可里后小多天心" +
            "汉字测试龙龟学习爱国重庆" +
            "漢字測試龍龜學習愛國重慶";

    // Kleine Stichprobe aus CJK Unified Ideographs (BMP): 0x4E00–0x9FFF
    // (keine Vollscan-Orgie, sondern z.B. alle ~200 Codepoints)
    private static String cjkSampleStride(int stride) {
        StringBuilder sb = new StringBuilder();
        for (int cp = 0x4E00; cp <= 0x9FFF; cp += stride) {
            sb.appendCodePoint(cp);
        }
        return sb.toString();
    }

    public static List<String> findFontFamilies(boolean deep) {
        String required = SAMPLE_DE + CORE_ZH + "，。！？（）《》「」【】";
	
        if (deep) required += cjkSampleStride(191); // ~ (0x9FFF-0x4E00)/191 ≈ 96 zusätzliche Hanzi

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] families = ge.getAvailableFontFamilyNames();

        List<String> hits = new ArrayList<>();
	
        for (String fam : families) {
            Font f = new Font(fam, Font.PLAIN, 12);
	    
	    System.out.println(f.getFontName());
	    
            if (f.canDisplayUpTo(required) == -1) {
                hits.add(fam);
            }
        }
	
        return hits;
    }
}
