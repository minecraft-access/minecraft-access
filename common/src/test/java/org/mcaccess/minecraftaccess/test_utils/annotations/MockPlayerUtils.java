package org.mcaccess.minecraftaccess.test_utils.annotations;

import org.mcaccess.minecraftaccess.test_utils.extensions.MockPlayerUtilsExtension;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mockito.MockedStatic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tag for generating {@link MockedStatic} {@link PlayerUtils} instances, see {@link MockPlayerUtilsExtension}.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MockPlayerUtils {
}
