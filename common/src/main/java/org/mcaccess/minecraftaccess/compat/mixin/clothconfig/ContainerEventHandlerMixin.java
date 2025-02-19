package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ContainerEventHandler.class)
public interface ContainerEventHandlerMixin {
    @WrapOperation(method = "handleTabNavigation",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/events/ContainerEventHandler;children()Ljava/util/List;"))
    default List<? extends GuiEventListener> respectSearchFilter(ContainerEventHandler instance, Operation<List<? extends GuiEventListener>> original) {
        if (instance instanceof SubCategoryListEntry subcategory) {
            return subcategory.filteredEntries();
        }
        return original.call(instance);
    }
}
