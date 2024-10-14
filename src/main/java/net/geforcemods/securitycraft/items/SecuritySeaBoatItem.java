package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.entity.AbstractSecuritySeaBoat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SecuritySeaBoatItem extends BoatItem {
	private final EntityType<? extends AbstractSecuritySeaBoat> entityType;

	public SecuritySeaBoatItem(EntityType<? extends AbstractSecuritySeaBoat> entityType, Item.Properties properties) {
		super(entityType, properties);
		this.entityType = entityType;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);

		if (!level.getFluidState(hitResult.getBlockPos()).is(FluidTags.LAVA))
			return super.use(level, player, hand);
		else
			return InteractionResult.FAIL;
	}

	@Override
	public AbstractBoat getBoat(Level level, HitResult hitResult, ItemStack stack, Player player) {
		AbstractSecuritySeaBoat boat = entityType.create(level, EntitySpawnReason.SPAWN_ITEM_USE);

		if (boat != null) {
			Vec3 vec3 = hitResult.getLocation();

			boat.setInitialPos(vec3.x, vec3.y, vec3.z);

			if (level instanceof ServerLevel serverLevel)
				EntityType.<AbstractBoat>createDefaultStackConfig(serverLevel, stack, player).accept(boat);
		}

		boat.setOwner(player);
		return boat;
	}
}
