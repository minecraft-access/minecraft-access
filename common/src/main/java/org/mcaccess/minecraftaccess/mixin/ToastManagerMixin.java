package org.mcaccess.minecraftaccess.mixin;

import lombok.extern.slf4j.Slf4j;
import net.fabricmc.loader.impl.util.log.Log;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.*;
import net.minecraft.text.OrderedText;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Collectors;

@Slf4j
@Mixin(ToastManager.class)
public class ToastManagerMixin {
    @Inject(at = @At("TAIL"), method = "add")
    public void add(Toast toast, CallbackInfo ci) {
        StringBuilder toastTextBuilder = new StringBuilder();
        toastTextBuilder.append(I18n.translate("minecraft_access.toast.shown"))
                .append(", ");
        switch (toast) {
            case AdvancementToast advancementToast -> {
                ((AdvancementToastAccessor) advancementToast).getAdvancement()
                        .value().display()
                        .ifPresent(display -> {
                            toastTextBuilder.append(display.getFrame().getToastText().getString())
                                    .append(' ')
                                    .append(display.getTitle().getString());
                        });
            }
            case RecipeToast ignored -> toastTextBuilder.append(I18n.translate("recipe.toast.title"))
                    .append(". ")
                    .append(I18n.translate("recipe.toast.description"));
            case SystemToast systemToast -> {
                toastTextBuilder
                        .append(((SystemToastAccessor) systemToast)
                                .getTitle()
                                .getString())
                        .append(". ")
                        .append(((SystemToastAccessor) systemToast).getLines().stream()
                                .map(StringUtils::orderedTextToString)
                                .collect(Collectors.joining(" ")));
            }
            case TutorialToast tutorialToast -> {
                toastTextBuilder
                        .append(((TutorialToastAccessor) tutorialToast).getText().stream()
                                .map(StringUtils::orderedTextToString)
                                .collect(Collectors.joining(" ")));
            }
            default -> {
                toastTextBuilder.append(I18n.translate("minecraft_access.toast.unknown"));
            }
        }
        MainClass.speakWithNarrator(toastTextBuilder.toString(), false);
    }
}
