package io.github.minecraft_access.minecraft_access.test_utils;

import io.github.minecraft_access.minecraft_access.utils.condition.Keystroke;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MockKeystrokeActionTest {
    @Test
    void testMockKeystrokeWorks() {
        var m = new MockKeystrokeAction(true);
        var k = new Keystroke(m.supplier);
        assertThat(k.isPressing()).isTrue();
        m.revertKeystrokeResult();
        assertThat(k.isPressing()).isFalse();
    }
}