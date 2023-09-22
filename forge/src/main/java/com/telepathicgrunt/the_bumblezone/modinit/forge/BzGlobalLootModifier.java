package com.telepathicgrunt.the_bumblezone.modinit.forge;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.loot.forge.BeeStingerLootApplier;
import com.telepathicgrunt.the_bumblezone.loot.forge.DimensionFishingLootApplier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BzGlobalLootModifier {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Bumblezone.MODID);

    public static final RegistryObject<Codec<BeeStingerLootApplier>> INJECT_DROPS_TO_NON_BZ_MOBS = GLM.register("inject_drops_to_non_bz_mobs", BeeStingerLootApplier.CODEC);
    public static final RegistryObject<Codec<DimensionFishingLootApplier>> DIMENSION_FISHING = GLM.register("dimension_fishing", DimensionFishingLootApplier.CODEC);
}
