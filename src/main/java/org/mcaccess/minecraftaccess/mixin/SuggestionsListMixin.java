package org.mcaccess.minecraftaccess.mixin;

import java.util.List;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.i18n.Narratable;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

/**
 * Since text modifying narrations are suppressed in {@link EditBoxMixin},
 * manually narrate (command) suggestions (in {@link AbstractCommandBlockEditScreen} and {@link ChatScreen}).
 */
@Mixin(CommandSuggestions.SuggestionsList.class)
abstract class SuggestionsListMixin {
    @Shadow
    private int lastNarratedEntry;
    @Shadow
    private int current;
    @Shadow
    @Final
    private List<Suggestion> suggestionList;

    @Inject(at = @At("HEAD"), method = "getNarrationMessage", cancellable = true)
    private void simplifySuggestionNarration(CallbackInfoReturnable<Component> cir) {
        // Don't know why they update this value here
        lastNarratedEntry = current;
        cir.setReturnValue(getSuggestionTextNarration().toComponent());
        cir.cancel();
    }

    @Unique
    private Narratable getSuggestionTextNarration() {
        Message message = suggestionList.get(current).getTooltip();

        String format = Config.getInstance().commandSuggestionNarratorFormat;
        Translation.Delimited item = new Translation.Delimited(' ')
                .put(format.formatted(current + 1, suggestionList.size(), suggestionList.get(current).getText()));
        if (message != null) {
            item.put(message.getString());
        }
        return new Translation("minecraft_access.other.selected")
                .variable("item").put(item);
    }

    @Inject(at = @At("HEAD"), method = "useSuggestion")
    private void narrateCompletion(CallbackInfo ci) {
        String selected = suggestionList.get(current).getText();
        MainClass.narrate(selected, true);
    }

    @Inject(at = @At("RETURN"), method = "<init>")
    private void narrateFirstSuggestionWhenSuggestionsAreShown(CallbackInfo ci) {
        getSuggestionTextNarration().narrate(true);
    }
}
