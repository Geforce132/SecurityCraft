package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncTENBTTag implements IMessage {
	private BlockPos pos;
	private NBTTagCompound tag;

	public SyncTENBTTag() {}

	public SyncTENBTTag(BlockPos pos, NBTTagCompound tag) {
		this.pos = pos;
		this.tag = tag;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		tag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler implements IMessageHandler<SyncTENBTTag, IMessage> {
		@Override
		public IMessage onMessage(SyncTENBTTag message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayer player = ctx.getServerHandler().player;
				TileEntity te = player.world.getTileEntity(message.pos);

				if (!player.isSpectator() && te instanceof IOwnable && ((IOwnable) te).isOwnedBy(player))
					te.readFromNBT(message.tag);
			});

			return null;
		}
	}
}
