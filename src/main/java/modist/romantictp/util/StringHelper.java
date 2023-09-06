package modist.romantictp.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

public class StringHelper {
    private static final List<String> ARTICLES = List.of
            ("a", "an", "the", "to", "in", "on", "from", "and", "with", "of");
    private static final List<String> ROMAN_NUMERALS =
            List.of("i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix", "x", "xi", "xii");
    private static final char[] ESCAPE_CHARACTERS =
            new char[] {'~', '\'', '"'};
    private static final char[] DELIMITERS =
            new char[] {' ', '.', '"'};
    public static final List<String> TOOLTIPS = new ArrayList<>();
    public static final String GROUP = registerTooltip("group");
    public static final String TIME = registerTooltip("time");
    public static final String AUTHOR = registerTooltip("author");
    public static final String SECTION = registerTooltip("section");
    public static final String ID = registerTooltip("id");
    public static final String REVERB = registerTooltip("reverb");

    public static String registerTooltip(String s) {
        TOOLTIPS.add(s);
        return s;
    }

    public static boolean validMidiName(String path) {
        return path.split("--").length <= 3;
    }

    public static String[] splitMidiName(String path) {
        return path.split("--");
    }

    public static String getGroup(String name) {
        String[] strings = name.split("/");
        if (strings.length < 2) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length - 1; i++) {
            String word = strings[i];
            sb.append(word);
            if (i < strings.length - 2) {
                sb.append("/");
            }
        }
        return sb.toString();
    }

    public static String getDisplayName(String name) {
        String[] strings = name.split("/");
        return strings[strings.length - 1];
    }

    public static String title(String str) {
        str = escape(str);
        String[] strings = str.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            String word = upperCase(i, strings[i]);
            sb.append(word);
            if (i < strings.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public static String escape(String str) {
        String ret = str;
        for(int i=0; i<ESCAPE_CHARACTERS.length; i++) {
            ret = ret.replace("__"+i+"__", String.valueOf(ESCAPE_CHARACTERS[i]));
        }
        return ret;
    }

    @SuppressWarnings("deprecation")
    private static String upperCase(int index, String str) {
        if(shouldCapitalizeAll(str)){
            return StringUtils.upperCase(str);
        }
        if (index==0 || shouldCapitalize(str)) {
            return WordUtils.capitalize(str, DELIMITERS);
        }
        return str;
    }

    private static boolean shouldCapitalize(String word) {
        return !ARTICLES.contains(word);
    }

    private static boolean shouldCapitalizeAll(String word) {
        return ROMAN_NUMERALS.contains(word);
    }
}
