package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSToggleOption implements IMessage{
	
	private int x, y, z, id;
	
	public PacketSToggleOption(){ }
	
	public PacketSToggleOption(int x, int y, int z, int id){
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}
	
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		par1ByteBuf.writeInt(id);
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
		this.id = par1ByteBuf.readInt();
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSToggleOption, IMessage> {

	public IMessage onMessage(PacketSToggleOption packet, MessageContext context) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		BlockPos pos = BlockUtils.toPos(x, y, z);
		int id = packet.id;
		EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;
	
		if(getWorld(par1EntityPlayer).getTileEntity(pos) != null && getWorld(par1EntityPlayer).getTileEntity(pos) instanceof CustomizableSCTE) {
			((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(pos)).customOptions()[id].toggle();
			((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(pos)).onOptionChanged(((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(pos)).customOptions()[id]);
			((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(pos)).sync();
		}
		
		return null;
	}
}
	
}
