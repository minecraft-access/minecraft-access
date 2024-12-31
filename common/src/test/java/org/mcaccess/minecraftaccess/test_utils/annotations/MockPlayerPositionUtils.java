package org.mcaccess.minecraftaccess.test_utils.annotations;

import org.mcaccess.minecraftaccess.test_utils.extensions.MockPlayerPositionUtilsExtension;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;
import org.mockito.MockedStatic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tag for generating {@link MockedStatic} {@link PlayerPositionUtils} instances, see {@link MockPlayerPositionUtilsExtension}.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MockPlayerPositionUtils {
}
