package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blocks.BlockChangeDetectorBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClearChangeDetectorServer implements IMessage {
	private BlockPos pos;

	public ClearChangeDetectorServer() {}

	public ClearChangeDetectorServer(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
	}

	public static class Handler implements IMessageHandler<ClearChangeDetectorServer, IMessage> {
		@Override
		public IMessage onMessage(ClearChangeDetectorServer message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayer player = ctx.getServerHandler().player;
				World level = player.world;
				BlockPos pos = message.pos;
				TileEntity tile = level.getTileEntity(pos);

				if (!player.isSpectator() && tile instanceof BlockChangeDetectorBlockEntity) {
					BlockChangeDetectorBlockEntity te = (BlockChangeDetectorBlockEntity) tile;

					if (te.isOwnedBy(player)) {
						IBlockState state = level.getBlockState(pos);

						te.getEntries().clear();
						te.markDirty();
						te.sync();

						if (state.getValue(BlockChangeDetectorBlock.POWERED)) {
							BlockChangeDetectorBlock block = (BlockChangeDetectorBlock) state.getBlock();

							level.setBlockState(pos, state.withProperty(BlockChangeDetectorBlock.POWERED, false));
							BlockUtils.updateIndirectNeighbors(level, pos, block, block.getConnectedDirection(state).getOpposite());
						}
					}
				}
			});

			return null;
		}
	}
}
