package org.mcaccess.minecraftaccess.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilsTest {
    @ParameterizedTest
    @MethodSource
    public void testGetLineTextWhereTheCursorIsLocated(String whole, int cursor, String result) {
        assertEquals(result, StringUtils.getLineTextWhereTheCursorIsLocatedIn(whole, cursor));
    }

    /**
     * These test cases are sampled from book editing screen
     */
    public static Stream<Arguments> testGetLineTextWhereTheCursorIsLocated() {
        return Stream.of(
                Arguments.of("a", 0, "a"),
                Arguments.of("a", 1, "a"),
                Arguments.of("\na", 0, ""),
                Arguments.of("a\n", 0, "a"),
                Arguments.of("a\n", 1, "a"),
                Arguments.of("a\nb", 1, "a"),
                Arguments.of("a\nb", 2, "b"),
                Arguments.of("a\nb\nc", 2, "b"),
                Arguments.of("a\nb\nc", 3, "b"),
                Arguments.of("a\nb\nc", 4, "c")
        );
    }
}