package com.telepathicgrunt.the_bumblezone.items.dispenserbehavior;

import com.telepathicgrunt.the_bumblezone.blocks.StringCurtain;
import com.telepathicgrunt.the_bumblezone.mixin.blocks.DefaultDispenseItemBehaviorInvoker;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;


public class StringDispenseBehavior extends DefaultDispenseItemBehavior {
    public static DispenseItemBehavior DEFAULT_STRING_DISPENSE_BEHAVIOR;
    public static DefaultDispenseItemBehavior DROP_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior();

    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    @Override
    public ItemStack execute(BlockSource source, ItemStack stack) {
        ServerLevel world = source.level();
        Position dispensePosition = DispenserBlock.getDispensePosition(source);
        BlockPos dispenseBlockPos = BlockPos.containing(dispensePosition);
        BlockState blockstate = world.getBlockState(dispenseBlockPos);

        if (blockstate.is(BzTags.STRING_CURTAINS)) {
            boolean success = StringCurtain.extendCurtainIfPossible(blockstate, world, dispenseBlockPos);
            if (success) {
                stack.shrink(1);
            }
        }
        else {
            // If it instanceof DefaultDispenseItemBehavior, call dispenseStack directly to avoid
            // playing particles and sound twice due to dispense method having that by default.
            if(DEFAULT_STRING_DISPENSE_BEHAVIOR instanceof DefaultDispenseItemBehavior) {
                return ((DefaultDispenseItemBehaviorInvoker) DEFAULT_STRING_DISPENSE_BEHAVIOR).invokeExecute(source, stack);
            }
            else {
                // Fallback to dispense as someone chose to make a custom class without dispenseStack.
                return DEFAULT_STRING_DISPENSE_BEHAVIOR.dispense(source, stack);
            }
        }

        return stack;
    }


    /**
     * Play the dispense sound from the specified block.
     */
    @Override
    protected void playSound(BlockSource source) {
        source.level().levelEvent(1002, source.pos(), 0);
    }
}
