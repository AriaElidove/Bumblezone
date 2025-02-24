package com.telepathicgrunt.the_bumblezone.modcompat;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.enchantments.CombCutterEnchantmentApplication;
import com.telepathicgrunt.the_bumblezone.entities.teleportation.BzWorldSavedData;
import com.telepathicgrunt.the_bumblezone.entities.teleportation.EntityTeleportationHookup;
import com.telepathicgrunt.the_bumblezone.items.essence.EssenceOfTheBees;
import com.telepathicgrunt.the_bumblezone.modinit.BzCriterias;
import com.telepathicgrunt.the_bumblezone.modinit.BzDimension;
import com.telepathicgrunt.the_bumblezone.modinit.BzEnchantments;
import com.telepathicgrunt.the_bumblezone.utils.EnchantmentUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

/**
 * For mods that want to use Bumblezone methods but would like to make sure it doesn't get changed over time and break the dependent mods.
 * Idk what methods to put here lol. Just contact me if there are methods you want exposed to go here.
 * <p>
 * Basically these methods and class path will not change making this class and its methods safer to use in other mods.
 */
public class BumblezoneAPI {
    /**
     * ID of the dimension for Bumblezone
     */
    public static final ResourceLocation MOD_DIMENSION_ID = ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, Bumblezone.MODID);

    // Requiem mod uses this method
    /**
     * Will teleport the given entity out of Bumblezone to the last dimension they came from.
     * Position is determined by the entity's Bumblezone position scaled like how the Nether is 8:1.
     * If the entity does not have a previous dimension stored, the Overworld will be the destination instead.
     */
    public static void teleportOutOfBz(LivingEntity livingEntity) {
        EntityTeleportationHookup.teleportOutOfBz(livingEntity);
    }

    /**
     * Will check if the projectile entity hit a beehive or bee nest and teleport the thrower to Bumblezone.
     * Please only call this for throwables that teleport the entity like Enderpearls.
     * @return - Whether the projectile hit a beehive or bee nest.
     */
    public static boolean runEnderpearlImpact(HitResult hitResult, Entity thrower, Projectile projectile) {
        return EntityTeleportationHookup.runTeleportProjectileImpact(hitResult, thrower, projectile);
    }

    /**
     * Will check if the projectile entity hit a beehive or bee nest and teleport the thrower to Bumblezone.
     * Please only call this for throwables that teleport the entity like Enderpearls.
     * @return - Whether the projectile hit a beehive or bee nest.
     */
    public static boolean runEnderpearlImpact(HitResult hitResult, @NotNull Projectile projectile) {
        return EntityTeleportationHookup.runTeleportProjectileImpact(hitResult, projectile.getOwner(), projectile);
    }

    /**
     * Will check if the clicked position is a beehive or bee nest and teleport the thrower to Bumblezone.
     * Please only call this for right click item use methods that teleport the entity like Traveler Anchors's staffs.
     * @return - Whether the projectile hit a beehive or bee nest.
     */
    public static boolean runItemUseOn(Player user, BlockPos clickedPos, ItemStack usingStack) {
        return EntityTeleportationHookup.runItemUseOn(user, clickedPos, usingStack);
    }

    /**
     * Will check if the passed in position is a beehive or bee nest and teleport the thrower to Bumblezone.
     * Last resort method to call if none of the other methods fits your case.
     * @return - Whether the projectile hit a beehive or bee nest.
     */
    public static boolean runGenericTeleport(Player user, BlockPos targetPos) {
        return EntityTeleportationHookup.runGenericTeleport(user, targetPos);
    }

    /**
     * Will check if entity is pushed by piston into a beehive or bee nest and teleport to Bumblezone if so.
     */
    public static void runPistonPushed(Direction direction, LivingEntity livingEntity) {
        EntityTeleportationHookup.runPistonPushed(direction, livingEntity);
    }

    /**
     * Trigger Bumblezone's advancement for teleporting into the Bumblezone dimension as if you used an Ender Pearl.
     */
    public static void triggerEnderPearlAdvancement(ServerPlayer serverPlayer) {
        BzCriterias.TELEPORT_TO_BUMBLEZONE_PEARL_TRIGGER.get().trigger(serverPlayer);
    }

    /**
     * Trigger Bumblezone's advancement for teleporting into the Bumblezone dimension as if you used a Piston.
     */
    public static void triggerPistonAdvancement(ServerPlayer serverPlayer) {
        BzCriterias.TELEPORT_TO_BUMBLEZONE_PISTON_TRIGGER.get().trigger(serverPlayer);
    }

    /**
     * Trigger Bumblezone's advancement for teleporting out of the Bumblezone dimension.
     */
    public static void triggerExitingBumblezoneAdvancement(ServerPlayer serverPlayer) {
        BzCriterias.TELEPORT_OUT_OF_BUMBLEZONE_TRIGGER.get().trigger(serverPlayer);
    }

    /**
     * Will queue up the entity for Teleporting into the Bumblezone dimension.
     * On next world tick, the entity and its passengers will be teleported safely.
     */
    public static void queueEntityForTeleportingToBumblezone(Entity entity) {
        BzWorldSavedData.queueEntityToTeleport(entity, BzDimension.BZ_WORLD_KEY);
    }

    /**
     * Will queue up the entity for Teleporting out of the Bumblezone dimension.
     * On next world tick, the entity and its passengers will be teleported safely.
     */
    public static void queueEntityForTeleportingOutOfBumblezone(Entity entity, ResourceKey<Level> worldToTeleportTo) {
        BzWorldSavedData.queueEntityToTeleport(entity, worldToTeleportTo);
    }

    /**
     * Returns true if the given serverplayer has Bee Essence.
     * Best used for making bees not attack player when player mines modded hives or takes honey/shears modded hives.
     */
    public static boolean playerHasBeeEssence(ServerPlayer serverPlayer) {
        return EssenceOfTheBees.hasEssence(serverPlayer);
    }

    /**
     * Trigger Bumblezone's advancement for getting more combs from a hive by shearing with a tool that has Comb Cutter enchantment.
     */
    public static void triggerCombCutterExtraDropAdvancement(ServerPlayer serverPlayer) {
        BzCriterias.COMB_CUTTER_EXTRA_DROPS_TRIGGER.get().trigger(serverPlayer);
    }

    /**
     * Returns what level Comb Cutter enchantment the passed in itemstack has.
     */
    public static int getCombCutterLevelForItem(ItemStack toolUsed, Level level) {
        return CombCutterEnchantmentApplication.getCombCutterEnchantLevel(toolUsed, level);
    }

    /**
     * Returns what level Comb Cutter enchantment the currently used itemstack has for the passed in player.
     */
    public static int getCombCutterLevelForPlayer(ServerPlayer serverPlayer) {
        return EnchantmentHelper.getEnchantmentLevel(EnchantmentUtils.getEnchantmentHolder(BzEnchantments.COMB_CUTTER, serverPlayer.level()), serverPlayer);
    }

}
