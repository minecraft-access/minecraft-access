package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.system.MouseUtils;

@Mixin(RecipeButton.class)
abstract class RecipeButtonMixin {
    @Shadow
    private RecipeCollection collection;

    @Unique
    private boolean vibratingFlag = false;

    @Unique
    private String previousItemName = "";

    @Unique
    private final Interval interval = Interval.ms(5000);

    @Shadow
    public abstract ItemStack getDisplayStack();

    @Inject(method = "updateWidgetNarration", at = @At("HEAD"), cancellable = true)
    private void updateWidgetNarrationsMixin(CallbackInfo ci) {
        ItemStack itemStack = getDisplayStack();
        String itemName = itemStack.getHoverName().getString();

        boolean sameItem = itemName.equalsIgnoreCase(previousItemName);
        if (!sameItem || interval.isReady()) {
            String craftable = collection.hasCraftable() ? "craftable" : "not_craftable";
            craftable = I18n.get("minecraft_access.other." + craftable);
            String narration = "%s %d %s".formatted(craftable, itemStack.getCount(), itemName);
            MainClass.narrate(narration, true);
        }

        // update the states
        if (!sameItem) {
            previousItemName = itemName;
            interval.reset();
        }

        shakeTheMouse();
        ci.cancel();
    }

    /**
     * It seems the "appendNarrations" will be invoked after every mouse moving.
     * Keep moving the mouse to trigger this method to read different items in same animated button.
     * It's not a solution that gets to the root of the problem, but I think it's simpler and more stable.
     * This method doesn't affect slot moving inside recipe book group.
     */
    @Unique
    private void shakeTheMouse() {
        // the width and height of one animated button are both 25.
        int offset = vibratingFlag ? 12 : 13;
        int x = ((AbstractWidgetAccessor) this).callGetX() + offset;
        int y = ((AbstractWidgetAccessor) this).callGetY() + offset;
        MouseUtils.Coordinates p = MouseUtils.calcRealPositionOfWidget(x, y);
        MouseUtils.move(p.x(), p.y());
        vibratingFlag = !vibratingFlag;
    }
}
