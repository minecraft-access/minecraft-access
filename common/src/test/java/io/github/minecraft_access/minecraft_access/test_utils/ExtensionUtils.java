package io.github.minecraft_access.minecraft_access.test_utils;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class ExtensionUtils {
    /**
     * @param targetClass          which class to be mocked with Mockito.mockStatic()
     * @param fieldAnnotationClass find field annotated with which annotation
     * @return generated MockedStatic instance
     */
    public static <T> MockedStatic<T> mockStaticForAnnotatedField(ExtensionContext extensionContext, Class<T> targetClass, Class<? extends Annotation> fieldAnnotationClass) {
        MockedStatic<T> ms = Mockito.mockStatic(targetClass);

        // Assign mock instance to first tagged field in test class
        Object testInstance = extensionContext.getRequiredTestInstance();
        Optional<Field> of = Arrays.stream(testInstance.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(fieldAnnotationClass))
                .findFirst();

        if (of.isPresent()) {
            try {
                of.get().trySetAccessible();
                of.get().set(testInstance, ms);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return ms;
    }
}
