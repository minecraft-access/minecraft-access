package org.mcaccess.minecraftaccess.mixin;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Map;

/**
 * This class is for making one {@link InputConstants.Key} being multiple {@link KeyMapping}'s "boundKey".
 * See issue <a href="https://github.com/khanshoaib3/minecraft-access/issues/310">#310</a> for details.
 */
@Mixin(KeyMapping.class)
public class KeyMappingMixin {
    @Unique
    private static final Multimap<InputConstants.Key, KeyMapping> KEY_TO_BINDINGS_LIST = ArrayListMultimap.create();
    @Final
    @Shadow
    private static Map<String, KeyMapping> ALL;

    @Shadow
    private InputConstants.Key key;

    @Inject(at = @At("HEAD"), method = "click", cancellable = true)
    private static void clickMixin(InputConstants.Key key, CallbackInfo ci) {
        Collection<KeyMapping> keyBindings = KEY_TO_BINDINGS_LIST.get(key);
        if (!keyBindings.isEmpty()) {
            keyBindings.forEach(kb -> {
                KeyMappingAccessor kba = (KeyMappingAccessor) kb;
                kba.setClickCount(kba.getClickCount() + 1);
            });
        }
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "set", cancellable = true)
    private static void setMixin(InputConstants.Key key, boolean pressed, CallbackInfo ci) {
        Collection<KeyMapping> keyBindings = KEY_TO_BINDINGS_LIST.get(key);
        if (!keyBindings.isEmpty()) {
            keyBindings.forEach(kb -> kb.setDown(pressed));
        }
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "resetMapping", cancellable = true)
    private static void resetMappingMixin(CallbackInfo ci) {
        KEY_TO_BINDINGS_LIST.clear();
        for (KeyMapping keyBinding : ALL.values()) {
            KEY_TO_BINDINGS_LIST.put(((KeyMappingAccessor) keyBinding).getKey(), keyBinding);
        }
        ci.cancel();
    }

    @Inject(at = @At("RETURN"), method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V")
    void initMap(String translationKey, InputConstants.Type type, int code, String category, CallbackInfo ci) {
        KEY_TO_BINDINGS_LIST.put(this.key, (KeyMapping) (Object) this);
    }
}
