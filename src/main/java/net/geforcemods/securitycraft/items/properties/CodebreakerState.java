package net.geforcemods.securitycraft.items.properties;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.components.CodebreakerData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

public record CodebreakerState(HitCheck hitCheck) {
	public static final float DEFAULT = 0.0F, DECODING = 0.25F, FAILURE = 0.5F, SUCCESS = 0.75F;

	public float get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
		CodebreakerData codebreakerData = stack.getOrDefault(SCContent.CODEBREAKER_DATA, CodebreakerData.DEFAULT);
		boolean isPlayer = entity instanceof Player;

		if ((!isPlayer || !((Player) entity).isCreative() && !((Player) entity).isSpectator()) && codebreakerData.wasRecentlyUsed())
			return codebreakerData.wasSuccessful() ? SUCCESS : FAILURE;

		if (level != null && isPlayer) {
			Player player = (Player) entity;
			BlockHitResult hitResult = HitCheck.getHitResult(level, player);

			if (hitResult != null) {
				BlockPos pos = hitResult.getBlockPos();
				BlockEntity be = level.getBlockEntity(pos);

				//if the block is not ownable/not owned by the player looking at it, don't show the indicator if it's disguised
				if (!(be instanceof IOwnable ownable) || !ownable.isOwnedBy(player)) {
					if (IDisguisable.getDisguisedBlockState(be).isPresent())
						return DEFAULT;
				}

				if (hitCheck.isValidHitResult(level, hitResult))
					return DECODING;
			}
		}

		return DEFAULT;
	}
}
