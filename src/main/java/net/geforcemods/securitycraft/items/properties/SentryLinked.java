package net.geforcemods.securitycraft.items.properties;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.NamedPositions;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record SentryLinked() {
	public static final float NO_POSITIONS = 0.0F, UNKNOWN = 0.25F, NOT_LINKED = 0.5F, LINKED = 0.75F;

	public static float get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
		NamedPositions positions = stack.get(SCContent.BOUND_SENTRIES);

		if (positions != null) {
			if (level != null && entity instanceof Player && Minecraft.getInstance().crosshairPickEntity instanceof Sentry sentry) {
				GlobalPos globalPos = new GlobalPos(level.dimension(), sentry.blockPosition());

				return positions.isPositionAdded(globalPos) ? LINKED : NOT_LINKED;
			}

			return positions.isEmpty() ? NO_POSITIONS : UNKNOWN;
		}

		return NO_POSITIONS;
	}
}
