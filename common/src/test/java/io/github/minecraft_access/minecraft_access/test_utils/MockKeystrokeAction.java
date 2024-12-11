package io.github.minecraft_access.minecraft_access.test_utils;

import io.github.minecraft_access.minecraft_access.utils.condition.Keystroke;
import io.github.minecraft_access.minecraft_access.utils.condition.MenuKeystroke;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.function.BooleanSupplier;

/**
 * Combining a changeable boolean variable with supplier.
 */
public class MockKeystrokeAction {
    Boolean pressed;
    public BooleanSupplier supplier;
    public Keystroke mockTarget;

    public void revertKeystrokeResult() {
        this.pressed = !this.pressed;
    }

    public MockKeystrokeAction(boolean initPressed) {
        this.pressed = initPressed;
        this.supplier = () -> this.pressed;
    }

    public void press() {
        this.pressed = true;
    }

    public void release() {
        this.pressed = false;
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
            return MockKeystrokeAction.mock((Keystroke) ReflectionUtils.tryToReadFieldValue(keyField).get());
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
            MockKeystrokeAction action = MockKeystrokeAction.released();
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
        this.pressed = false;
        try {
            if (this.mockTarget instanceof MenuKeystroke) {
                Field justClosed = MenuKeystroke.class.getDeclaredField("isMenuJustClosed");
                justClosed.setAccessible(true);
                justClosed.set(this.mockTarget, false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
