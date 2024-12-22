package org.mcaccess.minecraftaccess.test_utils;

import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

public class MockKeystrokeActionArray {
    private final MockKeystrokeAction[] actions;

    public MockKeystrokeActionArray(Class<?> clazz, String keyArrayFieldName) {
        try {
            Field keyArrayField = clazz.getDeclaredField(keyArrayFieldName);
            Keystroke[] keyArray = (Keystroke[]) ReflectionUtils.tryToReadFieldValue(keyArrayField).get();

            actions = new MockKeystrokeAction[keyArray.length];
            for (int i = 0; i < keyArray.length; i++) {
                actions[i] = MockKeystrokeAction.mock(keyArray[i]);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int size() {
        return actions.length;
    }

    public MockKeystrokeAction get(int i) {
        return actions[Math.abs(i % size())];
    }

    /**
     * Reset target key array field's elements inner states to avoid test cases from affecting each other.
     */
    public void resetTargetInnerState() {
        Arrays.stream(actions).forEach(MockKeystrokeAction::resetTargetInnerState);
    }
}
