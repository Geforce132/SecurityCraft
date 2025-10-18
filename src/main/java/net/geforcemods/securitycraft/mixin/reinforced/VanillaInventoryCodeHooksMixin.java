package net.geforcemods.securitycraft.mixin.reinforced;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ContainerOrHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaInventoryCodeHooks;

/**
 * @see HopperBlockEntityMixin
 */
@Mixin(VanillaInventoryCodeHooks.class)
public class VanillaInventoryCodeHooksMixin {
	@ModifyReturnValue(method = "getEntityContainerOrHandler", at = @At(value = "RETURN", ordinal = 0))
	private static ContainerOrHandler securitycraft$skipEntityContainer(ContainerOrHandler original, Level level, double x, double y, double z, Direction side, @Local Entity entity) {
		if (entity instanceof IOwnable) {
			ResourceHandler<ItemResource> entityCap = entity.getCapability(Capabilities.Item.ENTITY_AUTOMATION, side);

			if (entityCap != null)
				return new ContainerOrHandler(null, entityCap);
			else
				return ContainerOrHandler.EMPTY;
		}
		else
			return original;
	}
}
