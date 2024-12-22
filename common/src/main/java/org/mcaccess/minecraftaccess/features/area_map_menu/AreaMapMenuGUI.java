package com.github.khanshoaib3.minecraft_access.features.area_map_menu;

import com.github.khanshoaib3.minecraft_access.utils.BaseScreen;
import net.minecraft.client.MinecraftClient;

import java.util.Objects;

public class AreaMapMenuGUI extends BaseScreen {
    public AreaMapMenuGUI(MinecraftClient client) {
        super("area_map_menu");
        this.client = client;
    }

    @Override
    public void close() {
        Objects.requireNonNull(this.client).setScreen(null);
    }
}
