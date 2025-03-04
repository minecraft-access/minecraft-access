package org.mcaccess.minecraftaccess.mixin;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Since text modifying narrations are suppressed in {@link EditBoxMixin},
 * manually speak (command) suggestions (in {@link AbstractCommandBlockEditScreen} and {@link ChatScreen}).
 */
@Mixin(CommandSuggestions.SuggestionsList.class)
public class CommandSuggestionsSuggestionsListMixin {
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
        this.lastNarratedEntry = this.current;
        String textToSpeak = mca$getSuggestionTextToSpeak();
        cir.setReturnValue(Component.nullToEmpty(textToSpeak));
        cir.cancel();
    }

    @Unique
    private String mca$getSuggestionTextToSpeak() {
        Suggestion suggestion = this.suggestionList.get(this.current);
        Message message = suggestion.getTooltip();

        String format = Config.getInstance().commandSuggestionNarratorFormat;
        String textToSpeak = format.formatted(this.current + 1, this.suggestionList.size(), suggestion.getText());

        if (message != null) {
            textToSpeak = I18n.get("minecraft_access.other.selected", textToSpeak + " " + message.getString());
        } else {
            textToSpeak = I18n.get("minecraft_access.other.selected", textToSpeak);
        }
        return textToSpeak;
    }

    @Inject(at = @At("HEAD"), method = "useSuggestion")
    private void speakCompletion(CallbackInfo ci) {
        String selected = this.suggestionList.get(this.current).getText();
        MainClass.speakWithNarratorIfNotEmpty(selected, true);
    }

    @Inject(at = @At("RETURN"), method = "<init>")
    private void speakFirstSuggestionWhenSuggestionsAreShown(CallbackInfo ci) {
        String first = mca$getSuggestionTextToSpeak();
        MainClass.speakWithNarratorIfNotEmpty(first, true);
    }
}
