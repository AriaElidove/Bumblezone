package com.telepathicgrunt.the_bumblezone.items;

import com.telepathicgrunt.the_bumblezone.entities.nonliving.ThrownStingerSpearEntity;
import com.telepathicgrunt.the_bumblezone.modinit.BzCriterias;
import com.telepathicgrunt.the_bumblezone.modinit.BzEnchantments;
import com.telepathicgrunt.the_bumblezone.modinit.BzSounds;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import com.telepathicgrunt.the_bumblezone.modules.PlayerDataHandler;
import com.telepathicgrunt.the_bumblezone.platform.ItemExtension;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class StingerSpearItem extends TridentItem implements ItemExtension {
    public static final float BASE_DAMAGE = 1F;
    public static final float BASE_THROWN_DAMAGE = 1.5F;

    public StingerSpearItem(Properties properties) {
        super(properties);
    }

    public static @NotNull ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", BASE_DAMAGE, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                    new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -1F, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 50000;
    }

    /**
     * Specify what item can repair this weapon
     */
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.is(BzTags.STINGER_SPEAR_REPAIR_ITEMS);
    }

    @Override
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int currentDuration) {
        if (livingEntity instanceof Player player) {
            int remainingDuration = this.getUseDuration(itemStack) - currentDuration;
            if (remainingDuration >= 10) {
                if (!level.isClientSide) {
                    itemStack.hurtAndBreak(1, player, livingEntity.getUsedItemHand()  == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                    ThrownStingerSpearEntity thrownStingerSpear = new ThrownStingerSpearEntity(level, player, itemStack);
                    thrownStingerSpear.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, 1.0F);
                    if (player.getAbilities().instabuild) {
                        thrownStingerSpear.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }

                    level.addFreshEntity(thrownStingerSpear);
                    level.playSound(null, thrownStingerSpear, BzSounds.STINGER_SPEAR_THROW.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        player.getInventory().removeItem(itemStack);
                    }
                }

                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (itemStack.getDamageValue() >= itemStack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(itemStack);
        }
        else {
            player.startUsingItem(interactionHand);
            return InteractionResultHolder.consume(itemStack);
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity enemy, LivingEntity user) {
        int durabilityDecrease = 1;

        if (enemy.getType().is(EntityTypeTags.UNDEAD)) {
            int potentPoisonLevel = EnchantmentHelper.getItemEnchantmentLevel(BzEnchantments.POTENT_POISON.get(), itemStack);
            enemy.addEffect(new MobEffectInstance(
                    MobEffects.POISON,
                    100 + 100 * (potentPoisonLevel - ((potentPoisonLevel - 1) / 2)),
                    potentPoisonLevel, // 0, 1, 2, 3
                    true,
                    true,
                    true));

            if (user instanceof ServerPlayer serverPlayer) {
                BzCriterias.STINGER_SPEAR_POISONING_TRIGGER.get().trigger(serverPlayer);
            }

            if (!enemy.getType().is(BzTags.PARALYZED_IMMUNE)) {
                int neuroToxinLevel = EnchantmentHelper.getItemEnchantmentLevel(BzEnchantments.NEUROTOXINS.get(), itemStack);
                if (neuroToxinLevel > 0) {
                    durabilityDecrease = 4;
                }
            }
        }

        itemStack.hurtAndBreak(durabilityDecrease, user, EquipmentSlot.MAINHAND);

        if (user instanceof ServerPlayer serverPlayer &&
            enemy.getType() == EntityType.WITHER &&
            enemy.isDeadOrDying() &&
            PlayerDataHandler.rootAdvancementDone(serverPlayer))
        {
            BzCriterias.STINGER_SPEAR_KILLED_WITH_WITHER_TRIGGER.get().trigger(serverPlayer);
        }

        return true;
    }
}
