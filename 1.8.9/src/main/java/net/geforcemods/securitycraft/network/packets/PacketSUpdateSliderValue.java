package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSUpdateSliderValue implements IMessage{

	private BlockPos pos;
	private int id;
	private double value;

	public PacketSUpdateSliderValue(){ }

	public PacketSUpdateSliderValue(BlockPos pos, int id, double v){
		this.pos = pos;
		this.id = id;
		value = v;
	}

	@Override
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeLong(pos.toLong());
		par1ByteBuf.writeInt(id);
		par1ByteBuf.writeDouble(value);
	}

	@Override
	public void fromBytes(ByteBuf par1ByteBuf) {
		pos = BlockPos.fromLong(par1ByteBuf.readLong());
		id = par1ByteBuf.readInt();
		value = par1ByteBuf.readDouble();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSUpdateSliderValue, IMessage> {

		@Override
		public IMessage onMessage(PacketSUpdateSliderValue packet, MessageContext context) {
			BlockPos pos = packet.pos;
			int id = packet.id;
			double value = packet.value;
			EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;

			if(getWorld(par1EntityPlayer).getTileEntity(pos) != null && getWorld(par1EntityPlayer).getTileEntity(pos) instanceof CustomizableSCTE) {
				((OptionDouble)((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(pos)).customOptions()[id]).setValue(value);
				((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(pos)).onOptionChanged(((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(pos)).customOptions()[id]);
				((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(pos)).sync();
			}

			return null;
		}
	}

}
