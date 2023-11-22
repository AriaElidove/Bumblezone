
package com.telepathicgrunt.the_bumblezone.modcompat.neoforge;

import com.telepathicgrunt.the_bumblezone.entities.teleportation.EntityTeleportationHookup;
import com.telepathicgrunt.the_bumblezone.modcompat.ModChecker;
import com.telepathicgrunt.the_bumblezone.modcompat.ModCompat;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.EnumSet;

public class DragonEnchantsCompat implements ModCompat {
	private static final String END_STEP_ENCHANT_ATTACHED_TAG = "dragonenchants:end_step";
	private static final ResourceLocation END_STEP_RL = new ResourceLocation("dragonenchants", "end_step");

	public DragonEnchantsCompat() {
		// Keep at end so it is only set to true if no exceptions was thrown during setup
		ModChecker.dragonEnchantsPresent = true;
	}

	@Override
	public EnumSet<Type> compatTypes() {
		return EnumSet.of(Type.PROJECTILE_IMPACT_HANDLED);
	}

	public InteractionResult isProjectileTeleportHandled(HitResult hitResult, Entity owner, Projectile projectile) {
		if (hitResult instanceof BlockHitResult blockHitResult &&
			projectile != null &&
			projectile.getPersistentData().getBoolean(END_STEP_ENCHANT_ATTACHED_TAG) &&
			GeneralUtils.isInTag(BuiltInRegistries.ENCHANTMENT, BzTags.ENCHANT_SPECIAL_DEDICATED_COMPAT, BuiltInRegistries.ENCHANTMENT.get(END_STEP_RL)))
		{
			return EntityTeleportationHookup.runTeleportProjectileImpact(blockHitResult, owner, projectile) ? InteractionResult.SUCCESS : InteractionResult.PASS;
		}
		return InteractionResult.PASS;
	}
}
