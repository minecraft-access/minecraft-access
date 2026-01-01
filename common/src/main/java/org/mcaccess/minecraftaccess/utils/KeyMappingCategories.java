package org.mcaccess.minecraftaccess.utils;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

import org.mcaccess.minecraftaccess.MainClass;

public final class KeyMappingCategories {
    public static final KeyMapping.Category ACCESS_MENU = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "access_menu"));
    public static final KeyMapping.Category CAMERA_CONTROLS = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls"));
    public static final KeyMapping.Category INVENTORY_CONTROLS = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls"));
    public static final KeyMapping.Category MOUSE_SIMULATION = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "mouse_simulation"));
    public static final KeyMapping.Category OBJECT_TRACKER = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "object_tracker"));
    public static final KeyMapping.Category OTHER = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other"));

    private KeyMappingCategories() {
    }
}
