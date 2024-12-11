package io.github.minecraft_access.minecraft_access.test_utils.extensions;


import io.github.minecraft_access.minecraft_access.test_utils.ExtensionUtils;
import io.github.minecraft_access.minecraft_access.test_utils.annotations.MockPlayerUtils;
import io.github.minecraft_access.minecraft_access.utils.PlayerUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.MockedStatic;

import java.util.Objects;

/**
 * At {@link BeforeEach} phase,
 * assign new {@link MockedStatic} {@link PlayerUtils} instances to first field that tagged with {@link MockPlayerUtils}.
 * Close the mocked static instance at {@link AfterEach} phase.
 */
public class MockPlayerUtilsExtension implements BeforeEachCallback, AfterEachCallback {
    private MockedStatic<PlayerUtils> ms;

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        this.ms = ExtensionUtils.mockStaticForAnnotatedField(extensionContext, PlayerUtils.class, MockPlayerUtils.class);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        if (Objects.nonNull(this.ms)) this.ms.close();
    }
}
