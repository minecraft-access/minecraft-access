package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ContainerEventHandler.class)
public interface ContainerEventHandlerMixin {
    @WrapOperation(method = "handleTabNavigation",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/events/ContainerEventHandler;children()Ljava/util/List;"))
    default List<? extends GuiEventListener> modifyNavigationCandidates(ContainerEventHandler instance, Operation<List<? extends GuiEventListener>> original) {
        if (instance instanceof SubCategoryListEntry subcategory && subcategory.isExpanded()) {
            // respect search filter
            return subcategory.filteredEntries();
        } else if (instance instanceof ClothConfigScreen) {
            // remove scroll arrow buttons since they're useless for blind users
            return instance.children().stream().filter(this::mca$isNotArrowButton).toList();
        } else {
            return original.call(instance);
        }
    }

    @Unique
    private boolean mca$isNotArrowButton(GuiEventListener c) {
        ClothConfigScreenAccessor accessor = (ClothConfigScreenAccessor) this;
        return c != accessor.getButtonLeftTab() && c != accessor.getButtonRightTab();
    }
}
