package com.github.khanshoaib3.minecraft_access.mixin;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.utils.system.KeyUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BookScreen.class)
public class BookScreenMixin {
    @Shadow
    private int pageIndex;
    @Shadow
    private BookScreen.Contents contents;

    @Shadow
    private Text pageIndexText;
    @Shadow
    private PageTurnWidget nextPageButton;
    @Shadow
    private PageTurnWidget previousPageButton;
    String previousContent = "";

    @Inject(at = @At("HEAD"), method = "render")
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) return;
        if (minecraftClient.currentScreen == null) return;

        boolean isRPressed = KeyUtils.isAnyPressed(GLFW.GLFW_KEY_R);

        // Repeat current page content and un-focus next and previous page buttons
        if (Screen.hasAltDown() && isRPressed) {
            if (this.nextPageButton.isFocused()) this.nextPageButton.setFocused(false);
            if (this.previousPageButton.isFocused()) this.previousPageButton.setFocused(false);
            previousContent = "";
        }

        int pageIndex = this.pageIndex;
        if (pageIndex < 0 || pageIndex > this.contents.getPageCount())
            return; //Return if the page index is out of bounds

        String currentPageContentString = this.contents.getPage(pageIndex).getString();
        currentPageContentString = "%s \n\n %s".formatted(this.pageIndexText.getString(), currentPageContentString);

        if (!previousContent.equals(currentPageContentString)) {
            previousContent = currentPageContentString;
            MainClass.speakWithNarrator(currentPageContentString, true);
        }
    }
}
