package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BlockPocketManagerFailedActivation(BlockPos pos) implements CustomPacketPayload {
	public static final Type<BlockPocketManagerFailedActivation> TYPE = new Type<>(SecurityCraft.resLoc("block_pocket_manager_failed_activation"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, BlockPocketManagerFailedActivation> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, BlockPocketManagerFailedActivation::pos,
			BlockPocketManagerFailedActivation::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Level level = ctx.player().level();

		if (level.getBlockEntity(pos) instanceof BlockPocketManagerBlockEntity be)
			be.setEnabled(false);
	}
}
