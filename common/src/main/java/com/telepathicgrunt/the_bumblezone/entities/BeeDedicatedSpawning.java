package com.telepathicgrunt.the_bumblezone.entities;

import com.telepathicgrunt.the_bumblezone.configs.BzGeneralConfigs;
import com.telepathicgrunt.the_bumblezone.modinit.BzEntities;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import com.telepathicgrunt.the_bumblezone.utils.PlatformHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;

public final class BeeDedicatedSpawning {
    private BeeDedicatedSpawning() {}

    public static void specialSpawnBees(ServerLevel world) {
        int despawnDistance = 80;
        int entityCountChange = 0;
        Set<Bee> allWildBees = GeneralUtils.getAllWildBees();
        List<ServerPlayer> serverPlayers = world.players();

        // Remove all wild bees too far from a player.
        for(Bee wildBee : allWildBees) {
            boolean isTooFar = true;
            
            for (ServerPlayer serverPlayer : serverPlayers) {
                if (PlatformHooks.isFakePlayer(serverPlayer)) {
                    continue;
                }

                if (wildBee.position().subtract(serverPlayer.position()).length() <= despawnDistance) {
                    isTooFar = false;
                    break;
                }
            }

            if (isTooFar) {
                wildBee.remove(Entity.RemovalReason.DISCARDED);
                entityCountChange--;
            }
        }

        int beesPerPlayer = BzGeneralConfigs.nearbyBeesPerPlayerInBz;
        int maxWildBeeLimit = beesPerPlayer * serverPlayers.size();
        if(allWildBees.size() <= maxWildBeeLimit) {
            for(ServerPlayer serverPlayer : serverPlayers) {
                if (PlatformHooks.isFakePlayer(serverPlayer)) {
                    continue;
                }

                int nearbyBees = 0;
                for (Entity entity : world.getEntities(serverPlayer, serverPlayer.getBoundingBox().inflate(despawnDistance, despawnDistance, despawnDistance))) {
                    if (entity instanceof Bee) {
                        nearbyBees++;
                    }
                }

                for (int i = nearbyBees; i <= beesPerPlayer; i++) {
                    BlockPos newBeePos = GeneralUtils.getRandomBlockposWithinRange(serverPlayer, 45, 20);
                    if(!world.getBlockState(newBeePos).isAir()) {
                        continue;
                    }

                    Bee newBee = (BzGeneralConfigs.variantBeeTypes.size() > 0 && world.getRandom().nextFloat() < 0.05f) ?
                            BzEntities.VARIANT_BEE.get().create(world) : EntityType.BEE.create(world);

                    newBee.setPos(Vec3.atCenterOf(newBeePos));
                    newBee.setDeltaMovement(new Vec3(0, 1D, 0));
                    newBee.setSpeed(0);
                    newBee.finalizeSpawn(world, world.getCurrentDifficultyAt(newBee.blockPosition()), MobSpawnType.NATURAL, null);

                    PlatformHooks.finalizeSpawn(newBee, world, null, MobSpawnType.NATURAL);
                    world.addFreshEntity(newBee);
                    entityCountChange++;
                }
            }
        }

        GeneralUtils.adjustEntityCountInBz(entityCountChange);
    }
}
