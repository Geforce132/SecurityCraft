package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
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

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(id);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		id = buf.readInt();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSToggleOption, IMessage> {

		@Override
		public IMessage onMessage(PacketSToggleOption message, MessageContext context) {
			int x = message.x;
			int y = message.y;
			int z = message.z;
			BlockPos pos = BlockUtils.toPos(x, y, z);
			int id = message.id;
			EntityPlayer player = context.getServerHandler().playerEntity;

			if(getWorld(player).getTileEntity(pos) != null && getWorld(player).getTileEntity(pos) instanceof CustomizableSCTE) {
				((CustomizableSCTE) getWorld(player).getTileEntity(pos)).customOptions()[id].toggle();
				((CustomizableSCTE) getWorld(player).getTileEntity(pos)).onOptionChanged(((CustomizableSCTE) getWorld(player).getTileEntity(pos)).customOptions()[id]);
				((CustomizableSCTE) getWorld(player).getTileEntity(pos)).sync();
			}

			return null;
		}
	}

}
