package org.mcaccess.minecraftaccess.test_utils;

import org.junit.jupiter.api.Test;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;

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