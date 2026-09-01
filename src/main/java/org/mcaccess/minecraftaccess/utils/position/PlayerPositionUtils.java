package org.mcaccess.minecraftaccess.utils.position;

import net.minecraft.client.Minecraft;

import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;
import org.mcaccess.minecraftaccess.utils.i18n.Untranslated;

/**
 * Functions about getting player entity's position, facing direction etc.
 */
public final class PlayerPositionUtils {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    private PlayerPositionUtils() {
    }

    public static Translation.Delimited getNarratableXYZPosition() {
        return new Translation.Delimited()
                .put(getNarratableXPos())
                .put(getNarratableYPos())
                .put(getNarratableZPos());
    }

    public static Translation.Delimited getNarratableXPos() {
        assert CLIENT.player != null;
        return new Translation.Delimited(Untranslated.FORMATTER.put(""))
                .put(CLIENT.player.position().x)
                .put("x");
    }

    public static Translation.Delimited getNarratableYPos() {
        assert CLIENT.player != null;
        return new Translation.Delimited(Untranslated.FORMATTER.put(""))
                .put(CLIENT.player.position().y)
                .put("y");
    }

    public static Translation.Delimited getNarratableZPos() {
        assert CLIENT.player != null;
        return new Translation.Delimited(Untranslated.FORMATTER.put(""))
                .put(CLIENT.player.position().z)
                .put("z");
    }

    public static Translation getVerticalFacingDirectionInWords() {
        assert CLIENT.player != null;
        int angle = Math.round(CLIENT.player.getRotationVector().x);
        if (Math.abs(angle) >= 88) {
            return new Translation("minecraft_access.direction").variant(angle > 0 ? "up" : "down");
        } else if (Math.abs(angle) >= 3) {
            return new Translation("minecraft_access.direction.degrees")
                    .variable("degrees").put(Math.abs(angle))
                    .variable("direction").put(new Translation("minecraft_access.direction").variant(angle > 0 ? "up" : "down"));
        } else {
            return new Translation("minecraft_access.direction").variant("straight");
        }
    }

    public static Orientation getHorizontalFacing() {
        assert CLIENT.player != null;
        return Orientation.ofHorizontal((int) CLIENT.player.getRotationVector().y % 360);
    }

    public static Translation.Unmodifiable getHorizontalFacingDirectionInWords() {
        return new Translation("minecraft_access.direction")
                .variant(getHorizontalFacing().toString())
                .unmodifiableView();
    }
}
