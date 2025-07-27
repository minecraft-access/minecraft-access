package org.mcaccess.minecraftaccess.mixin;

import java.util.Objects;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import org.mcaccess.minecraftaccess.MainClass;

@Mixin(GameModeSwitcherScreen.class)
public class GameModeSwitcherScreenMixin {
    @Unique
    private GameModeIconAccessor previous;

    @WrapOperation(
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/screens/debug/GameModeSwitcherScreen;currentlyHovered:Lnet/minecraft/client/gui/screens/debug/GameModeSwitcherScreen$GameModeIcon;",
                    opcode = Opcodes.PUTFIELD
            ),
            method = {"init", "keyPressed", "render"}
    )
    private void narrateGameMode(GameModeSwitcherScreen instance, @Coerce GameModeIconAccessor value, Operation<Void> original) {
        original.call(instance, value);
        if (!Objects.equals(value, previous)) {
            MainClass.narrate(value.getName().getString(), true);
        }
        previous = value;
    }
}
