package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.utils.i18n.Translation;
import org.mcaccess.minecraftaccess.utils.i18n.Untranslated;

@Mixin(ToastManager.class)
abstract class ToastManagerMixin {
    @Inject(method = "addToast", at = @At("TAIL"))
    private void narrateToast(Toast toast, CallbackInfo ci) {
        Translation.Delimited toastTextBuilder = new Translation.Delimited()
                .put(new Translation("minecraft_access.toast.shown"));
        switch (toast) {
            case AdvancementToast advancementToast -> ((AdvancementToastAccessor) advancementToast).getAdvancement()
                    .value().display()
                    .ifPresent(display -> toastTextBuilder.put(new Translation.Delimited(' ')
                            .put(display.getType().getDisplayName())
                            .put(display.getTitle())));
            case RecipeToast ignored -> toastTextBuilder.put(new Translation.Delimited(Untranslated.FORMATTER.put(". "))
                    .put(new Translation.Vanilla("recipe.toast.title"))
                    .put(new Translation.Vanilla("recipe.toast.description")));
            case SystemToast systemToast -> {
                Translation.Delimited titleLines = new Translation.Delimited(' ');
                ((SystemToastAccessor) systemToast).getTitleLines().forEach(titleLines::put);
                Translation.Delimited messageLines = new Translation.Delimited(' ');
                ((SystemToastAccessor) systemToast).getMessageLines().forEach(messageLines::put);
                toastTextBuilder.put(new Translation.Delimited(Untranslated.FORMATTER.put(". "))
                        .put(titleLines)
                        .put(messageLines));
            }
            case TutorialToast tutorialToast -> {
                Translation.Delimited lines = new Translation.Delimited(' ');
                ((TutorialToastAccessor) tutorialToast).getLines().forEach(lines::put);
                toastTextBuilder.put(lines);
            }
            default -> toastTextBuilder.put(new Translation("minecraft_access.toast.unknown"));
        }
        toastTextBuilder.narrate(false);
    }
}
