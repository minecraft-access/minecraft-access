package org.mcaccess.minecraftaccess.test_utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mcaccess.minecraftaccess.test_utils.MockMinecraftClientWrapper;
import org.mcaccess.minecraftaccess.test_utils.extensions.MockMinecraftClientExtension;

/**
 * Tag for generating {@link MockMinecraftClientWrapper} instances, see {@link MockMinecraftClientExtension}.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MockMinecraftClient {
}
