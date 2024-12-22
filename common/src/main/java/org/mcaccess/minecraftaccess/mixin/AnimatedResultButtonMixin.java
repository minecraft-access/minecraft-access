package org.mcaccess.minecraftaccess.mixin;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.system.MouseUtils;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimatedResultButton.class)
public abstract class AnimatedResultButtonMixin {
    @Shadow
    public abstract ItemStack getDisplayStack();

    @Shadow
    private RecipeResultCollection resultCollection;

    @Unique
    boolean minecraft_access$vibratingFlag = false;

    @Unique
    String minecraft_access$previousItemName = "";

    @Unique
    private final Interval minecraft_access$interval = Interval.inMilliseconds(5000);

    //    @Inject(at = @At("HEAD"), method = "appendNarrations", cancellable = true) // Pre 1.19.3
    @Inject(at = @At("HEAD"), method = "appendClickableNarrations", cancellable = true) // From 1.19.3
    private void appendNarrations(NarrationMessageBuilder builder, CallbackInfo callbackInfo) {
        ItemStack itemStack = getDisplayStack();
        String itemName = itemStack.getName().getString();

        boolean sameItem = itemName.equalsIgnoreCase(minecraft_access$previousItemName);
        if (!sameItem || minecraft_access$interval.isReady()) {
            String craftable = resultCollection.hasCraftableRecipes() ? "craftable" : "not_craftable";
            craftable = I18n.translate("minecraft_access.other." + craftable);
            String toSpeak = "%s %d %s".formatted(craftable, itemStack.getCount(), itemName);
            MainClass.speakWithNarrator(toSpeak, true);
        }

        // update the states
        if (!sameItem) {
            minecraft_access$previousItemName = itemName;
            minecraft_access$interval.reset();
        }

        minecraft_access$shakeTheMouse();
        callbackInfo.cancel();
    }

    /**
     * It seems the "appendNarrations" will be invoked after every mouse moving.
     * Keep moving the mouse to trigger this method to read different items in same animated button.
     * It's not a solution that gets to the root of the problem, but I think it's simpler and more stable.
     * This method doesn't affect slot moving inside recipe book group.
     */
    @Unique
    private void minecraft_access$shakeTheMouse() {
        // the width and height of one animated button are both 25.
        int offset = this.minecraft_access$vibratingFlag ? 12 : 13;
        int x = ((ClickableWidgetAccessor) this).callGetX() + offset;
        int y = ((ClickableWidgetAccessor) this).callGetY() + offset;
        MouseUtils.Coordinates p = MouseUtils.calcRealPositionOfWidget(x, y);
        MouseUtils.move(p.x(), p.y());
        this.minecraft_access$vibratingFlag = !this.minecraft_access$vibratingFlag;
    }
}
