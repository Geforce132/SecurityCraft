package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blocks.AbstractPanelBlock;
import net.geforcemods.securitycraft.blocks.BlockChangeDetectorBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

public class ClearChangeDetectorServer {
	private BlockPos pos;

	public ClearChangeDetectorServer() {}

	public ClearChangeDetectorServer(BlockPos pos) {
		this.pos = pos;
	}

	public ClearChangeDetectorServer(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayer player = ctx.get().getSender();
		Level level = player.level;

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
