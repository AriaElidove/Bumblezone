package com.telepathicgrunt.the_bumblezone.modinit;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class BzDamageSources {
    public static final ResourceKey<DamageType> CRYSTALLINE_FLOWER_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "crystalline_flower"));
    public static final ResourceKey<DamageType> ARCHITECTS_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "architects"));
    public static final ResourceKey<DamageType> SPIKE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "spikes"));
    public static final ResourceKey<DamageType> COSMIC_CRYSTAL_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "cosmic_crystal"));
    public static final ResourceKey<DamageType> SENTRY_WATCHER_CRUSHING_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "sentry_watcher_crushing"));
}
