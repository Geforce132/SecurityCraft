package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.components.GlobalPositions;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemoveMineFromMRAT(GlobalPos globalPos) implements CustomPacketPayload {
	public static final Type<RemoveMineFromMRAT> TYPE = new Type<>(SecurityCraft.resLoc("remove_mine_from_mrat"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, RemoveMineFromMRAT> STREAM_CODEC = StreamCodec.composite(
			GlobalPos.STREAM_CODEC, RemoveMineFromMRAT::globalPos,
			RemoveMineFromMRAT::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(ctx.player(), SCContent.MINE_REMOTE_ACCESS_TOOL.get());

		if (!stack.isEmpty()) {
			GlobalPositions mines = stack.get(SCContent.BOUND_MINES);

			if (mines != null)
				mines.remove(SCContent.BOUND_MINES, stack, globalPos);
		}
	}
}
