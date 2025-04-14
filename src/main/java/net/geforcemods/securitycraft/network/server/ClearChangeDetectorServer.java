package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blocks.AbstractPanelBlock;
import net.geforcemods.securitycraft.blocks.BlockChangeDetectorBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClearChangeDetectorServer(BlockPos pos) implements CustomPacketPayload {
	public static final Type<ClearChangeDetectorServer> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "clear_change_detector_server"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, ClearChangeDetectorServer> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ClearChangeDetectorServer::pos,
			ClearChangeDetectorServer::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();

		if (!player.isSpectator() && level.getBlockEntity(pos) instanceof BlockChangeDetectorBlockEntity be && be.isOwnedBy(player)) {
			BlockState state = be.getBlockState();

			be.getEntries().clear();
			be.setChanged();
			level.sendBlockUpdated(pos, state, state, 2);

			if (state.getValue(BlockChangeDetectorBlock.POWERED)) {
				level.setBlockAndUpdate(pos, state.setValue(BlockChangeDetectorBlock.POWERED, false));
				BlockUtils.updateIndirectNeighbors(level, pos, SCContent.BLOCK_CHANGE_DETECTOR.get(), AbstractPanelBlock.getConnectedDirection(state).getOpposite());
			}
		}
	}
}
