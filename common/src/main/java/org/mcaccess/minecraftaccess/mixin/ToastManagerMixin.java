package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.components.toasts.*;
import net.minecraft.client.resources.language.I18n;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Collectors;

@Mixin(ToastManager.class)
public class ToastManagerMixin {
    @Inject(at = @At("TAIL"), method = "addToast")
    public void narrateToast(Toast toast, CallbackInfo ci) {
        StringBuilder toastTextBuilder = new StringBuilder();
        toastTextBuilder.append(net.minecraft.client.resources.language.I18n.get("minecraft_access.toast.shown"))
                .append(", ");
        switch (toast) {
            case AdvancementToast advancementToast -> {
                ((AdvancementToastAccessor) advancementToast).getAdvancement()
                        .value().display()
                        .ifPresent(display -> {
                            toastTextBuilder.append(display.getType().getDisplayName().getString())
                                    .append(' ')
                                    .append(display.getTitle().getString());
                        });
            }
            case RecipeToast ignored -> toastTextBuilder.append(I18n.get("recipe.toast.title"))
                    .append(". ")
                    .append(I18n.get("recipe.toast.description"));
            case SystemToast systemToast -> {
                toastTextBuilder
                        .append(((SystemToastAccessor) systemToast)
                                .getTitle().getString())
                        .append(". ")
                        .append(((SystemToastAccessor) systemToast).getMessageLines().stream()
                                .map(StringUtils::formattedCharSequenceToString)
                                .collect(Collectors.joining(" ")));
            }
            case TutorialToast tutorialToast -> {
                toastTextBuilder
                        .append(((TutorialToastAccessor) tutorialToast).getLines().stream()
                                .map(StringUtils::formattedCharSequenceToString)
                                .collect(Collectors.joining(" ")));
            }
            default -> {
                toastTextBuilder.append(I18n.get("minecraft_access.toast.unknown"));
            }
        }
        MainClass.speakWithNarrator(toastTextBuilder.toString(), false);
    }
}
