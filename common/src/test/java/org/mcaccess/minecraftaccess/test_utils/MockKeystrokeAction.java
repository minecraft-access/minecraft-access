package org.mcaccess.minecraftaccess.test_utils;

import java.lang.reflect.Field;
import java.util.function.BooleanSupplier;

import org.junit.platform.commons.util.ReflectionUtils;

import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
import org.mcaccess.minecraftaccess.utils.condition.MenuKeystroke;

/**
 * Combining a changeable boolean variable with supplier.
 */
public class MockKeystrokeAction {
    Boolean pressed;
    public BooleanSupplier supplier;
    public Keystroke mockTarget;

    public MockKeystrokeAction(boolean initPressed) {
        pressed = initPressed;
        supplier = () -> pressed;
    }

    public void revertKeystrokeResult() {
        pressed = !pressed;
    }

    public void press() {
        pressed = true;
    }

    public void release() {
        pressed = false;
    }

    public static MockKeystrokeAction pressed() {
        return new MockKeystrokeAction(true);
    }

    public static MockKeystrokeAction released() {
        return new MockKeystrokeAction(false);
    }

    /**
     * Replace given KeyStroke field's condition with new MockKeystrokeAction instance's supplier
     *
     * @return generated MockKeystrokeAction instance
     */
    public static MockKeystrokeAction mock(Class<?> clazz, String keyFieldName) {
        try {
            Field keyField = clazz.getDeclaredField(keyFieldName);
            return mock((Keystroke) ReflectionUtils.tryToReadFieldValue(keyField).get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Replace given KeyStroke field's condition with new MockKeystrokeAction instance's supplier
     *
     * @return generated MockKeystrokeAction instance
     */
    public static MockKeystrokeAction mock(Keystroke fieldValue) {
        try {
            MockKeystrokeAction action = released();
            action.mockTarget = fieldValue;
            Field conditionField = Keystroke.class.getDeclaredField("condition");
            conditionField.setAccessible(true);
            conditionField.set(action.mockTarget, action.supplier);
            return action;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reset target field's inner state to avoid test cases from affecting each other.
     */
    public void resetTargetInnerState() {
        pressed = false;
        try {
            if (mockTarget instanceof MenuKeystroke) {
                Field justClosed = MenuKeystroke.class.getDeclaredField("isMenuJustClosed");
                justClosed.setAccessible(true);
                justClosed.set(mockTarget, false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
