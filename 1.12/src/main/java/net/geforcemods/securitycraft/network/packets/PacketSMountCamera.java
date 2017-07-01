package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSMountCamera implements IMessage {

	private int x;
	private int y;
	private int z;
	private int id;

	public PacketSMountCamera(){

	}

	public PacketSMountCamera(int x, int y, int z, int id){
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}

	@Override
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(this.x);
		par1ByteBuf.writeInt(this.y);
		par1ByteBuf.writeInt(this.z);
		par1ByteBuf.writeInt(this.id);
	}

	@Override
	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
		this.id = par1ByteBuf.readInt();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSMountCamera, IMessage>{
		
		@Override
		public IMessage onMessage(PacketSMountCamera packet, MessageContext context) {
			int x = packet.x;
			int y = packet.y;
			int z = packet.z;
			int id = packet.id;
			EntityPlayerMP player = context.getServerHandler().player;

			if((BlockUtils.getBlock(getWorld(player), BlockUtils.toPos(x, y, z)) instanceof BlockSecurityCamera)){
				((BlockSecurityCamera) BlockUtils.getBlock(getWorld(player), x, y, z)).mountCamera(getWorld(player), x, y, z, id, player);
			}

			return null;
		}
	}
	
}