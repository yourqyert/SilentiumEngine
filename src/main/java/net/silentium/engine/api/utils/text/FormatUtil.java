package net.silentium.engine.api.utils.text;

public class FormatUtil {

    public static String getModifiedValue(long input, String v1, String v2, String v3) {
        StringBuilder modified = new StringBuilder();
        if (input > 5L && input < 20L || input % 10L == 0L || input % 10L >= 5L) {
            modified.append(input).append(" ").append(v2);
        } else if (input < 5L || input > 20L) {
            if (input % 10L == 1L) {
                modified.append(input).append(" ").append(v1);
            } else if (input % 10L >= 2L || input % 10L <= 4L) {
                modified.append(input).append(" ").append(v3);
            }
        }
        return modified.toString();
    }

    public static String toRomanNumerals(int number) {
        if (number < 1 || number > 3999) {
            throw new IllegalArgumentException("1 3999");
        }

        String[] romanSymbols = {"I", "IV", "V", "IX", "X", "XL", "L", "XC", "C", "CD", "D", "CM", "M"};
        int[] arabicValues = {1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000};

        StringBuilder roman = new StringBuilder();

        int i = romanSymbols.length - 1;
        while (number > 0) {
            if (number >= arabicValues[i]) {
                roman.append(romanSymbols[i]);
                number -= arabicValues[i];
            } else {
                i--;
            }
        }

        return roman.toString();
    }

}
