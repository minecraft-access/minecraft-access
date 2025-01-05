package org.mcaccess.minecraftaccess.utils;

import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;

/**
 * Preparing {@link Text}s for {@link NarrationMessageBuilder}
 */
public class NarrationMessages {
    /**
     * "{TYPE} %s out of %s" narration
     */
    public record Position(int position, int totalSize, Type type) {
        public Text toText() {
            return Text.translatable(type.translationKey, position + 1, totalSize);
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
