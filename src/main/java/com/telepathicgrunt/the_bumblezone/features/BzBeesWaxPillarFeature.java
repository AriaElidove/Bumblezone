package com.telepathicgrunt.the_bumblezone.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.the_bumblezone.modCompat.ResourcefulBeesRedirection;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class BzBeesWaxPillarFeature extends Feature<NoFeatureConfig> {
   public BzBeesWaxPillarFeature(Codec<NoFeatureConfig> noFeatureConfigCodec) {
      super(noFeatureConfigCodec);
   }

   public boolean generate(ISeedReader world, ChunkGenerator chunkGenerator, Random random,
                           BlockPos pos, NoFeatureConfig noFeatureConfig)
   {
      if (world.getBlockState(pos) == Blocks.CAVE_AIR.getDefaultState() && !world.isAirBlock(pos.up())) {
         makeColumn(Direction.DOWN, world, random, pos);
         makeColumn(Direction.UP, world, random, pos);
         return true;
      } else {
         return false;
      }
   }

   private void makeColumn(Direction direction, ISeedReader world, Random random, BlockPos pos){

      BlockPos.Mutable blockpos$mutable = pos.mutableCopy();
      BlockPos.Mutable blockpos$mutable1 = pos.mutableCopy();
      boolean flag = true;
      boolean flag1 = true;
      boolean flag2 = true;
      boolean flag3 = true;

      while(!world.getBlockState(blockpos$mutable).isSolid() || world.getBlockState(blockpos$mutable).getBlock() == ResourcefulBeesRedirection.getRBBeesWaxBlock().getBlock()) {
         if (World.isOutsideBuildHeight(blockpos$mutable)) {
            return;
         }

         world.setBlockState(blockpos$mutable, ResourcefulBeesRedirection.getRBBeesWaxBlock(), 2);
         flag = flag && this.stopOrPlaceWax(world, random, blockpos$mutable1.set(blockpos$mutable, Direction.NORTH));
         flag1 = flag1 && this.stopOrPlaceWax(world, random, blockpos$mutable1.set(blockpos$mutable, Direction.SOUTH));
         flag2 = flag2 && this.stopOrPlaceWax(world, random, blockpos$mutable1.set(blockpos$mutable, Direction.WEST));
         flag3 = flag3 && this.stopOrPlaceWax(world, random, blockpos$mutable1.set(blockpos$mutable, Direction.EAST));
         blockpos$mutable.move(direction);
      }

      blockpos$mutable.move(direction.getOpposite());
      this.tryPlaceWax(world, random, blockpos$mutable1.set(blockpos$mutable, Direction.NORTH));
      this.tryPlaceWax(world, random, blockpos$mutable1.set(blockpos$mutable, Direction.SOUTH));
      this.tryPlaceWax(world, random, blockpos$mutable1.set(blockpos$mutable, Direction.WEST));
      this.tryPlaceWax(world, random, blockpos$mutable1.set(blockpos$mutable, Direction.EAST));
      blockpos$mutable.move(direction);
      BlockPos.Mutable blockpos$mutable2 = new BlockPos.Mutable();

      for(int x = -6; x < 7; ++x) {
         for(int z = -6; z < 7; ++z) {
            int radius = MathHelper.abs(x) * MathHelper.abs(z);
            if (random.nextInt(36) < 36 - radius) {
               blockpos$mutable2.setPos(blockpos$mutable.add(x, 0, z));
               int y = 3;

               while(!world.getBlockState(blockpos$mutable1.set(blockpos$mutable2, direction)).isSolid()) {
                  blockpos$mutable2.move(direction);
                  --y;
                  if (y <= 0) {
                     break;
                  }
               }

               if (world.getBlockState(blockpos$mutable1.set(blockpos$mutable2, direction)).isSolid()) {
                  world.setBlockState(blockpos$mutable2, ResourcefulBeesRedirection.getRBBeesWaxBlock(), 2);
               }
            }
         }
      }
   }

   private void tryPlaceWax(IWorld world, Random random, BlockPos pos) {
      if (random.nextBoolean()) {
         world.setBlockState(pos, ResourcefulBeesRedirection.getRBBeesWaxBlock(), 2);
      }

   }

   private boolean stopOrPlaceWax(IWorld world, Random random, BlockPos pos) {
      if (random.nextInt(6) != 0) {
         world.setBlockState(pos, ResourcefulBeesRedirection.getRBBeesWaxBlock(), 2);
         return true;
      } else {
         return false;
      }
   }
}
