package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSUpdateSliderValue implements IMessage{

	private int x, y, z, id;
	private double value;

	public PacketSUpdateSliderValue(){ }

	public PacketSUpdateSliderValue(int x, int y, int z, int id, double v){
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
		value = v;
	}

	@Override
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		par1ByteBuf.writeInt(id);
		par1ByteBuf.writeDouble(value);
	}

	@Override
	public void fromBytes(ByteBuf par1ByteBuf) {
		x = par1ByteBuf.readInt();
		y = par1ByteBuf.readInt();
		z = par1ByteBuf.readInt();
		id = par1ByteBuf.readInt();
		value = par1ByteBuf.readDouble();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSUpdateSliderValue, IMessage> {

		@Override
		public IMessage onMessage(PacketSUpdateSliderValue packet, MessageContext context) {
			int x = packet.x;
			int y = packet.y;
			int z = packet.z;
			int id = packet.id;
			double value = packet.value;
			EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;

			if(getWorld(par1EntityPlayer).getTileEntity(x, y, z) != null && getWorld(par1EntityPlayer).getTileEntity(x, y, z) instanceof CustomizableSCTE) {
				((OptionDouble)((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).customOptions()[id]).setValue(value);
				((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).onOptionChanged(((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).customOptions()[id]);
				((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).sync();
			}

			return null;
		}
	}

}
