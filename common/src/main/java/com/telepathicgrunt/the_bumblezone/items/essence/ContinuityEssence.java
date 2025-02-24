package com.telepathicgrunt.the_bumblezone.items.essence;

import com.telepathicgrunt.the_bumblezone.configs.BzGeneralConfigs;
import com.telepathicgrunt.the_bumblezone.entities.teleportation.BzWorldSavedData;
import com.telepathicgrunt.the_bumblezone.events.entity.EntityDeathEvent;
import com.telepathicgrunt.the_bumblezone.mixin.entities.ServerPlayerAccessor;
import com.telepathicgrunt.the_bumblezone.modinit.BzDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.server.network.FilteredText;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DeathMessageType;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class ContinuityEssence extends AbilityEssenceItem {

    private static final Supplier<Integer> cooldownLengthInTicks = () -> BzGeneralConfigs.continuityEssenceCooldown;
    private static final Supplier<Integer> abilityUseAmount = () -> 1;
    private static final ConcurrentLinkedQueue<TickCapsule> NEXT_TICK_BEHAVIORS = new ConcurrentLinkedQueue<>();
    private static final Style INTENTIONAL_GAME_DESIGN_STYLE = Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("MCPE-28723")));

    private record TickCapsule(Runnable runnable, long tickTarget) {}

    public ContinuityEssence(Properties properties) {
        super(properties, cooldownLengthInTicks, abilityUseAmount);
    }

    @Override
    public int getColor() {
        return 0xFFFFFF;
    }

    @Override
    void addDescriptionComponents(List<Component> components) {
        components.add(Component.translatable("item.the_bumblezone.essence_continuity_description_1").withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.ITALIC));
        components.add(Component.translatable("item.the_bumblezone.essence_continuity_description_2").withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.ITALIC));
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        TickCapsule tickCapsule = NEXT_TICK_BEHAVIORS.poll();
        if (tickCapsule != null) {
            if (level.getGameTime() > tickCapsule.tickTarget) {
                tickCapsule.runnable().run();
            }
            else {
                NEXT_TICK_BEHAVIORS.add(tickCapsule);
            }
        }

        // Uncomment this for debugging purposes
