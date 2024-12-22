package com.github.khanshoaib3.minecraft_access.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;
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
    public abstract Text getNarratedTitle();

    @Shadow
    @Nullable
    public static Screen.SelectedElementNarrationData findSelectedElementData(List<? extends Selectable> selectables, @Nullable Selectable selectable) {
        return null;
    }

    @Shadow
    @Nullable
    private Selectable selected;

    @Shadow
    @Final
    private List<Selectable> selectables;

    @Shadow
    protected abstract void addElementNarrations(NarrationMessageBuilder builder);

    @Inject(at = @At("HEAD"), method = "addScreenNarrations", cancellable = true)
    private void addScreenNarrationsHead(NarrationMessageBuilder builder, CallbackInfo ci) {
        builder.put(NarrationPart.TITLE, this.getNarratedTitle());
        this.addElementNarrations(builder);
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "addElementNarrations*", cancellable = true)
    private void addElementNarrationsHead(NarrationMessageBuilder builder, CallbackInfo callbackInfo) {
        if (MinecraftClient.getInstance().currentScreen instanceof MerchantScreen) {
            callbackInfo.cancel();
        }

        ImmutableList<Selectable> immutableList = this.selectables.stream().filter(Selectable::isNarratable).collect(ImmutableList.toImmutableList());
        Screen.SelectedElementNarrationData selectedElementNarrationData = findSelectedElementData(immutableList, this.selected);
        if (selectedElementNarrationData != null) {
            if (selectedElementNarrationData.selectType.isFocused()) {
                this.selected = selectedElementNarrationData.selectable;
            }

            selectedElementNarrationData.selectable.appendNarrations(builder.nextMessage());
        }

        callbackInfo.cancel();
    }
}
