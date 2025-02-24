package com.telepathicgrunt.the_bumblezone.enchantments;

import com.telepathicgrunt.the_bumblezone.configs.BzGeneralConfigs;
import com.telepathicgrunt.the_bumblezone.events.player.PlayerBreakSpeedEvent;
import com.telepathicgrunt.the_bumblezone.modinit.BzCriterias;
import com.telepathicgrunt.the_bumblezone.modinit.BzEnchantments;
import com.telepathicgrunt.the_bumblezone.utils.EnchantmentUtils;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;

public class CombCutterEnchantmentApplication {
    private static final GeneralUtils.Lazy<Set<Block>> TARGET_BLOCKS = new GeneralUtils.Lazy<>();
    private static final GeneralUtils.Lazy<Set<Block>> LESSER_TARGET_BLOCKS = new GeneralUtils.Lazy<>();

    public static int getCombCutterEnchantLevel(ItemStack stack, Level level) {
        Holder<Enchantment> ncombCutterurotoxin = EnchantmentUtils.getEnchantmentHolder(BzEnchantments.COMB_CUTTER, level);
        return Math.min(EnchantmentHelper.getItemEnchantmentLevel(ncombCutterurotoxin, stack), BzGeneralConfigs.neurotoxinMaxLevel);
    }

    public static Set<Block> getTargetBlocks() {
        return TARGET_BLOCKS.getOrCompute(() -> {
            Set<Block> validBlocks = new HashSet<>();
            for (ResourceLocation key : BuiltInRegistries.BLOCK.keySet()) {
                String path = key.getPath();
                if(path.contains("comb") || path.contains("luminescent_wax") || path.contains("ancient_wax")) {
                    validBlocks.add(BuiltInRegistries.BLOCK.get(key));
                }
            }
            return validBlocks;
        });
    }

    public static Set<Block> getLesserTargetBlocks() {
        return LESSER_TARGET_BLOCKS.getOrCompute(() -> {
            Set<Block> validBlocks = new HashSet<>();
            for (ResourceLocation key : BuiltInRegistries.BLOCK.keySet()) {
                String path = key.getPath();
                Block block = BuiltInRegistries.BLOCK.get(key);
                if (block instanceof BeehiveBlock || path.contains("hive") || path.contains("nest") || (path.contains("wax") && !path.contains("waxed"))) {
                    validBlocks.add(block);
                }
            }
            return validBlocks;
        });
    }

    public static void attemptFasterMining(PlayerBreakSpeedEvent event){
        if (getTargetBlocks().contains(event.state().getBlock())){
            mineFaster(event, false);
        }
        else if (getLesserTargetBlocks().contains(event.state().getBlock())){
            mineFaster(event, true);
        }
    }

    private static void mineFaster(PlayerBreakSpeedEvent event, boolean lesserTarget) {
        Player playerEntity = event.player();
        ItemStack itemStack = playerEntity.getMainHandItem();
        int equipmentLevel = EnchantmentHelper.getEnchantmentLevel(EnchantmentUtils.getEnchantmentHolder(BzEnchantments.COMB_CUTTER, playerEntity.level()), playerEntity);
        if (equipmentLevel > 0 && !itemStack.isEmpty()) {
            double newSpeed = (equipmentLevel * equipmentLevel) + (lesserTarget ? 3 : 13);

            if (playerEntity.hasEffect(MobEffects.DIG_SLOWDOWN)) {
                int amplifier = playerEntity.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier();
                if (amplifier < 0) {
                    amplifier = 3;
                }
                newSpeed /= ((Math.pow(amplifier, 6) + 2) * 20);
            }

            event.speed().addAndGet(newSpeed);
        }
    }

    public static void increasedCombDrops(Player playerEntity, Level world, BlockPos pos) {
        ItemStack itemStack = playerEntity.getMainHandItem();
        int equipmentLevel = EnchantmentHelper.getEnchantmentLevel(EnchantmentUtils.getEnchantmentHolder(BzEnchantments.COMB_CUTTER, playerEntity.level()), playerEntity);
        if (equipmentLevel > 0 && !itemStack.isEmpty()) {
            Block.popResource(world, pos, new ItemStack(Items.HONEYCOMB, equipmentLevel * 3));
            if(playerEntity instanceof ServerPlayer serverPlayer) {
                BzCriterias.COMB_CUTTER_EXTRA_DROPS_TRIGGER.get().trigger(serverPlayer);
            }
        }
    }
}
