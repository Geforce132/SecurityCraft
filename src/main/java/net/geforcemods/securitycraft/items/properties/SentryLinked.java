package net.geforcemods.securitycraft.items.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.NamedPositions;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record SentryLinked() implements SelectItemModelProperty<String> {
	public static final String NO_POSITIONS = "no_positions", UNKNOWN = "unknown", NOT_LINKED = "not_linked", LINKED = "linked";
	public static final SelectItemModelProperty.Type<SentryLinked, String> TYPE = SelectItemModelProperty.Type.create(MapCodec.unit(new SentryLinked()), Codec.STRING);

	@Override
	public String get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed, ItemDisplayContext ctx) {
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

	@Override
	public Type<? extends SelectItemModelProperty<String>, String> type() {
		return TYPE;
	}
}
