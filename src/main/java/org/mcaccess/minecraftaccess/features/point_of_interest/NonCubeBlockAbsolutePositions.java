package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * The position of the block (blockPos.getCenter()) is generally considered to be the center of the block (x.5,y.5,z.5).
 * Some blocks do not occupy the entire cube space, and for some of those that can be interacted with (thus need can be locked via POI Locking),
 * we manually calculate the locking position of these blocks (which are usually not the center of the block) by these methods.
 */
public final class NonCubeBlockAbsolutePositions {
    private NonCubeBlockAbsolutePositions() {
    }

    public static Vec3 getTrapDoorPos(Vec3 blockPos) {
        Map<String, String> props = getProperties(blockPos);
        String half = props.getOrDefault("half", "");
        String facing = props.getOrDefault("facing", "");
        String open = props.getOrDefault("open", "");

        double x = blockPos.x();
        double y = blockPos.y();
        double z = blockPos.z();

        if (open.equalsIgnoreCase("true")) {
            if (facing.equalsIgnoreCase("north")) {
                z += 0.4;
            } else if (facing.equalsIgnoreCase("south")) {
                z -= 0.4;
            } else if (facing.equalsIgnoreCase("west")) {
                x += 0.4;
            } else if (facing.equalsIgnoreCase("east")) {
                x -= 0.4;
            }
        } else if (open.equalsIgnoreCase("false")) {
            if (half.equalsIgnoreCase("bottom")) {
                y -= 0.4;
            } else if (half.equalsIgnoreCase("top")) {
                y += 0.4;
            }
        }

        return new Vec3(x, y, z);
    }

    public static Vec3 getLeverPos(Vec3 blockPos) {
        Map<String, String> props = getProperties(blockPos);
        String face = props.getOrDefault("face", "");
        String facing = props.getOrDefault("facing", "");

        double x = blockPos.x();
        double y = blockPos.y();
        double z = blockPos.z();

        if (face.equalsIgnoreCase("floor")) {
            y -= 0.3;
        } else if (face.equalsIgnoreCase("ceiling")) {
            y += 0.3;
        } else if (face.equalsIgnoreCase("wall")) {
            if (facing.equalsIgnoreCase("north")) {
                z += 0.3;
            } else if (facing.equalsIgnoreCase("south")) {
                z -= 0.3;
            } else if (facing.equalsIgnoreCase("east")) {
                x -= 0.3;
            } else if (facing.equalsIgnoreCase("west")) {
                x += 0.3;
            }
        }

        return new Vec3(x, y, z);
    }

    public static Vec3 getLadderPos(Vec3 blockPos) {
        Map<String, String> props = getProperties(blockPos);
        String facing = props.getOrDefault("facing", "");

        double x = blockPos.x();
        double y = blockPos.y();
        double z = blockPos.z();

        if (facing.equalsIgnoreCase("north")) {
            z += 0.35;
        } else if (facing.equalsIgnoreCase("south")) {
            z -= 0.35;
        } else if (facing.equalsIgnoreCase("west")) {
            x += 0.35;
        } else if (facing.equalsIgnoreCase("east")) {
            x -= 0.35;
        }

        return new Vec3(x, y, z);
    }

    public static Vec3 getButtonPos(Vec3 blockPos) {
        Map<String, String> props = getProperties(blockPos);
        String face = props.getOrDefault("face", "");
        String facing = props.getOrDefault("facing", "");

        double x = blockPos.x();
        double y = blockPos.y();
        double z = blockPos.z();

        if (face.equalsIgnoreCase("floor")) {
            y -= 0.4;
        } else if (face.equalsIgnoreCase("ceiling")) {
            y += 0.4;
        } else if (face.equalsIgnoreCase("wall")) {
            if (facing.equalsIgnoreCase("north")) {
                z += 0.4;
            } else if (facing.equalsIgnoreCase("south")) {
                z -= 0.4;
            } else if (facing.equalsIgnoreCase("east")) {
                x -= 0.4;
            } else if (facing.equalsIgnoreCase("west")) {
                x += 0.4;
            }
        }

        return new Vec3(x, y, z);
    }

    public static Vec3 getDoorPos(Vec3 blockPos) {
        Map<String, String> props = getProperties(blockPos);
        String facing = props.getOrDefault("facing", "");
        String hinge = props.getOrDefault("hinge", "");
        String open = props.getOrDefault("open", "");

        double x = blockPos.x();
        double y = blockPos.y();
        double z = blockPos.z();

        if (open.equalsIgnoreCase("false")) {
            if (facing.equalsIgnoreCase("north")) {
                z += 0.35;
            } else if (facing.equalsIgnoreCase("south")) {
                z -= 0.35;
            } else if (facing.equalsIgnoreCase("east")) {
                x -= 0.35;
            } else if (facing.equalsIgnoreCase("west")) {
                x += 0.35;
            }
        } else {
            if (hinge.equalsIgnoreCase("right")) {
                if (facing.equalsIgnoreCase("north")) {
                    x += 0.35;
                } else if (facing.equalsIgnoreCase("south")) {
                    x -= 0.35;
                } else if (facing.equalsIgnoreCase("east")) {
                    z += 0.35;
                } else if (facing.equalsIgnoreCase("west")) {
                    z -= 0.35;
                }
            } else {
                if (facing.equalsIgnoreCase("north")) {
                    x -= 0.35;
                } else if (facing.equalsIgnoreCase("south")) {
                    x += 0.35;
                } else if (facing.equalsIgnoreCase("east")) {
                    z -= 0.35;
                } else if (facing.equalsIgnoreCase("west")) {
                    z += 0.35;
                }
            }
        }

        return new Vec3(x, y, z);
    }

    @NotNull
    private static Map<String, String> getProperties(Vec3 blockPos) {
        assert Minecraft.getInstance().level != null;
        BlockState blockState = Minecraft.getInstance().level.getBlockState(BlockPos.containing(blockPos));
        return blockState.getValues().collect(Collectors.toMap(
                v -> v.property().getName().toLowerCase(),
                Property.Value::valueName
        ));
    }
}
