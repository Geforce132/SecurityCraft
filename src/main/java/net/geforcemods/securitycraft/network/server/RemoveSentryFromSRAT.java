package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.components.NamedPositions;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemoveSentryFromSRAT(GlobalPos globalPos) implements CustomPacketPayload {
	public static final Type<RemoveSentryFromSRAT> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "remove_sentry_from_srat"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, RemoveSentryFromSRAT> STREAM_CODEC = StreamCodec.composite(
			GlobalPos.STREAM_CODEC, RemoveSentryFromSRAT::globalPos,
			RemoveSentryFromSRAT::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.SENTRY_REMOTE_ACCESS_TOOL.get());

		if (!stack.isEmpty()) {
			NamedPositions sentries = stack.get(SCContent.BOUND_SENTRIES);

			if (sentries != null)
				sentries.remove(SCContent.BOUND_SENTRIES, stack, globalPos);
		}
	}
}
