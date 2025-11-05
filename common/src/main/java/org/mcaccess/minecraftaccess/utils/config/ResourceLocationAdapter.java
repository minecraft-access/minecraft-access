package org.mcaccess.minecraftaccess.utils.config;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceLocation;

class ResourceLocationAdapter extends TypeAdapter<ResourceLocation> {
    @Override
    public void write(JsonWriter out, ResourceLocation value) throws IOException {
        out.value(value.toString());
    }

    @Override
    public ResourceLocation read(JsonReader in) throws IOException {
        return ResourceLocation.parse(in.nextString());
    }
}
