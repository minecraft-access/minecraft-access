package org.mcaccess.minecraftaccess.fabric.mixin;

import net.minecraft.client.Minecraft;
import org.mcaccess.minecraftaccess.MainClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    /**
     * This is how fabric api "register" client tick events.
     * ref: <a href="https://github.com/FabricMC/fabric/blob/6e5bbc4a94f0976be5d13c59c7ee2f6eb17e2487/fabric-lifecycle-events-v1/src/client/java/net/fabricmc/fabric/mixin/event/lifecycle/client/MinecraftClientMixin.java#L38">...</a>
     */
    @Inject(at = @At("RETURN"), method = "tick")
    private void onEndTick(CallbackInfo info) {
        MainClass.clientTickEventsMethod(Minecraft.getInstance());
    }
}
