package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

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
		arrayLength = modules.length;
	}

	@Override
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		par1ByteBuf.writeInt(arrayLength);
		for(ItemStack stack : modules)
			ByteBufUtils.writeItemStack(par1ByteBuf, stack);
	}

	@Override
	public void fromBytes(ByteBuf par1ByteBuf) {
		x = par1ByteBuf.readInt();
		y = par1ByteBuf.readInt();
		z = par1ByteBuf.readInt();
		arrayLength = par1ByteBuf.readInt();
		for(int i = 0; i < arrayLength; i++){
			if(modules == null)
				modules = new ItemStack[arrayLength];

			modules[i] = ByteBufUtils.readItemStack(par1ByteBuf);
		}
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSAddModules, IMessage> {

		@Override
		public IMessage onMessage(PacketSAddModules packet, MessageContext context) {
			int x = packet.x;
			int y = packet.y;
			int z = packet.z;
			ItemStack[] modules = packet.modules;
			EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;

			if(getWorld(par1EntityPlayer).getTileEntity(x, y, z) != null && getWorld(par1EntityPlayer).getTileEntity(x, y, z) instanceof CustomizableSCTE)
				for(ItemStack module : modules)
					if(!((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).hasModule(EnumCustomModules.getModuleFromStack(module)))
						((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).insertModule(module);

			return null;
		}
	}


}
