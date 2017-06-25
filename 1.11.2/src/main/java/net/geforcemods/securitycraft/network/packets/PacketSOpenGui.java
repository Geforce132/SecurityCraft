package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSOpenGui implements IMessage {

	private int id;
	private int x;
	private int y;
	private int z;

	public PacketSOpenGui(){}

	public PacketSOpenGui(int id, int x, int y, int z){
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(this.id);
		par1ByteBuf.writeInt(this.x);
		par1ByteBuf.writeInt(this.y);
		par1ByteBuf.writeInt(this.z);
	}

	@Override
	public void fromBytes(ByteBuf par1ByteBuf) {
		this.id = par1ByteBuf.readInt();
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
	}

public static class Handler extends PacketHelper implements IMessageHandler<PacketSOpenGui, IMessage> {

	@Override
	public IMessage onMessage(PacketSOpenGui packet, MessageContext context) {
		int id = packet.id;
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		EntityPlayerMP player = context.getServerHandler().playerEntity;

		player.openGui(mod_SecurityCraft.instance, id, getWorld(player), x, y, z);
		return null;
	}
}

}