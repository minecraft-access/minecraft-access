package org.mcaccess.minecraftaccess.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow
    public abstract Component getNarrationMessage();

    @Shadow
    @Nullable
    public static Screen.NarratableSearchResult findNarratableWidget(List<? extends NarratableEntry> selectables, @Nullable NarratableEntry selectable) {
        return null;
    }

    @Shadow
    @Nullable
    private NarratableEntry lastNarratable;

    @Shadow
    @Final
    private List<NarratableEntry> narratables;

    @Shadow
    protected abstract void updateNarratedWidget(NarrationElementOutput builder);

    @Inject(at = @At("HEAD"), method = "updateNarrationState", cancellable = true)
    private void updateNarrationStateMixin(NarrationElementOutput builder, CallbackInfo ci) {
        builder.add(NarratedElementType.TITLE, this.getNarrationMessage());
        this.updateNarratedWidget(builder);
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "updateNarratedWidget", cancellable = true)
    private void updateNarratedWidgetMixin(NarrationElementOutput builder, CallbackInfo callbackInfo) {
        if (Minecraft.getInstance().screen instanceof MerchantScreen) {
            callbackInfo.cancel();
        }

        ImmutableList<NarratableEntry> immutableList = this.narratables.stream().filter(NarratableEntry::isActive).collect(ImmutableList.toImmutableList());
        Screen.NarratableSearchResult selectedElementNarrationData = findNarratableWidget(immutableList, this.lastNarratable);
        if (selectedElementNarrationData != null) {
            if (selectedElementNarrationData.priority.isTerminal()) {
                this.lastNarratable = selectedElementNarrationData.entry;
            }

            selectedElementNarrationData.entry.updateNarration(builder.nest());
        }

        callbackInfo.cancel();
    }
}
