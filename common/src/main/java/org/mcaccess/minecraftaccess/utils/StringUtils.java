package com.github.khanshoaib3.minecraft_access.utils;

public class StringUtils {

    public static String getLineTextWhereTheCursorIsLocatedIn(String whole, int cursor) {
        // ref: https://stackoverflow.com/questions/5034442/indexes-of-all-occurrences-of-character-in-a-string
        String lineSeparator = "\n";
        int index = whole.indexOf(lineSeparator), previousIndex = 0, start = 0, end = whole.length();

        // for example whole="\na", cursor=0
        if (cursor == 0 && index == 0) return "";

        while (index >= 0) {
            if (index < cursor) {
                // line text is not include the line separator, so +1
                start = Math.min(whole.length(), index + 1);
            } else if (index == cursor) {
                // in this case, we chose the line ahead of this position
                start = previousIndex == 0 ? 0 : previousIndex + 1;
                end = index;
                break;
            } else {
                end = index;
                // we've found the line, no need to continue
                break;
            }
            previousIndex = index;
            // find the next position
            index = whole.indexOf(lineSeparator, index + 1);
        }
        return whole.substring(start, end);
    }


    /**
     * Comma first, then whitespaces.
     */
    public static String[] splitToParts(String msg) {
        if (msg.contains(",")) {
            return msg.split(",");
        } else {
            return msg.split("\\s");
        }
    }
}
