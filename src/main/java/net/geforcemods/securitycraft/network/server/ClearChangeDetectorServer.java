package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blocks.AbstractPanelBlock;
import net.geforcemods.securitycraft.blocks.BlockChangeDetectorBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClearChangeDetectorServer {
	private BlockPos pos;

	public ClearChangeDetectorServer() {}

	public ClearChangeDetectorServer(BlockPos pos) {
		this.pos = pos;
	}

	public ClearChangeDetectorServer(PacketBuffer buf) {
		pos = buf.readBlockPos();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayerEntity player = ctx.get().getSender();
		World level = player.level;
		TileEntity te = level.getBlockEntity(pos);

		if (!player.isSpectator() && te instanceof BlockChangeDetectorBlockEntity) {
			BlockChangeDetectorBlockEntity be = (BlockChangeDetectorBlockEntity) te;

			if (be.isOwnedBy(player)) {
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
}
