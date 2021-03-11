package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncTENBTTag implements IMessage{

	private int x, y, z;
	private NBTTagCompound tag;

	public SyncTENBTTag(){

	}

	public SyncTENBTTag(int x, int y, int z, NBTTagCompound tag){
		this.x = x;
		this.y = y;
		this.z = z;
		this.tag = tag;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		tag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler implements IMessageHandler<SyncTENBTTag, IMessage> {

		@Override
		public IMessage onMessage(SyncTENBTTag message, MessageContext ctx) {
			WorldUtils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
				EntityPlayer player = ctx.getServerHandler().player;
				TileEntity te = player.world.getTileEntity(pos);

				if(te instanceof IOwnable && ((IOwnable)te).getOwner().isOwner(player))
					te.readFromNBT(message.tag);
			});

			return null;
		}

	}

}
