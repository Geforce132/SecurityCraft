package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

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
		public IMessage onMessage(PacketSSyncTENBTTag packet, MessageContext ctx) {
			int x = packet.x;
			int y = packet.y;
			int z = packet.z;
			NBTTagCompound tag = packet.tag;
			EntityPlayer player = ctx.getServerHandler().playerEntity;

			if(getWorld(player).getTileEntity(x, y, z) != null)
				getWorld(player).getTileEntity(x, y, z).readFromNBT(tag);

			return null;
		}

	}

}
