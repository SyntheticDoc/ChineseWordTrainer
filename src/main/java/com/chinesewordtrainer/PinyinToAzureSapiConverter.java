package com.chinesewordtrainer;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Converts tone-marked or numbered Hanyu Pinyin into Azure zh-CN SAPI phoneme
 * strings, e.g. "Zhōngguó" -> "zhong 1 - guo 2".
 */
public final class PinyinToAzureSapiConverter {

    private static final int NEUTRAL_TONE = 5;
    private static final List<String> VALID_SYLLABLE_BASES_BY_LENGTH = buildValidSyllableBasesByLength();

    public String toAzureSapi(String pinyin) {
        List<PinyinSyllable> syllables = parse(pinyin);
        StringJoiner joiner = new StringJoiner(" - ");

        for (int i = 0; i < syllables.size(); i++) {
            PinyinSyllable current = syllables.get(i);

            // Erhua: Azure documents forms such as "hui r 4" for 一会儿.
            if (i + 1 < syllables.size()) {
                PinyinSyllable next = syllables.get(i + 1);
                if ("r".equals(next.getBase()) && next.getTone() == NEUTRAL_TONE) {
                    joiner.add(toAzureSapiBase(current.getBase()) + " r " + current.getTone());
                    i++;
                    continue;
                }
            }

            joiner.add(toAzureSapiBase(current.getBase()) + " " + current.getTone());
        }

        return joiner.toString();
    }

    public String toCompactNumberedPinyin(String pinyin) {
        List<PinyinSyllable> syllables = parse(pinyin);
        StringJoiner joiner = new StringJoiner("-");

        for (PinyinSyllable syllable : syllables) {
            joiner.add(syllable.getBase() + syllable.getTone());
        }

        return joiner.toString();
    }

    public List<PinyinSyllable> parse(String pinyin) {
        if (pinyin == null || pinyin.trim().isEmpty()) {
            throw new IllegalArgumentException("Pinyin must not be null or empty.");
        }

        List<Token> tokens = normalizePinyin(pinyin);
        List<PinyinSyllable> result = new ArrayList<>();

        for (Token token : tokens) {
            List<PinyinSyllable> segmented = segmentToken(token);

            if (segmented == null) {
                throw new IllegalArgumentException(
                        "Could not split pinyin token into known syllables: " + token.letters
                                + " from original pinyin: " + pinyin);
            }

            result.addAll(segmented);
        }

        if (result.isEmpty()) {
            throw new IllegalArgumentException("No pinyin syllables found in: " + pinyin);
        }

        return Collections.unmodifiableList(result);
    }

    private List<PinyinSyllable> segmentToken(Token token) {
        return segmentFrom(token, 0);
    }

    private List<PinyinSyllable> segmentFrom(Token token, int pos) {
        if (pos == token.letters.length()) {
            return new ArrayList<>();
        }

        for (String base : VALID_SYLLABLE_BASES_BY_LENGTH) {
            if (!token.letters.startsWith(base, pos)) {
                continue;
            }

            int end = pos + base.length();
            int tone = toneInRange(token, pos, end);

            if (tone == -1) {
                continue;
            }

            List<PinyinSyllable> rest = segmentFrom(token, end);

            if (rest != null) {
                List<PinyinSyllable> out = new ArrayList<>();
                out.add(new PinyinSyllable(base, tone));
                out.addAll(rest);
                return out;
            }
        }

        return null;
    }

    /**
     * @return -1 if more than one tone mark/number falls into the same syllable.
     */
    private int toneInRange(Token token, int start, int end) {
        int tone = 0;

        for (Map.Entry<Integer, Integer> e : token.toneAtIndex.entrySet()) {
            int index = e.getKey();

            if (index >= start && index < end) {
                if (tone != 0) {
                    return -1;
                }
                tone = e.getValue();
            }
        }

        return tone == 0 ? NEUTRAL_TONE : tone;
    }

    private List<Token> normalizePinyin(String pinyin) {
        String normalized = Normalizer.normalize(pinyin, Normalizer.Form.NFC)
                .toLowerCase(Locale.ROOT)
                .replace('ǹ', 'n')
                .replace('ń', 'n');

        List<Token> result = new ArrayList<>();
        StringBuilder letters = new StringBuilder();
        Map<Integer, Integer> toneAtIndex = new HashMap<>();

        for (int i = 0; i < normalized.length(); i++) {
            char ch = normalized.charAt(i);

            // Common ASCII spelling for ü: u:
            if (ch == 'u' && i + 1 < normalized.length() && normalized.charAt(i + 1) == ':') {
                letters.append('v');
                i++;
                continue;
            }

            ToneChar toneChar = mapToneChar(ch);
            if (toneChar != null) {
                int index = letters.length();
                letters.append(toneChar.base);
                toneAtIndex.put(index, toneChar.tone);
                continue;
            }

            if (ch >= 'a' && ch <= 'z') {
                letters.append(ch);
                continue;
            }

            if (ch == 'ü') {
                letters.append('v');
                continue;
            }

            if (ch >= '1' && ch <= '5') {
                if (letters.length() == 0) {
                    throw new IllegalArgumentException("Tone number without preceding pinyin letters in: " + pinyin);
                }
                toneAtIndex.put(letters.length() - 1, Character.digit(ch, 10));
                continue;
            }

            // Spaces, apostrophes, hyphens, parentheses, commas, semicolons etc.
            // all terminate the current pinyin token.
            flushToken(result, letters, toneAtIndex);
        }

        flushToken(result, letters, toneAtIndex);
        return result;
    }

