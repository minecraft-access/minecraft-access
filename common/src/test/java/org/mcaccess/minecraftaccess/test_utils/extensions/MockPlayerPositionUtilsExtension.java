package org.mcaccess.minecraftaccess.test_utils.extensions;

import org.mcaccess.minecraftaccess.test_utils.ExtensionUtils;
import org.mcaccess.minecraftaccess.test_utils.annotations.MockPlayerPositionUtils;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.MockedStatic;

import java.util.Objects;
import java.util.Optional;

/**
 * At {@link BeforeEach} phase, assign new {@link MockedStatic} {@link PlayerPositionUtils} instances to first field that tagged with {@link MockPlayerPositionUtils}.
 * Close the mocked static instance at {@link AfterEach} phase.
 */
public class MockPlayerPositionUtilsExtension implements BeforeEachCallback, AfterEachCallback {
    private MockedStatic<PlayerPositionUtils> ms;

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        this.ms = ExtensionUtils.mockStaticForAnnotatedField(extensionContext, PlayerPositionUtils.class, MockPlayerPositionUtils.class);
        this.ms.when(PlayerPositionUtils::getPlayerPosition).thenReturn(Optional.of(Vec3d.ZERO));
        this.ms.when(PlayerPositionUtils::getPlayerBlockPosition).thenReturn(Optional.of(new BlockPos(Vec3i.ZERO)));
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        if (Objects.nonNull(this.ms)) this.ms.close();
    }
}
