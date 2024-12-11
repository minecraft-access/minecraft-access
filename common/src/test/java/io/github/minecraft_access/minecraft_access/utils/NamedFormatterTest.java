package io.github.minecraft_access.minecraft_access.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NamedFormatterTest {
    @ParameterizedTest
    @MethodSource("testFormatInputs")
    void testFormat(String template, Map<String, Object> values, String expected) {
        String actual = NamedFormatter.format(template, values);
        assertEquals(expected, actual);
    }

    private static Stream<Arguments> testFormatInputs() {
        return Stream.of(
                Arguments.of("{name}", Map.of("name", "200"), "200"),
                Arguments.of("Hello, {name}!", Map.of("name", "200"), "Hello, 200!"),
                Arguments.of("Hello, \\{name}!", Map.of("name", "200"), "Hello, {name}!"),
                Arguments.of("Hello, {name}!", Map.of("foo", "bar"), "Hello, {name}!"),
                Arguments.of("Hello, {one}, {two}!", Map.of("one", "100", "two", "200"), "Hello, 100, 200!")
        );
    }
}