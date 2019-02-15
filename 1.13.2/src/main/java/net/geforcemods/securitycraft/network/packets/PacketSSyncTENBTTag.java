package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public class PacketSSyncTENBTTag implements IMessage{

	private int x, y, z;
	private NBTTagCompound tag;

	public PacketSSyncTENBTTag(){

	}

	public PacketSSyncTENBTTag(int x, int y, int z, NBTTagCompound tag){
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

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSSyncTENBTTag, IMessage> {

		@Override
		public IMessage onMessage(PacketSSyncTENBTTag message, MessageContext ctx) {
			WorldUtils.addScheduledTask(getWorld(ctx.getServerHandler().player), () -> {
				BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
				NBTTagCompound tag = message.tag;
				EntityPlayer player = ctx.getServerHandler().player;

				if(getWorld(player).getTileEntity(pos) != null)
					getWorld(player).getTileEntity(pos).readFromNBT(tag);
			});

			return null;
		}

	}

}
