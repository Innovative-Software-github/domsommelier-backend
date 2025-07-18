package com.innovativesoftware.domsommelier_backend.filter_management.util;

import java.util.HashMap;
import java.util.Map;

public final class TransliterationUtils {

    private static final Map<Character, String> TRANSLIT = new HashMap<>();
    static {
        TRANSLIT.put('а', "a"); TRANSLIT.put('б', "b"); TRANSLIT.put('в', "v");
        TRANSLIT.put('г', "g"); TRANSLIT.put('д', "d"); TRANSLIT.put('е', "e");
        TRANSLIT.put('ё', "yo"); TRANSLIT.put('ж', "zh"); TRANSLIT.put('з', "z");
        TRANSLIT.put('и', "i"); TRANSLIT.put('й', "y"); TRANSLIT.put('к', "k");
        TRANSLIT.put('л', "l"); TRANSLIT.put('м', "m"); TRANSLIT.put('н', "n");
        TRANSLIT.put('о', "o"); TRANSLIT.put('п', "p"); TRANSLIT.put('р', "r");
        TRANSLIT.put('с', "s"); TRANSLIT.put('т', "t"); TRANSLIT.put('у', "u");
        TRANSLIT.put('ф', "f"); TRANSLIT.put('х', "kh"); TRANSLIT.put('ц', "ts");
        TRANSLIT.put('ч', "ch"); TRANSLIT.put('ш', "sh"); TRANSLIT.put('щ', "shch");
        TRANSLIT.put('ы', "y"); TRANSLIT.put('э', "e"); TRANSLIT.put('ю', "yu");
        TRANSLIT.put('я', "ya"); TRANSLIT.put('ь', ""); TRANSLIT.put('ъ', "");
        // upper
        TRANSLIT.put('А', "A"); TRANSLIT.put('Б', "B"); TRANSLIT.put('В', "V");
        TRANSLIT.put('Г', "G"); TRANSLIT.put('Д', "D"); TRANSLIT.put('Е', "E");
        TRANSLIT.put('Ё', "Yo"); TRANSLIT.put('Ж', "Zh"); TRANSLIT.put('З', "Z");
        TRANSLIT.put('И', "I"); TRANSLIT.put('Й', "Y"); TRANSLIT.put('К', "K");
        TRANSLIT.put('Л', "L"); TRANSLIT.put('М', "M"); TRANSLIT.put('Н', "N");
        TRANSLIT.put('О', "O"); TRANSLIT.put('П', "P"); TRANSLIT.put('Р', "R");
        TRANSLIT.put('С', "S"); TRANSLIT.put('Т', "T"); TRANSLIT.put('У', "U");
        TRANSLIT.put('Ф', "F"); TRANSLIT.put('Х', "Kh"); TRANSLIT.put('Ц', "Ts");
        TRANSLIT.put('Ч', "Ch"); TRANSLIT.put('Ш', "Sh"); TRANSLIT.put('Щ', "Shch");
        TRANSLIT.put('Ы', "Y"); TRANSLIT.put('Э', "E"); TRANSLIT.put('Ю', "Yu");
        TRANSLIT.put('Я', "Ya"); TRANSLIT.put('Ь', ""); TRANSLIT.put('Ъ', "");
    }

    private TransliterationUtils() {}

    public static String transliterate(String input) {
        if (input == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            String repl = TRANSLIT.get(c);
            if (repl != null) {
                sb.append(repl);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String toFilterValue(String label) {
        if (label == null) return "";
        String translit = transliterate(label);
        String sanitized = translit.replaceAll("[\\s\\-.,/\\\\]+", "_")
                .replaceAll("[^a-zA-Z0-9_]", "");
        sanitized = sanitized.replaceAll("_+", "_")
                .replaceAll("^_+", "")
                .replaceAll("_+$", "");
        return sanitized.toLowerCase();
    }
}

