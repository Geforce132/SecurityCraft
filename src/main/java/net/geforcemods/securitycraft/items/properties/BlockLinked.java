package net.geforcemods.securitycraft.items.properties;

import net.geforcemods.securitycraft.api.IBlockMine;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.components.GlobalPositionComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

@SuppressWarnings("rawtypes")
public record BlockLinked(DataComponentType<?> positionComponent, HitCheck hitCheck) {
	public static final float NO_POSITIONS = 0.0F, UNKNOWN = 0.25F, NOT_LINKED = 0.5F, LINKED = 0.75F;

	public float get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
		if (stack.get(positionComponent) instanceof GlobalPositionComponent positions) {
			if (level != null && entity instanceof Player player) {
				BlockHitResult hitResult = HitCheck.getHitResult(level, player);

				if (hitResult != null) {
					BlockPos pos = hitResult.getBlockPos();
					BlockEntity be = level.getBlockEntity(pos);

					//if the block is not ownable/not owned by the player looking at it, don't show the indicator if it's disguised
					if (!(be instanceof IOwnable ownable) || !ownable.isOwnedBy(player)) {
						if (be.getBlockState().getBlock() instanceof IBlockMine || IDisguisable.getDisguisedBlockState(be).isPresent())
							return returnBasedOnComponent(positions);
					}

					if (hitCheck.isValidHitResult(level, hitResult)) {
						GlobalPos globalPos = new GlobalPos(level.dimension(), pos);

						return positions.isPositionAdded(globalPos) ? LINKED : NOT_LINKED;
					}
				}
			}

			return returnBasedOnComponent(positions);
		}

		return NO_POSITIONS;
	}

	private final float returnBasedOnComponent(GlobalPositionComponent positions) {
		return positions.isEmpty() ? NO_POSITIONS : UNKNOWN;
	}
}
