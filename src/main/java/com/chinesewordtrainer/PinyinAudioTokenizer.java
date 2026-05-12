package com.chinesewordtrainer;

import java.util.*;

public class PinyinAudioTokenizer {

    private static final char NEUTRAL_TONE = '5';

    // Auf true lassen, wenn deine Dateien z.B. nv3.mp3, lv4.mp3 heißen.
    // Auf false stellen, wenn sie wirklich nü3.mp3, lü4.mp3 heißen.
    private static final boolean UMLAUT_U_AS_V = true;

    private final List<String> syllableBases;

    public PinyinAudioTokenizer(Collection<SoundFile> soundFiles) {
        Set<String> bases = new HashSet<>();

        for (SoundFile sf : soundFiles) {
            bases.add(sf.getSoundName().toLowerCase(Locale.ROOT));
        }

        syllableBases = new ArrayList<>(bases);

        // Wichtig: längste Silben zuerst, damit "zhong" vor z.B. "zho"/"ng" kommt.
        syllableBases.sort((a, b) -> Integer.compare(b.length(), a.length()));
    }

    public List<String> toAudioStems(String pinyin) {
        List<Token> tokens = normalizePinyin(pinyin);
        List<String> result = new ArrayList<>();

        for (Token token : tokens) {
            List<String> segmented = segmentToken(token);

            if (segmented == null) {
                throw new IllegalArgumentException(
                        "Could not split pinyin token into known syllables: " + token.letters
                );
            }

            result.addAll(segmented);
        }

        return result;
    }

    private List<String> segmentToken(Token token) {
        return segmentFrom(token, 0);
    }

    private List<String> segmentFrom(Token token, int pos) {
        if (pos == token.letters.length()) {
            return new ArrayList<>();
        }

        for (String base : syllableBases) {
            if (!token.letters.startsWith(base, pos)) {
                continue;
            }

            int end = pos + base.length();
            Character tone = toneInRange(token, pos, end);

            if (tone == null) {
                continue; // Mehr als ein Tonzeichen in dieser Silbe: ungültige Segmentierung.
            }

            if (tone == 0) {
                tone = NEUTRAL_TONE;
            }

            List<String> rest = segmentFrom(token, end);

            if (rest != null) {
                List<String> out = new ArrayList<>();
                out.add(base + tone);
                out.addAll(rest);
                return out;
            }
        }

        return null;
    }

    private Character toneInRange(Token token, int start, int end) {
        char tone = 0;

        for (Map.Entry<Integer, Character> e : token.toneAtIndex.entrySet()) {
            int index = e.getKey();

            if (index >= start && index < end) {
                if (tone != 0) {
                    return null;
                }

                tone = e.getValue();
            }
        }

        return tone;
    }

    private List<Token> normalizePinyin(String pinyin) {
        List<Token> result = new ArrayList<>();
        StringBuilder letters = new StringBuilder();
        Map<Integer, Character> toneAtIndex = new HashMap<>();

        for (int i = 0; i < pinyin.length(); i++) {
            char ch = Character.toLowerCase(pinyin.charAt(i));

            ToneChar tc = mapToneChar(ch);

            if (tc != null) {
                int index = letters.length();
                letters.append(tc.base);
                toneAtIndex.put(index, tc.tone);
                continue;
            }

            if (ch >= 'a' && ch <= 'z') {
                letters.append(ch);
                continue;
            }

            if (ch == 'ü') {
                letters.append(UMLAUT_U_AS_V ? 'v' : 'ü');
                continue;
            }

            // Unterstützt auch bereits numerisches Pinyin wie zhong1guo2.
            if (ch >= '1' && ch <= '5') {
                if (letters.length() == 0) {
                    throw new IllegalArgumentException("Tone number without preceding syllable in: " + pinyin);
                }

                toneAtIndex.put(letters.length() - 1, ch);
                continue;
            }

            // Leerzeichen, Apostroph, Bindestrich, Satzzeichen etc. trennen Tokens.
            flushToken(result, letters, toneAtIndex);
        }

        flushToken(result, letters, toneAtIndex);

        return result;
    }

    private void flushToken(List<Token> result, StringBuilder letters, Map<Integer, Character> toneAtIndex) {
        if (letters.length() == 0) {
            return;
        }

        result.add(new Token(letters.toString(), new HashMap<>(toneAtIndex)));
        letters.setLength(0);
        toneAtIndex.clear();
    }

    private ToneChar mapToneChar(char ch) {
        switch (ch) {
            case 'ā': return new ToneChar('a', '1');
            case 'á': return new ToneChar('a', '2');
            case 'ǎ': return new ToneChar('a', '3');
            case 'à': return new ToneChar('a', '4');

            case 'ē': return new ToneChar('e', '1');
            case 'é': return new ToneChar('e', '2');
            case 'ě': return new ToneChar('e', '3');
            case 'è': return new ToneChar('e', '4');

            case 'ī': return new ToneChar('i', '1');
            case 'í': return new ToneChar('i', '2');
            case 'ǐ': return new ToneChar('i', '3');
            case 'ì': return new ToneChar('i', '4');

            case 'ō': return new ToneChar('o', '1');
            case 'ó': return new ToneChar('o', '2');
            case 'ǒ': return new ToneChar('o', '3');
            case 'ò': return new ToneChar('o', '4');

            case 'ū': return new ToneChar('u', '1');
            case 'ú': return new ToneChar('u', '2');
            case 'ǔ': return new ToneChar('u', '3');
            case 'ù': return new ToneChar('u', '4');

            case 'ǖ': return new ToneChar(UMLAUT_U_AS_V ? 'v' : 'ü', '1');
            case 'ǘ': return new ToneChar(UMLAUT_U_AS_V ? 'v' : 'ü', '2');
            case 'ǚ': return new ToneChar(UMLAUT_U_AS_V ? 'v' : 'ü', '3');
            case 'ǜ': return new ToneChar(UMLAUT_U_AS_V ? 'v' : 'ü', '4');

            default: return null;
        }
    }

    private static class Token {
        String letters;
        Map<Integer, Character> toneAtIndex;

        Token(String letters, Map<Integer, Character> toneAtIndex) {
            this.letters = letters;
            this.toneAtIndex = toneAtIndex;
        }
    }

    private static class ToneChar {
        char base;
        char tone;

        ToneChar(char base, char tone) {
            this.base = base;
            this.tone = tone;
        }
    }
}