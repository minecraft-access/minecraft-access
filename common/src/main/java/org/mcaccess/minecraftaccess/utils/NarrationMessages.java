package org.mcaccess.minecraftaccess.utils;

import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/**
 * Preparing {@link net.minecraft.network.chat.Component}s for {@link NarrationElementOutput}
 */
public class NarrationMessages {
    /**
     * "{TYPE} %s out of %s" narration
     */
    public record Position(int position, int totalSize, Type type) {
        public Component toComponent() {
            return Component.translatable(type.translationKey, position + 1, totalSize);
        }

        /**
         * "narrator.position.*" keys in original game's lang files
         */
        public enum Type {
            TAB("narrator.position.tab");

            private final String translationKey;

            Type(String translationKey) {
                this.translationKey = translationKey;
            }
        }
    }
}
