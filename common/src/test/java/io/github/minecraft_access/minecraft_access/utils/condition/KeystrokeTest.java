package io.github.minecraft_access.minecraft_access.utils.condition;

import io.github.minecraft_access.minecraft_access.test_utils.MockKeystrokeAction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class KeystrokeTest {

    static class InitKeystrokeConditionSameAsExpected implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(new MockKeystrokeAction(true), true),
                    Arguments.of(new MockKeystrokeAction(false), false)
            );
        }
    }

    @Nested
    class KeystrokeConditionCheckingTest {
        @ParameterizedTest
        @ArgumentsSource(InitKeystrokeConditionSameAsExpected.class)
        void testIsPressing(MockKeystrokeAction keystroke, boolean expected) {
            checkPressing(keystroke, expected, Keystroke::isPressing);
        }

        @ParameterizedTest
        @ArgumentsSource(InitKeystrokeConditionSameAsExpected.class)
        void testIsNotPressing(MockKeystrokeAction keystroke, boolean expected) {
            checkNotPressing(keystroke, expected, Keystroke::isNotPressing);
        }

        @ParameterizedTest
        @ArgumentsSource(InitKeystrokeConditionSameAsExpected.class)
        void testIsReleased(MockKeystrokeAction keyStroke, boolean expected) {
            checkReleased(keyStroke, expected, Keystroke::isReleased);
        }

        @ParameterizedTest
        @ArgumentsSource(InitKeystrokeConditionSameAsExpected.class)
        void testIsPressed(MockKeystrokeAction keyStroke, boolean expected) {
            checkPressed(keyStroke, expected, Keystroke::isPressed);
        }
    }

    @Nested
    class KeystrokeTriggerTest {
        @ParameterizedTest
        @ArgumentsSource(InitKeystrokeConditionSameAsExpected.class)
        void testIsPressing(MockKeystrokeAction keystroke, boolean expected) {
            checkPressing(keystroke, expected, Keystroke::canBeTriggered);
        }

        @ParameterizedTest
        @ArgumentsSource(InitKeystrokeConditionSameAsExpected.class)
        void testIsNotPressing(MockKeystrokeAction keystroke, boolean expected) {
            checkNotPressing(keystroke, expected, Keystroke::canBeTriggered);
        }

        @ParameterizedTest
        @ArgumentsSource(InitKeystrokeConditionSameAsExpected.class)
        void testIsReleased(MockKeystrokeAction keyStroke, boolean expected) {
            checkReleased(keyStroke, expected, Keystroke::canBeTriggered);
        }

        @ParameterizedTest
        @ArgumentsSource(InitKeystrokeConditionSameAsExpected.class)
        void testIsPressed(MockKeystrokeAction keyStroke, boolean expected) {
            checkPressed(keyStroke, expected, Keystroke::canBeTriggered);
        }
    }

    @Nested
    class KeystrokeTriggerCountTest {
        @ParameterizedTest
        @ArgumentsSource(InitKeystrokeConditionSameAsExpected.class)
        void testIsPressing(MockKeystrokeAction keystroke, boolean expected) {
            checkPressing(keystroke,
                    k -> {
                        assertThat(k.canBeTriggered()).isEqualTo(expected);
                        if (expected) {
                            k.updateStateForNextTick();
                            assertThat(k.canBeTriggered())
                                    .as("can be triggered only once")
                                    .isFalse();
                        }
                    },
                    k -> assertThat(k.canBeTriggered()).isEqualTo(!expected));
        }

        @ParameterizedTest
        @ArgumentsSource(InitKeystrokeConditionSameAsExpected.class)
        void testIsNotPressing(MockKeystrokeAction keystroke, boolean expected) {
            checkNotPressing(keystroke,
                    k -> {
                        assertThat(k.canBeTriggered()).isEqualTo(expected);
                        if (expected) {
                            k.updateStateForNextTick();
                            assertThat(k.canBeTriggered())
                                    .as("can be triggered only once")
                                    .isFalse();
                        }
                    },
                    k -> assertThat(k.canBeTriggered()).isEqualTo(!expected));
        }

        @ParameterizedTest
        @ArgumentsSource(InitKeystrokeConditionSameAsExpected.class)
        void testIsReleased(MockKeystrokeAction keystroke, boolean expected) {
            checkReleased(keystroke,
                    k -> {
                        assertThat(k.canBeTriggered()).isEqualTo(expected);
                        if (expected) {
                            k.updateStateForNextTick();
                            assertThat(k.canBeTriggered())
                                    .as("can be triggered only once")
                                    .isFalse();
                        }
                    },
                    k -> assertThat(k.canBeTriggered()).isEqualTo(!expected));
        }

        @ParameterizedTest
        @ArgumentsSource(InitKeystrokeConditionSameAsExpected.class)
        void testIsPressed(MockKeystrokeAction keystroke, boolean expected) {
            checkPressed(keystroke,
                    k -> {
                        boolean exp = !expected;
                        assertThat(k.canBeTriggered())
                                .as("originally released key should be pressed now (expected should be true), vice versa")
                                .isEqualTo(exp);
                        if (exp) {
                            k.updateStateForNextTick();
                            assertThat(k.canBeTriggered())
                                    .as("can be triggered only once")
                                    .isFalse();
                        }
                    },
                    k -> assertThat(k.canBeTriggered()).isEqualTo(expected));
        }
    }

    private static void checkPressing(MockKeystrokeAction keystroke, boolean expected, Function<Keystroke, Boolean> actual) {
        checkPressing(keystroke,
                k -> assertThat(actual.apply(k)).isEqualTo(expected),
                k -> assertThat(actual.apply(k))
                        .as("keystroke condition should be reverted")
                        .isEqualTo(!expected));
    }

    private static void checkPressing(MockKeystrokeAction keystroke, Consumer<Keystroke> trueAssertion, Consumer<Keystroke> falseAssertion) {
        Keystroke k = new Keystroke(keystroke.supplier, Keystroke.TriggeredAt.PRESSING);

        trueAssertion.accept(k);
        k.updateStateForNextTick();

        keystroke.revertKeystrokeResult();
        falseAssertion.accept(k);
        k.updateStateForNextTick();

        keystroke.revertKeystrokeResult();
        trueAssertion.accept(k);
    }

    private static void checkNotPressing(MockKeystrokeAction keystroke, boolean expected, Function<Keystroke, Boolean> actual) {
        checkNotPressing(keystroke,
                k -> assertThat(actual.apply(k))
                        .as("keystroke condition should be reverted")
                        .isEqualTo(expected),
                k -> assertThat(actual.apply(k)).isEqualTo(!expected));
    }

    private static void checkNotPressing(MockKeystrokeAction keystroke, Consumer<Keystroke> trueAssertion, Consumer<Keystroke> falseAssertion) {
        Keystroke k = new Keystroke(keystroke.supplier, Keystroke.TriggeredAt.NOT_PRESSING);

        falseAssertion.accept(k);
        k.updateStateForNextTick();

        keystroke.revertKeystrokeResult();
        trueAssertion.accept(k);
        k.updateStateForNextTick();

        keystroke.revertKeystrokeResult();
        falseAssertion.accept(k);
    }

    private static void checkReleased(MockKeystrokeAction keystroke, boolean expected, Function<Keystroke, Boolean> actual) {
        checkReleased(keystroke,
                k -> assertThat(actual.apply(k))
                        .as("originally pressed key should be released now, vice versa")
                        .isEqualTo(expected),
                k -> assertThat(actual.apply(k)).isEqualTo(!expected));
    }

    private static void checkReleased(MockKeystrokeAction keystroke, Consumer<Keystroke> trueAssertion, Consumer<Keystroke> falseAssertion) {
        Keystroke k = new Keystroke(keystroke.supplier, Keystroke.TriggeredAt.RELEASED);

        // tick 0
        k.updateStateForNextTick();

        // tick 1
        keystroke.revertKeystrokeResult();
        trueAssertion.accept(k);
        k.updateStateForNextTick();

        // tick 2
        keystroke.revertKeystrokeResult();
        falseAssertion.accept(k);
    }

    private static void checkPressed(MockKeystrokeAction keystroke, boolean expected, Function<Keystroke, Boolean> actual) {
        checkPressed(keystroke,
                k -> assertThat(actual.apply(k))
                        .as("originally released key should be pressed now, vice versa")
                        .isEqualTo(!expected),
                k -> assertThat(actual.apply(k)).isEqualTo(expected));
    }

    private static void checkPressed(MockKeystrokeAction keystroke, Consumer<Keystroke> trueAssertion, Consumer<Keystroke> falseAssertion) {
        Keystroke k = new Keystroke(keystroke.supplier);

        // tick 0
        k.updateStateForNextTick();

        // tick 1
        keystroke.revertKeystrokeResult();
        trueAssertion.accept(k);
        k.updateStateForNextTick();

        // tick 2
        keystroke.revertKeystrokeResult();
        falseAssertion.accept(k);
    }
}
