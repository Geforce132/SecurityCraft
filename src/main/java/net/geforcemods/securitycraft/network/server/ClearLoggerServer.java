package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClearLoggerServer implements IMessage {
	private BlockPos pos;

	public ClearLoggerServer() {}

	public ClearLoggerServer(BlockPos pos) {
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

	public static class Handler implements IMessageHandler<ClearLoggerServer, IMessage> {
		@Override
		public IMessage onMessage(ClearLoggerServer message, MessageContext context) {
			Utils.addScheduledTask(context.getServerHandler().player.world, () -> {
				EntityPlayer player = context.getServerHandler().player;
				TileEntity te = player.world.getTileEntity(message.pos);

				if (!player.isSpectator() && te instanceof UsernameLoggerBlockEntity && ((UsernameLoggerBlockEntity) te).isOwnedBy(player)) {
					((UsernameLoggerBlockEntity) te).setPlayers(new String[100]);
					((UsernameLoggerBlockEntity) te).sync();
				}
			});

			return null;
		}
	}
}
