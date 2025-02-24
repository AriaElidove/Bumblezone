package com.telepathicgrunt.the_bumblezone.blocks;

import com.mojang.serialization.MapCodec;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.configs.BzBeeAggressionConfigs;
import com.telepathicgrunt.the_bumblezone.items.essence.EssenceOfTheBees;
import com.telepathicgrunt.the_bumblezone.modinit.BzBlocks;
import com.telepathicgrunt.the_bumblezone.modinit.BzCriterias;
import com.telepathicgrunt.the_bumblezone.modinit.BzEffects;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;


public class FilledPorousHoneycomb extends Block {

    public static final MapCodec<FilledPorousHoneycomb> CODEC = Block.simpleCodec(FilledPorousHoneycomb::new);

    public FilledPorousHoneycomb() {
        this(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_ORANGE)
                .instrument(NoteBlockInstrument.BANJO)
                .strength(0.5F, 0.5F)
                .sound(SoundType.CORAL_BLOCK)
                .speedFactor(0.8F));
    }

    public FilledPorousHoneycomb(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends FilledPorousHoneycomb> codec() {
        return CODEC;
    }


    /**
     * Allow player to harvest honey and put honey into this block using bottles
     */
    @Override
    public ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level world, BlockPos position, Player playerEntity, InteractionHand playerHand, BlockHitResult raytraceResult) {
        /*
         * Player is harvesting the honey from this block if it is filled with honey
         */
        if (itemStack.getItem() == Items.GLASS_BOTTLE) {
            world.setBlock(position, BzBlocks.POROUS_HONEYCOMB.get().defaultBlockState(), 3); // removed honey from this block
            GeneralUtils.givePlayerItem(playerEntity, playerHand, new ItemStack(Items.HONEY_BOTTLE), false, true);

            Level level = playerEntity.level();
            if ((level.dimension().location().equals(Bumblezone.MOD_DIMENSION_ID) ||
                    BzBeeAggressionConfigs.allowWrathOfTheHiveOutsideBumblezone) &&
                    !playerEntity.isCreative() &&
                    !playerEntity.isSpectator() &&
                    BzBeeAggressionConfigs.aggressiveBees)
            {
                Registry<MobEffect> mobEffects = level.registryAccess().registryOrThrow(Registries.MOB_EFFECT);
                boolean hasProtection = playerEntity.hasEffect(mobEffects.getHolder(BzEffects.PROTECTION_OF_THE_HIVE.getId()).get());
                if(!hasProtection &&
                    playerEntity instanceof ServerPlayer serverPlayer &&
                    !EssenceOfTheBees.hasEssence(serverPlayer) &&
                    world.getDifficulty() != Difficulty.PEACEFUL)
                {
                    Component message = Component.translatable("system.the_bumblezone.no_protection").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RED);
                    serverPlayer.displayClientMessage(message, true);

                    //Now all bees nearby in Bumblezone will get VERY angry!!!
                    playerEntity.addEffect(new MobEffectInstance(mobEffects.getHolder(BzEffects.WRATH_OF_THE_HIVE.getId()).get(), BzBeeAggressionConfigs.howLongWrathOfTheHiveLasts, 2, false, BzBeeAggressionConfigs.showWrathOfTheHiveParticles, true));
                }

                if (hasProtection && playerEntity instanceof ServerPlayer serverPlayer) {
                    BzCriterias.HONEY_PERMISSION_TRIGGER.get().trigger(serverPlayer);
                }
            }

            return ItemInteractionResult.SUCCESS;
        }

        return super.useItemOn(itemStack, blockState, world, position, playerEntity, playerHand, raytraceResult);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos blockPos, Entity entity) {
        beeHoneyTake(state, level, blockPos, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState state, Entity entity) {
        beeHoneyTake(state, level, blockPos, entity);
    }

    public static void beeHoneyTake(BlockState state, Level level, BlockPos blockPos, Entity entity) {
        if(entity instanceof Bee beeEntity &&
            !beeEntity.isDeadOrDying() &&
            beeEntity.getHealth() < beeEntity.getMaxHealth() &&
            state.is(BzBlocks.FILLED_POROUS_HONEYCOMB.get()))
        {
            beeEntity.heal(Math.min(4, beeEntity.getMaxHealth() - beeEntity.getHealth()));
            level.setBlock(blockPos, BzBlocks.POROUS_HONEYCOMB.get().defaultBlockState(), 3);

            if(level.isClientSide()) return;

            Vec3 centerOfBee = beeEntity.getBoundingBox().getCenter();
            double spread = 0.25D;
            ((ServerLevel)level).sendParticles(
                    ParticleTypes.FALLING_HONEY,
                    centerOfBee.x(),
                    centerOfBee.y(),
                    centerOfBee.z(),
                    10,
                    spread,
                    spread,
                    spread,
                    0f
            );
        }
    }

    /**
     * Called periodically clientside on blocks near the player to show honey particles. 50% of attempting to spawn a
     * particle
     */
    @Override
    public void animateTick(BlockState blockState, Level world, BlockPos position, RandomSource random) {
        //number of particles in this tick
        for (int i = 0; i < random.nextInt(2); ++i) {
            this.spawnHoneyParticles(world, random, position, blockState);
        }
    }


    /**
     * tell redstone that this can be use with comparator
     */
    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }


    /**
     * the power fed into comparator 1
     */
    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return 1;
    }

    /**
     * Starts checking if the block can take the particle and if so and it passes another rng to reduce spawnrate, it then
     * takes the block's dimensions and passes into methods to spawn the actual particle
     */
    private void spawnHoneyParticles(Level world, RandomSource random, BlockPos position, BlockState blockState) {
        if (random.nextFloat() < 0.08F) {
            VoxelShape currentBlockShape = blockState.getCollisionShape(world, position);
            double yEndHeight = currentBlockShape.max(Direction.Axis.Y);
            if (yEndHeight >= 1.0D && !blockState.is(BlockTags.IMPERMEABLE)) {
                double yStartHeight = currentBlockShape.min(Direction.Axis.Y);
                if (yStartHeight > 0.0D) {
                    this.addHoneyParticle(world, random, position, currentBlockShape, position.getY() + yStartHeight - 0.05D);
                }
                else {
                    BlockPos belowBlockpos = position.below();
                    BlockState belowBlockstate = world.getBlockState(belowBlockpos);
                    VoxelShape belowBlockShape = belowBlockstate.getCollisionShape(world, belowBlockpos);
                    double yEndHeight2 = belowBlockShape.max(Direction.Axis.Y);
                    if ((yEndHeight2 < 1.0D || !belowBlockstate.isSolidRender(world, belowBlockpos)) && belowBlockstate.getFluidState().isEmpty()) {
                        this.addHoneyParticle(world, random, position, currentBlockShape, position.getY() - 0.05D);
                    }
                }
            }

        }
    }


    /**
     * intermediary method to apply the blockshape and ranges that the particle can spawn in for the next addHoneyParticle
     * method
     */
    private void addHoneyParticle(Level world, RandomSource random, BlockPos blockPos, VoxelShape blockShape, double height) {
        this.addHoneyParticle(
                world,
                random,
                blockPos.getX() + blockShape.min(Direction.Axis.X),
                blockPos.getX() + blockShape.max(Direction.Axis.X),
                blockPos.getZ() + blockShape.min(Direction.Axis.Z),
                blockPos.getZ() + blockShape.max(Direction.Axis.Z),
                height);
    }


    /**
     * Adds the actual honey particle into the world within the given range
     */
    private void addHoneyParticle(Level world, RandomSource random, double xMin, double xMax, double zMax, double zMin, double yHeight) {
        world.addParticle(ParticleTypes.DRIPPING_HONEY, Mth.lerp(random.nextDouble(), xMin, xMax), yHeight, Mth.lerp(random.nextDouble(), zMax, zMin), 0.0D, 0.0D, 0.0D);
    }
}
