package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.PayBlockBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TogglePayBlock(BlockPos pos, int transactions) implements CustomPacketPayload {
	public static final Type<TogglePayBlock> TYPE = new Type<>(SecurityCraft.resLoc("toggle_pay_block"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, TogglePayBlock> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, TogglePayBlock::pos,
			ByteBufCodecs.VAR_INT, TogglePayBlock::transactions,
			TogglePayBlock::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();

		if (player.level().getBlockEntity(pos) instanceof PayBlockBlockEntity be)
			be.doTransaction(player, transactions);
	}
}
