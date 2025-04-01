package com.github.shiftjis.cursedforestry.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Decoder;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.*;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.Reader;
import java.util.Iterator;
import java.util.Map;

@Mixin(RegistryDataLoader.class)
public class MixinRegistryDataLoader {
    @Inject(method = "loadRegistryContents", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Decoder;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static <E> void loadRegistryContents(RegistryOps.RegistryInfoLookup lookup, ResourceManager manager, ResourceKey<? extends Registry<E>> registryKey, WritableRegistry<E> registry, Decoder<E> decoder, Map<ResourceKey<?>, Exception> exceptions, CallbackInfo ci, String s, FileToIdConverter filetoidconverter, RegistryOps registryops, Iterator var9, Map.Entry entry, ResourceLocation resourcelocation, ResourceKey resourcekey, Resource resource, Reader reader, JsonElement jsonelement) {
        if (!s.startsWith("worldgen/configured_feature") || !jsonelement.isJsonObject()) {
            return;
        }

        JsonObject configuredFeature = jsonelement.getAsJsonObject();
        if (!configuredFeature.get("type").getAsString().equals("minecraft:tree")) {
            return;
        }

        JsonObject treeFeatureConfig = configuredFeature.getAsJsonObject("config");
        JsonObject foliageProvider = treeFeatureConfig.getAsJsonObject("foliage_provider");
        JsonObject trunkProvider = treeFeatureConfig.getAsJsonObject("trunk_provider");
        if (!foliageProvider.has("state") || !trunkProvider.has("state")) {
            return;
        }

        String vanillaFoliageState = foliageProvider.getAsJsonObject("state").get("Name").getAsString();
        String vanillaTrunkState = trunkProvider.getAsJsonObject("state").get("Name").getAsString();
        foliageProvider.getAsJsonObject("state").addProperty("Name", vanillaTrunkState);
        trunkProvider.getAsJsonObject("state").addProperty("Name", vanillaFoliageState);
    }
}
