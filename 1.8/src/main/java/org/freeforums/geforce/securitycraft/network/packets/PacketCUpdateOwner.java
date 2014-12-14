package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

public class PacketCUpdateOwner implements IMessage {
	
	private int x, y, z;
	private String owner;
	private boolean checkForNull;
	
	public PacketCUpdateOwner(){
		
	}
	
	public PacketCUpdateOwner(int x, int y, int z, String owner, boolean checkForNull){
		this.x = x;
		this.y = y;
		this.z = z;
		this.owner = owner;
		this.checkForNull = checkForNull;
	}

	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.owner = ByteBufUtils.readUTF8String(buf);
		this.checkForNull = buf.readBoolean();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, owner);
		buf.writeBoolean(checkForNull);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketCUpdateOwner, IMessage>{

	@SideOnly(Side.CLIENT)
	public IMessage onMessage(PacketCUpdateOwner message, MessageContext ctx) {
		if(message.checkForNull && Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(message.x, message.y, message.z)) != null && Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(message.x, message.y, message.z)) instanceof TileEntityOwnable && ((TileEntityOwnable)Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(message.x, message.y, message.z))).getOwner() == null){
			((TileEntityOwnable)Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(message.x, message.y, message.z))).setOwner(message.owner);
			mod_SecurityCraft.log("Sending owner to: " + message.owner);
		}else if(!message.checkForNull){
			((TileEntityOwnable)Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(message.x, message.y, message.z))).setOwner(message.owner);
			mod_SecurityCraft.log("Sending owner to: " + message.owner);
		}
		return null;
	}
	
}

}
