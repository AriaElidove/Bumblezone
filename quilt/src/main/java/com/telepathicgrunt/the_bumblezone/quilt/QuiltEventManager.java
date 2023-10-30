package com.telepathicgrunt.the_bumblezone.quilt;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.configs.BzModCompatibilityConfigs;
import com.telepathicgrunt.the_bumblezone.events.BlockBreakEvent;
import com.telepathicgrunt.the_bumblezone.events.RegisterCommandsEvent;
import com.telepathicgrunt.the_bumblezone.events.RegisterVillagerTradesEvent;
import com.telepathicgrunt.the_bumblezone.events.RegisterWanderingTradesEvent;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.AddBuiltinResourcePacks;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.DatapackSyncEvent;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.RegisterDataSerializersEvent;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.RegisterFlammabilityEvent;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.RegisterReloadListenerEvent;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.RegisterSpawnPlacementsEvent;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.ServerGoingToStartEvent;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.ServerGoingToStopEvent;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.ServerLevelTickEvent;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.TagsUpdatedEvent;
import com.telepathicgrunt.the_bumblezone.events.player.PlayerItemAttackBlockEvent;
import com.telepathicgrunt.the_bumblezone.events.player.PlayerItemUseEvent;
import com.telepathicgrunt.the_bumblezone.events.player.PlayerItemUseOnBlockEvent;
import com.telepathicgrunt.the_bumblezone.fabricbase.FabricBaseEventManager;
import com.telepathicgrunt.the_bumblezone.mixin.quilt.qsl.BiomeModificationContextImplMixin;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import com.telepathicgrunt.the_bumblezone.utils.PlatformHooks;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.phys.BlockHitResult;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;
import org.quiltmc.qsl.block.content.registry.api.FlammableBlockEntry;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;
import org.quiltmc.qsl.entity.networking.api.tracked_data.QuiltTrackedDataHandlerRegistry;
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents;
import org.quiltmc.qsl.lifecycle.api.event.ServerWorldLoadEvents;
import org.quiltmc.qsl.lifecycle.api.event.ServerWorldTickEvents;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourcePackActivationType;
import org.quiltmc.qsl.villager.api.TradeOfferHelper;
import org.quiltmc.qsl.worldgen.biome.api.BiomeModificationContext;
import org.quiltmc.qsl.worldgen.biome.api.BiomeModifications;
import org.quiltmc.qsl.worldgen.biome.api.ModificationPhase;

import java.nio.file.Files;

public class QuiltEventManager {

    public static void init() {
        FabricBaseEventManager.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> RegisterCommandsEvent.EVENT.invoke(new RegisterCommandsEvent(dispatcher, selection, context)));
        ServerWorldTickEvents.START.register((server, world) -> ServerLevelTickEvent.EVENT.invoke(new ServerLevelTickEvent(world, false)));
        ServerWorldTickEvents.END.register((server, world) -> ServerLevelTickEvent.EVENT.invoke(new ServerLevelTickEvent(world, true)));
        ServerLifecycleEvents.STARTING.register(server -> ServerGoingToStartEvent.EVENT.invoke(new ServerGoingToStartEvent(server)));
        ServerLifecycleEvents.STOPPING.register(server -> ServerGoingToStopEvent.EVENT.invoke(ServerGoingToStopEvent.INSTANCE));

        ServerWorldLoadEvents.LOAD.register((server, level) -> registerTrades());

