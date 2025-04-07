package net.geforcemods.securitycraft.mixin.reinforced;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

/**
 * The vanilla hopper always checks for Containers first when trying to insert/extract. Since SecurityCraft's block entities
 * extend vanilla's, they must also implement Container. Due to ownership this is undesirable, since only blocks by the same
 * owner should be able to extract somewhere. This mixin makes sure that the hopper never considers the Container for SC's
 * blocks.
 */
@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
	@ModifyReturnValue(method = "getBlockContainer", at = @At(value = "RETURN", ordinal = 1))
	private static Container securitycraft$skipBlockContainer(Container original, @Local BlockEntity be) {
		if (be instanceof IOwnable)
			return null;
		else
			return original;
	}

	@ModifyReturnValue(method = "getEntityContainer", at = @At("RETURN"))
	private static Container securitycraft$skipEntityContainer(Container original) {
		if (original instanceof IOwnable)
			return null;
		else
			return original;
	}
}
