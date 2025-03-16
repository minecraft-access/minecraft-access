package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BookViewScreen.class)
public class BookViewScreenMixin {
    @Shadow
    private int currentPage;
    @Shadow
    private BookViewScreen.BookAccess bookAccess;

    @Shadow
    private Component pageMsg;
    @Shadow
    private PageButton forwardButton;
    @Shadow
    private PageButton backButton;
    String previousContent = "";

    @Inject(at = @At("HEAD"), method = "render")
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient == null) return;
        if (minecraftClient.screen == null) return;

        boolean isRPressed = KeyUtils.isAnyPressed(GLFW.GLFW_KEY_R);

        // Repeat current page content and un-focus next and previous page buttons
        if (Screen.hasAltDown() && isRPressed) {
            if (this.forwardButton.isFocused()) this.forwardButton.setFocused(false);
            if (this.backButton.isFocused()) this.backButton.setFocused(false);
            previousContent = "";
        }

        int pageIndex = this.currentPage;
        if (pageIndex < 0 || pageIndex > this.bookAccess.getPageCount())
            return; //Return if the page index is out of bounds

        String currentPageContentString = this.bookAccess.getPage(pageIndex).getString();
        currentPageContentString = "%s \n\n %s".formatted(this.pageMsg.getString(), currentPageContentString);

        if (!previousContent.equals(currentPageContentString)) {
            previousContent = currentPageContentString;
            MainClass.speakWithNarrator(currentPageContentString, true);
        }
    }
}