        RegisterFlammabilityEvent.EVENT.invoke(new RegisterFlammabilityEvent((block, igniteOdds, burnOdds) -> {
            BlockContentRegistries.FLAMMABLE.put(block, new FlammableBlockEntry(igniteOdds, burnOdds));
        }));
        RegisterReloadListenerEvent.EVENT.invoke(new RegisterReloadListenerEvent((id, listener) ->
                ResourceLoader.get(PackType.SERVER_DATA).registerReloader(new QuiltReloadListener(id, listener))));
        RegisterSpawnPlacementsEvent.EVENT.invoke(new RegisterSpawnPlacementsEvent(QuiltEventManager::registerPlacement));
        CommonLifecycleEvents.TAGS_LOADED.register((registry, client) ->
                TagsUpdatedEvent.EVENT.invoke(new TagsUpdatedEvent(registry, client)));
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockentity) ->
                !BlockBreakEvent.EVENT_LOWEST.invoke(new BlockBreakEvent(player, state)));

        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            DatapackSyncEvent.EVENT.invoke(new DatapackSyncEvent(player));
        });

        AddBuiltinResourcePacks.EVENT.invoke(new AddBuiltinResourcePacks((id, displayName, mode) -> {
            ModContainer container = getModPack(id);
            ResourceLoader.registerBuiltinResourcePack(
                    new ResourceLocation(container.metadata().id(), id.getPath()),
                    container,
                    switch (mode) {
                        case USER_CONTROLLED -> ResourcePackActivationType.NORMAL;
                        case ENABLED_BY_DEFAULT -> ResourcePackActivationType.DEFAULT_ENABLED;
                        case FORCE_ENABLED -> ResourcePackActivationType.ALWAYS_ENABLED;
                    },
                    displayName
            );
        }));

        RegisterDataSerializersEvent.EVENT.invoke(new RegisterDataSerializersEvent(QuiltTrackedDataHandlerRegistry::register));

        AttackBlockCallback.EVENT.register(QuiltEventManager::onItemAttackBlock);
        UseBlockCallback.EVENT.register(QuiltEventManager::onItemUseOnBlock);
        UseItemCallback.EVENT.register(QuiltEventManager::onItemUse);
    }

    private static ModContainer getModPack(ResourceLocation pack) {
        if (QuiltLoader.isDevelopmentEnvironment()) {
            for (ModContainer mod : QuiltLoader.getAllMods()) {
                if (mod.metadata().id().startsWith("generated_") && fileExists(mod, "resourcepacks/" + pack.getPath())) {
                    return mod;
                }
            }
        }
        return QuiltLoader.getModContainer(pack.getNamespace()).orElseThrow();
    }

    private static boolean fileExists(ModContainer container, String path) {
        return Files.exists(container.getPath(path));
    }

    private static <T extends Mob> void registerPlacement(EntityType<T> type, RegisterSpawnPlacementsEvent.Placement<T> placement) {
        SpawnPlacements.register(type, placement.spawn(), placement.height(), placement.predicate());
    }

    public static void registerTrades() {

        for (VillagerProfession profession : BuiltInRegistries.VILLAGER_PROFESSION) {
            RegisterVillagerTradesEvent.EVENT.invoke(new RegisterVillagerTradesEvent(profession,
                    (i, listing) -> TradeOfferHelper.registerVillagerOffers(profession, i, fill -> fill.add(listing))));
        }

        RegisterWanderingTradesEvent.EVENT.invoke(new RegisterWanderingTradesEvent(
                trade -> TradeOfferHelper.registerWanderingTraderOffers(1, fill -> fill.add(trade)),
                trade -> TradeOfferHelper.registerWanderingTraderOffers(2, fill -> fill.add(trade))
        ));
    }

    public static void lateInit() {
        if (PlatformHooks.isModLoaded("resourcefulbees") && BzModCompatibilityConfigs.spawnResourcefulBeesHoneycombVeins) {
            BiomeModifications.create(new ResourceLocation(Bumblezone.MODID, "resourceful_bees_compat"))
                    .add(ModificationPhase.ADDITIONS,
                            (context) -> context.isIn(TagKey.create(Registries.BIOME, new ResourceLocation(Bumblezone.MODID, Bumblezone.MODID))),
                            (context) -> {
                                for (Holder<PlacedFeature> placedFeatureHolder : getPlacedFeaturesByTag(context, BzTags.RESOURCEFUL_BEES_COMBS)) {
                                    FeatureConfiguration featureConfiguration = placedFeatureHolder.value().feature().value().config();

                                    if (featureConfiguration instanceof OreConfiguration oreConfiguration && oreConfiguration.targetStates.stream().noneMatch(e -> e.state.isAir())) {
                                        context.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatureHolder.unwrapKey().get());
                                    }
                                }
                            });
        }
    }

    private static Iterable<Holder<PlacedFeature>> getPlacedFeaturesByTag(BiomeModificationContext context, TagKey<PlacedFeature> placedFeatureTagKey) {
        RegistryAccess registryAccess = ((BiomeModificationContextImplMixin)context).getRegistries();
        Registry<PlacedFeature> placedFeatureRegistry = registryAccess.registryOrThrow(Registries.PLACED_FEATURE);
        return placedFeatureRegistry.getTagOrEmpty(placedFeatureTagKey);
    }

    public static InteractionResult onItemAttackBlock(Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction) {
        PlayerItemAttackBlockEvent event = new PlayerItemAttackBlockEvent(player, world, hand, player.getItemInHand(hand));
        InteractionResult result = PlayerItemAttackBlockEvent.EVENT_HIGH.invoke(event);
        return result != null ? result : InteractionResult.PASS;
    }

    public static InteractionResult onItemUseOnBlock(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
        PlayerItemUseOnBlockEvent event = new PlayerItemUseOnBlockEvent(player, world, hand, hitResult, player.getItemInHand(hand));
        InteractionResult result = PlayerItemUseOnBlockEvent.EVENT_HIGH.invoke(event);
        return result != null ? result : InteractionResult.PASS;
    }

    public static InteractionResultHolder<ItemStack> onItemUse(Player player, Level level, InteractionHand hand) {
        PlayerItemUseEvent event = new PlayerItemUseEvent(player, level, player.getItemInHand(hand));
        if (PlayerItemUseEvent.EVENT_HIGH.invoke(event)) {
            return InteractionResultHolder.success(event.usingStack());
        }
        return InteractionResultHolder.pass(event.usingStack());
    }
}
