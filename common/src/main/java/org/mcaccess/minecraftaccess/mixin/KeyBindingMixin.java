package com.github.khanshoaib3.minecraft_access.mixin;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
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
 * This class is for making one {@link InputUtil.Key} being multiple {@link KeyBinding}'s "boundKey".
 * See issue <a href="https://github.com/khanshoaib3/minecraft-access/issues/310">#310</a> for details.
 */
@Mixin(KeyBinding.class)
public class KeyBindingMixin {
    @Unique
    private static final Multimap<InputUtil.Key, KeyBinding> KEY_TO_BINDINGS_LIST = ArrayListMultimap.create();
    @Final
    @Shadow
    private static Map<String, KeyBinding> KEYS_BY_ID;

    @Shadow
    private InputUtil.Key boundKey;

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
        Collection<KeyBinding> keyBindings = KEY_TO_BINDINGS_LIST.get(key);
        if (!keyBindings.isEmpty()) {
            keyBindings.forEach(kb -> {
                KeyBindingAccessor kba = (KeyBindingAccessor) kb;
                kba.setTimesPressed(kba.getTimesPressed() + 1);
            });
        }
        ci.cancel();
    }

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        Collection<KeyBinding> keyBindings = KEY_TO_BINDINGS_LIST.get(key);
        if (!keyBindings.isEmpty()) {
            keyBindings.forEach(kb -> kb.setPressed(pressed));
        }
        ci.cancel();
    }

    @Inject(method = "updateKeysByCode", at = @At("HEAD"), cancellable = true)
    private static void updateKeysByCode(CallbackInfo ci) {
        KEY_TO_BINDINGS_LIST.clear();
        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
            KEY_TO_BINDINGS_LIST.put(((KeyBindingAccessor) keyBinding).getBoundKey(), keyBinding);
        }
        ci.cancel();
    }


    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V", at = @At("RETURN"))
    void initMap(String translationKey, InputUtil.Type type, int code, String category, CallbackInfo ci) {
        KEY_TO_BINDINGS_LIST.put(this.boundKey, (KeyBinding) (Object) this);
    }
}