//        if (entity instanceof ServerPlayer serverPlayer) {
//            serverPlayer.getCooldowns().removeCooldown(this);
//        }

        super.inventoryTick(itemStack, level, entity, i, bl);
    }

    @Override
    void applyAbilityEffects(ItemStack itemStack, Level level, ServerPlayer serverPlayer) {}

    public static boolean CancelledDeath(EntityDeathEvent event) {
        LivingEntity livingEntity = event.entity();
        if (livingEntity instanceof ServerPlayer player) {
            DamageSource source = event.source();
            Registry<DamageType> damageTypeRegistry = player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);

            // Kill command. Do not activate in that case.
            if (damageTypeRegistry.get(DamageTypes.GENERIC_KILL) == event.source().type() &&
                    source.getEntity() == null &&
                    source.getDirectEntity() == null &&
                    source.getSourcePosition() == null)
            {
                return false;
            }

            ItemStack itemStack = player.getOffhandItem();
            if (player.isDeadOrDying() &&
                itemStack.getItem() instanceof ContinuityEssence continuityEssence &&
                itemStack.get(BzDataComponents.ABILITY_ESSENCE_ACTIVITY_DATA.get()).isActive() &&
                !player.getCooldowns().isOnCooldown(itemStack.getItem()))
            {
                playerReset(player);

                List<MobEffectInstance> mobEffectInstances = new ArrayList<>(player.getActiveEffects());
                for (MobEffectInstance mobEffectInstance : mobEffectInstances) {
                    if (!mobEffectInstance.getEffect().value().isBeneficial()) {
                        player.removeEffect(mobEffectInstance.getEffect());
                    }
                }

                MinecraftServer server = player.level().getServer();
                if (server != null) {
                    spawnParticles(player.serverLevel(), player.position(), player.getRandom());
                    respawn(itemStack, continuityEssence, player, server, event.source());
                }
                return true;
            }
        }
        return false;
    }


    private static void respawn(ItemStack stack, ContinuityEssence continuityEssence, ServerPlayer serverPlayer, MinecraftServer server, DamageSource damageSource) {
        ResourceKey<Level> oldDimension = serverPlayer.level().dimension();
        BlockPos oldPosition = serverPlayer.blockPosition();
        ResourceKey<Level> respawnDimension = serverPlayer.getRespawnDimension();
        BlockPos respawningLinkedPosition = serverPlayer.getRespawnPosition();

        ServerLevel desiredDestination = server.getLevel(respawnDimension);
        Optional<Vec3> optionalRespawnPoint = desiredDestination != null && respawningLinkedPosition != null ?
                Optional.of(serverPlayer.findRespawnPositionAndUseSpawnBlock(true, DimensionTransition.DO_NOTHING).pos()) : Optional.empty();

        ServerLevel finalDestination = desiredDestination != null && optionalRespawnPoint.isPresent() ? desiredDestination : server.overworld();

        if (optionalRespawnPoint.isEmpty() && respawningLinkedPosition != null) {
            serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0f));
        }

        Vec3 playerRespawnPosition;
        BlockPos playerRespawnBlockPos;
        boolean isRespawnAnchor;
        if (optionalRespawnPoint.isPresent()) {
            playerRespawnPosition = optionalRespawnPoint.get();
            playerRespawnBlockPos = BlockPos.containing(playerRespawnPosition);

            BlockState blockState = finalDestination.getBlockState(respawningLinkedPosition);
            isRespawnAnchor = blockState.is(Blocks.RESPAWN_ANCHOR);
        }
        else {
            playerRespawnPosition = finalDestination.getSharedSpawnPos().getCenter();
            playerRespawnBlockPos = finalDestination.getSharedSpawnPos();

            isRespawnAnchor = false;
        }

        BzWorldSavedData.queueEntityToGenericTeleport(serverPlayer, finalDestination.dimension(), playerRespawnBlockPos, () -> {
            if (isRespawnAnchor) {
                serverPlayer.connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, respawningLinkedPosition.getX(), respawningLinkedPosition.getY(), respawningLinkedPosition.getZ(), 1.0f, 1.0f, finalDestination.getRandom().nextLong()));
            }

            playerReset(serverPlayer);

            ItemStack respawnedPlayerStack = serverPlayer.getOffhandItem();
            if (respawnedPlayerStack.getItem() instanceof ContinuityEssence continuityEssenceRespawnedPlayer) {
                continuityEssenceRespawnedPlayer.decrementAbilityUseRemaining(respawnedPlayerStack, serverPlayer, 1);
            }

            NEXT_TICK_BEHAVIORS.add(new TickCapsule(() -> {
                spawnParticles(finalDestination, playerRespawnPosition, finalDestination.getRandom());
                serverPlayer.getCooldowns().addCooldown(continuityEssence, continuityEssence.getCooldownTickLength());
            }, serverPlayer.serverLevel().getGameTime() + 5));
        });

        spawnBook(serverPlayer, damageSource, oldDimension, oldPosition, finalDestination, playerRespawnPosition);
    }

    private static void spawnBook(ServerPlayer serverPlayer, DamageSource damageSource, ResourceKey<Level> oldDimension, BlockPos oldPosition, ServerLevel finalDestination, Vec3 playerRespawnPosition) {
        ItemStack newBook = Items.WRITTEN_BOOK.getDefaultInstance();

        List<Filterable<Component>> componentList = new ArrayList<>();
        Entity causer = damageSource.getEntity();
        if (causer == null) {
            componentList.add(Filterable.passThrough(Component.translatable(
                    "item.the_bumblezone.essence_continuity_written_book_body_no_causer",
                    java.time.LocalDate.now().toString(),
                    oldPosition.getX(),
                    oldPosition.getY(),
                    oldPosition.getZ(),
                    oldDimension.location().toString(),
                    getDeathMessage(finalDestination, damageSource, serverPlayer))));
        }
        else {
            componentList.add(Filterable.passThrough(Component.translatable(
                    "item.the_bumblezone.essence_continuity_written_book_body",
                    java.time.LocalDate.now().toString(),
                    oldPosition.getX(),
                    oldPosition.getY(),
                    oldPosition.getZ(),
                    oldDimension.location().toString(),
                    causer.getName(),
                    getDeathMessage(finalDestination, damageSource, serverPlayer))));
        }

        newBook.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(
            Filterable.from(FilteredText.fullyFiltered("Essence of Continuity Record")),
            serverPlayer.getName().getString(),
            0,
            componentList,
            true
        ));
        ItemEntity itementity = new ItemEntity(finalDestination,
                playerRespawnPosition.x(),
                playerRespawnPosition.y(),
                playerRespawnPosition.z(),
                newBook);
        itementity.setDefaultPickUpDelay();
        finalDestination.addFreshEntity(itementity);
    }

    public static Component getDeathMessage(ServerLevel serverLevel, DamageSource damageSource, ServerPlayer serverPlayer) {
        if (damageSource.type() == serverLevel.damageSources().generic().type()) {
            return Component.translatable("death.attack.generic", serverPlayer.getDisplayName());
        }
        DeathMessageType deathMessageType = damageSource.type().deathMessageType();
        if (deathMessageType == DeathMessageType.FALL_VARIANTS && damageSource.getEntity() != null) {
            return getFallMessage(damageSource.getEntity(), serverPlayer);
        }
        if (deathMessageType == DeathMessageType.INTENTIONAL_GAME_DESIGN) {
            String string = "death.attack." + damageSource.getMsgId();
            MutableComponent component = ComponentUtils.wrapInSquareBrackets(Component.translatable(string + ".link")).withStyle(INTENTIONAL_GAME_DESIGN_STYLE);
            return Component.translatable(string + ".message", serverPlayer.getDisplayName(), component);
        }
        return damageSource.getLocalizedDeathMessage(serverPlayer);
    }

    private static Component getFallMessage(Entity entity, ServerPlayer serverPlayer) {
        Component component = entity.getDisplayName();
        if (component != null) {
            return getMessageForAssistedFall(serverPlayer, entity, component, "death.fell.finish.item", "death.fell.finish");
        }
        return Component.translatable("death.fell.killer", entity.getDisplayName());
    }

    private static Component getMessageForAssistedFall(ServerPlayer serverPlayer, Entity entity, Component component, String string, String string2) {
        ItemStack itemStack;

        if (entity instanceof LivingEntity livingEntity) {
            itemStack = livingEntity.getMainHandItem();
        }
        else {
            itemStack = ItemStack.EMPTY;
        }

        if (!itemStack.isEmpty() && itemStack.has(DataComponents.CUSTOM_NAME)) {
            return Component.translatable(string, serverPlayer.getDisplayName(), component, itemStack.getDisplayName());
        }

        return Component.translatable(string2, serverPlayer.getDisplayName(), component);
    }

    public static void spawnParticles(ServerLevel world, Vec3 location, RandomSource random) {
        world.sendParticles(
                ParticleTypes.FIREWORK,
                location.x(),
                location.y() + 1,
                location.z(),
                100,
                random.nextGaussian() * 0.1D,
                (random.nextGaussian() * 0.1D) + 0.1,
                random.nextGaussian() * 0.1D,
                random.nextFloat() * 0.4 + 0.2f);

        world.sendParticles(
                ParticleTypes.ENCHANT,
                location.x(),
                location.y() + 1,
                location.z(),
                400,
                1,
                1,
                1,
                random.nextFloat() * 0.5 + 1.2f);
    }

    private static void playerReset(ServerPlayer player) {
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setExhaustion(0);
        player.getFoodData().eat(20, 20);
        player.clearFire();
        player.setAirSupply(player.getMaxAirSupply());
        ((ServerPlayerAccessor)player).setStartingToFallPosition(null);
        player.invulnerableTime = 40;
        player.deathTime = 0;
        player.fallDistance = 0;
        player.stopSleeping();
        player.removeVehicle();
        player.ejectPassengers();
        player.setPortalCooldown();
        player.setDeltaMovement(new Vec3(0, 0, 0));
        player.setOldPosAndRot();

        for (MobEffectInstance effect : new ArrayList<>(player.getActiveEffects())) {
            if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                player.removeEffect(effect.getEffect());
            }
        }
    }
}