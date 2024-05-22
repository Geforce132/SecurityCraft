package net.geforcemods.securitycraft.mixin.boat;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.entity.SecuritySeaBoat;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

/**
 * Forge does not call {@link ICapabilityProvider#getCapability) for entities. This mixin adds a call for the security sea
 * boat's cap
 */
@Mixin(VanillaInventoryCodeHooks.class)
public class VanillaInventoryCodeHooksMixin {
	@Inject(method = "getItemHandler(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/core/Direction;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Ljava/util/Optional;empty()Ljava/util/Optional;"), cancellable = true)
	private static void securitycraft$callGetCapabilityForSecuritySeaBoat(Level level, double x, double y, double z, Direction side, CallbackInfoReturnable<Optional<Pair<IItemHandler, Object>>> cir) {
		List<SecuritySeaBoat> list = level.getEntitiesOfClass(SecuritySeaBoat.class, new AABB(x, y, z, x, y, z).inflate(0.5D), EntitySelector.ENTITY_STILL_ALIVE);

		if (!list.isEmpty()) {
			SecuritySeaBoat boat = list.get(level.random.nextInt(list.size()));
			LazyOptional<IItemHandler> entityCap = boat.getCapability(ForgeCapabilities.ITEM_HANDLER, side);

			cir.setReturnValue(entityCap.map(capability -> ImmutablePair.<IItemHandler, Object>of(capability, boat)));
		}
	}
}
