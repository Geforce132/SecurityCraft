package net.breakinbad.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.breakinbad.securitycraft.api.CustomizableSCTE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSAddModules implements IMessage{
	
	private int x, y, z;
	private ItemStack[] modules;
	private int arrayLength;
	
	public PacketSAddModules(){
		
	}
	
	public PacketSAddModules(int x, int y, int z, ItemStack... modules){
		this.x = x;
		this.y = y;
		this.z = z;
		this.modules = modules;
		this.arrayLength = modules.length;
	}

	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		par1ByteBuf.writeInt(arrayLength);
		for(ItemStack stack : modules){
			ByteBufUtils.writeItemStack(par1ByteBuf, stack);
		}
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
		this.arrayLength = par1ByteBuf.readInt();
		for(int i = 0; i < arrayLength; i++){
			if(this.modules == null){
				this.modules = new ItemStack[arrayLength];
			}
			
			this.modules[i] = ByteBufUtils.readItemStack(par1ByteBuf);
		}
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSAddModules, IMessage> {

	public IMessage onMessage(PacketSAddModules packet, MessageContext context) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		ItemStack[] modules = packet.modules;
		EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;

		if(getWorld(par1EntityPlayer).getTileEntity(x, y, z) != null && getWorld(par1EntityPlayer).getTileEntity(x, y, z) instanceof CustomizableSCTE){
			for(ItemStack module : modules){
				if(!((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).insertModule(module);
				}
			}
		}
		
		return null;
	}
}

	
}