    private void flushToken(List<Token> result, StringBuilder letters, Map<Integer, Integer> toneAtIndex) {
        if (letters.length() == 0) {
            return;
        }

        result.add(new Token(letters.toString(), new HashMap<>(toneAtIndex)));
        letters.setLength(0);
        toneAtIndex.clear();
    }

    private ToneChar mapToneChar(char ch) {
        switch (ch) {
            case 'ā': return new ToneChar('a', 1);
            case 'á': return new ToneChar('a', 2);
            case 'ǎ': return new ToneChar('a', 3);
            case 'à': return new ToneChar('a', 4);

            case 'ē': return new ToneChar('e', 1);
            case 'é': return new ToneChar('e', 2);
            case 'ě': return new ToneChar('e', 3);
            case 'è': return new ToneChar('e', 4);

            case 'ī': return new ToneChar('i', 1);
            case 'í': return new ToneChar('i', 2);
            case 'ǐ': return new ToneChar('i', 3);
            case 'ì': return new ToneChar('i', 4);

            case 'ō': return new ToneChar('o', 1);
            case 'ó': return new ToneChar('o', 2);
            case 'ǒ': return new ToneChar('o', 3);
            case 'ò': return new ToneChar('o', 4);

            case 'ū': return new ToneChar('u', 1);
            case 'ú': return new ToneChar('u', 2);
            case 'ǔ': return new ToneChar('u', 3);
            case 'ù': return new ToneChar('u', 4);

            case 'ǖ': return new ToneChar('v', 1);
            case 'ǘ': return new ToneChar('v', 2);
            case 'ǚ': return new ToneChar('v', 3);
            case 'ǜ': return new ToneChar('v', 4);

            case 'ê': return new ToneChar('e', NEUTRAL_TONE);
            case 'ế': return new ToneChar('e', 2);
            case 'ề': return new ToneChar('e', 4);
            default: return null;
        }
    }

    private String toAzureSapiBase(String base) {
        // Azure zh-CN SAPI documents ü as final "v" and lüe/nüe as "l ue"/"n ue".
        if ("lv".equals(base)) {
            return "l v";
        }
        if ("nv".equals(base)) {
            return "n v";
        }
        if ("lve".equals(base)) {
            return "l ue";
        }
        if ("nve".equals(base)) {
            return "n ue";
        }

        return base;
    }

    private static List<String> buildValidSyllableBasesByLength() {
        Set<String> syllables = new HashSet<>();

        List<String> initials = Arrays.asList(
                "", "b", "p", "m", "f", "d", "t", "n", "l", "g", "k", "h",
                "j", "q", "x", "zh", "ch", "sh", "r", "z", "c", "s", "y", "w"
        );

        List<String> finals = Arrays.asList(
                "a", "o", "e", "ai", "ei", "ao", "ou", "an", "en", "ang", "eng", "ong", "er",
                "i", "ia", "ie", "iao", "iu", "ian", "in", "iang", "ing", "iong",
                "u", "ua", "uo", "uai", "ui", "uan", "un", "uang",
                "ue", "v", "ve"
        );

        for (String initial : initials) {
            for (String fin : finals) {
                syllables.add(initial + fin);
            }
        }

        // Syllabic/apical and special forms that simple initial+final generation
        // either misses or should strongly prefer as whole pinyin syllables.
        syllables.addAll(Arrays.asList(
                "zhi", "chi", "shi", "ri", "zi", "ci", "si",
                "yi", "wu", "yu", "ye", "yue", "yuan", "yin", "yun", "ying", "yong",
                "yo", "er", "r",
                "nv", "nve", "lv", "lve"
        ));

        List<String> result = new ArrayList<>(syllables);
        result.sort((a, b) -> Integer.compare(b.length(), a.length()));
        return Collections.unmodifiableList(result);
    }

    private static final class Token {
        private final String letters;
        private final Map<Integer, Integer> toneAtIndex;

        private Token(String letters, Map<Integer, Integer> toneAtIndex) {
            this.letters = letters;
            this.toneAtIndex = toneAtIndex;
        }
    }

    private static final class ToneChar {
        private final char base;
        private final int tone;

        private ToneChar(char base, int tone) {
            this.base = base;
            this.tone = tone;
        }
    }

    public static final class PinyinSyllable {
        private final String base;
        private final int tone;

        public PinyinSyllable(String base, int tone) {
            if (base == null || base.isEmpty()) {
                throw new IllegalArgumentException("base must not be null or empty.");
            }
            if (tone < 1 || tone > 5) {
                throw new IllegalArgumentException("tone must be 1, 2, 3, 4 or 5.");
            }
            this.base = base;
            this.tone = tone;
        }

        public String getBase() {
            return base;
        }

        public int getTone() {
            return tone;
        }

        @Override
        public String toString() {
            return base + tone;
        }
    }
}