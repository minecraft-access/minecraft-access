package com.github.khanshoaib3.minecraft_access.utils;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Better than using string.format(),
 * placeholders makes translators know the context better.
 * ref: <a href="https://codereview.stackexchange.com/a/194454">source</a>
 */
public class NamedFormatter {
    private static final Pattern RE = Pattern.compile(
            "\\\\(.)" + // Treat any character after a backslash literally
                    "|" +
                    "(\\{([^)]+?)})"  // Look for {keys} to replace, "?" for non-greedy
    );

    private NamedFormatter() {
    }

    /**
     * Expands format strings containing <code>{keys}</code>.
     *
     * <p>Examples:</p>
     *
     * <ul>
     * <li><code>NamedFormatter.format("Hello, {name}!", Map.of("name", "200_success"))</code> → <code>"Hello, 200_success!"</code></li>
     * <li><code>NamedFormatter.format("Hello, \{name}!", Map.of("name", "200_success"))</code> → <code>"Hello, {name}!"</code></li>
     * <li><code>NamedFormatter.format("Hello, {name}!", Map.of("foo", "bar"))</code> → <code>"Hello, {name}!"</code></li>
     * </ul>
     *
     * @param fmt    The format string.  Any character in the format string that
     *               follows a backslash is treated literally.  Any
     *               <code>{key}</code> is replaced by its corresponding value
     *               in the <code>values</code> map.  If the key does not exist
     *               in the <code>values</code> map, then it is left unsubstituted.
     * @param values Key-value pairs to be used in the substitutions.
     * @return The formatted string.
     */
    public static String format(String fmt, Map<String, Object> values) {
        return RE.matcher(fmt).replaceAll(match ->
                match.group(1) != null ?
                        // escape
                        match.group(1) :
                        // replace
                        values.getOrDefault(match.group(3), match.group(2)).toString()
        );
    }
}