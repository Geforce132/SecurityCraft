package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockChangeDetector;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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
			WorldUtils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayer player = ctx.getServerHandler().player;
				TileEntity tile = player.world.getTileEntity(message.pos);

				if (tile instanceof TileEntityBlockChangeDetector) {
					TileEntityBlockChangeDetector te = (TileEntityBlockChangeDetector) tile;

					if (te.isOwnedBy(player)) {
						te.getEntries().clear();
						te.markDirty();
						te.sync();
					}
				}
			});

			return null;
		}
	}
}
