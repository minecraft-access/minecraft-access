package org.mcaccess.minecraftaccess.utils.condition;

import org.mcaccess.minecraftaccess.test_utils.MockInterval;
import org.mcaccess.minecraftaccess.test_utils.MockKeystrokeAction;
import org.mcaccess.minecraftaccess.test_utils.extensions.MockConfigExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockConfigExtension.class)
class DoubleClickTest {

    @Test
    void testCanCountTrigger() {
        MockKeystrokeAction m = MockKeystrokeAction.pressed();
        DoubleClick k = new DoubleClick(m.supplier, Keystroke.TriggeredAt.PRESSING);

        k.updateStateForNextTick();
        assertThat(k.canBeTriggered()).as("there should be one valid count").isTrue();

        k.updateStateForNextTick();
        assertThat(k.canBeTriggered()).as("once reaches triggered condition, the counter will be cleaned after update").isFalse();
    }

    @Test
    void testCanCleanStaleCountIfTimeOut() {
        MockKeystrokeAction m = MockKeystrokeAction.pressed();
        MockInterval i = new MockInterval(0, 0);
        DoubleClick k = new DoubleClick(m.supplier, Keystroke.TriggeredAt.PRESSING, i);

        // record first keystroke
        k.updateStateForNextTick();
        // simulate time passing through the interval
        i.setReady(true);
        k.updateStateForNextTick();

        assertThat(k.canBeTriggered()).as("first count should be cleaned").isFalse();
    }

    @Test
    void testCanTriggerIfProperlyTriggerAgain() {
        MockKeystrokeAction m = MockKeystrokeAction.pressed();
        MockInterval i = new MockInterval(0, 0);
        DoubleClick k = new DoubleClick(m.supplier, Keystroke.TriggeredAt.PRESSING, i);

        // record first keystroke
        k.updateStateForNextTick();
        // simulate time passing through the interval
        i.setReady(true);
        k.updateStateForNextTick();
        assertThat(k.canBeTriggered()).as("first count should be cleaned").isFalse();

        // third keystroke
        i.setReady(false);
        k.updateStateForNextTick();
        assertThat(k.canBeTriggered()).as("third keystroke should be regarded as first valid keystroke").isTrue();
    }
}