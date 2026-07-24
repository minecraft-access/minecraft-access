package org.mcaccess.minecraftaccess.mixin;

import java.util.List;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.ModConfig;

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

    @Inject(method = "getNarrationMessage", at = @At("HEAD"), cancellable = true)
    private void simplifySuggestionNarration(CallbackInfoReturnable<Component> cir) {
        // Don't know why they update this value here
        lastNarratedEntry = current;
        String textNarration = getSuggestionTextNarration();
        cir.setReturnValue(Component.nullToEmpty(textNarration));
        cir.cancel();
    }

    @Unique
    private String getSuggestionTextNarration() {
        Suggestion suggestion = suggestionList.get(current);
        Message message = suggestion.getTooltip();

        String format = ModConfig.getInstance().general.commandSuggestionNarratorFormat;
        String textNarration = format.formatted(current + 1, suggestionList.size(), suggestion.getText());

        if (message != null) {
            textNarration = I18n.get("minecraft_access.other.selected", textNarration + ' ' + message.getString());
        } else {
            textNarration = I18n.get("minecraft_access.other.selected", textNarration);
        }
        return textNarration;
    }

    @Inject(method = "useSuggestion", at = @At("HEAD"))
    private void narrateCompletion(CallbackInfo ci) {
        String selected = suggestionList.get(current).getText();
        MainClass.narrate(selected, true);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void narrateFirstSuggestionWhenSuggestionsAreShown(CallbackInfo ci) {
        String first = getSuggestionTextNarration();
        MainClass.narrate(first, true);
    }
}
