package io.github.minecraft_access.minecraft_access.test_utils.annotations;

import io.github.minecraft_access.minecraft_access.test_utils.extensions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Combining multiple extensions for feature testing.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({MockitoExtension.class,
        MockConfigExtension.class,
        MockMinecraftClientExtension.class,
        MockPlayerPositionUtilsExtension.class,
        MockPlayerUtilsExtension.class,
        MockIntervalExtension.class
})
public @interface FeatureText {

}
