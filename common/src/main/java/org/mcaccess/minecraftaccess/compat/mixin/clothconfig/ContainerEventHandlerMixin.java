package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import com.llamalad7.mixinextras.sugar.Local;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ContainerEventHandler.class)
public interface ContainerEventHandlerMixin {
    @Inject(method = "handleTabNavigation",
            at = @At(value = "INVOKE", target = "Ljava/util/Collections;sort(Ljava/util/List;Ljava/util/Comparator;)V", shift = At.Shift.AFTER))
    default void modifyChildrenBeforeComputeNavigationPath(CallbackInfoReturnable<ComponentPath> cir, @Local List<? extends GuiEventListener> ref) {
        // Skip the tab list scroll buttons when navigating through cloth screen's widgets
        if (this instanceof ClothConfigScreen) {
            ClothConfigScreenAccessor screen = ((ClothConfigScreenAccessor) this);
            ref.removeIf(e -> e == screen.getButtonLeftTab() || e == screen.getButtonRightTab());
        }
    }
}
