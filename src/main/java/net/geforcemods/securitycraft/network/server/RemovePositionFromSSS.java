package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.components.GlobalPositions;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemovePositionFromSSS(GlobalPos globalPos) implements CustomPacketPayload {
	public static final Type<RemovePositionFromSSS> TYPE = new Type<>(SecurityCraft.resLoc("remove_position_from_sss"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, RemovePositionFromSSS> STREAM_CODEC = StreamCodec.composite(
			GlobalPos.STREAM_CODEC, RemovePositionFromSSS::globalPos,
			RemovePositionFromSSS::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.SONIC_SECURITY_SYSTEM_ITEM.get());

		if (!player.isSpectator() && !stack.isEmpty()) {
			GlobalPositions sssLinkedBlocks = stack.get(SCContent.SSS_LINKED_BLOCKS);

			if (sssLinkedBlocks != null)
				sssLinkedBlocks.remove(SCContent.SSS_LINKED_BLOCKS, stack, globalPos);
		}
	}
}
