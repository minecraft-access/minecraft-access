package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

public class ClothConfigMixinPlugin implements IMixinConfigPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("org.mcaccess.minecraftaccess.compat.mixin.clothconfig.")) {
            return isClothConfigLoaded();
        }
        return true;
    }

    private boolean isClothConfigLoaded() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        return classLoader.getResource("me/shedaniel/clothconfig2/api/ConfigBuilder.class") != null;
    }
}
