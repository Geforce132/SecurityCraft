package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

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
		public IMessage onMessage(PacketSToggleOption packet, MessageContext message) {
			WorldUtils.addScheduledTask(getWorld(message.getServerHandler().player), () -> {
				int x = packet.x;
				int y = packet.y;
				int z = packet.z;
				BlockPos pos = BlockUtils.toPos(x, y, z);
				int id = packet.id;
				EntityPlayer player = message.getServerHandler().player;

				if(getWorld(player).getTileEntity(pos) != null && getWorld(player).getTileEntity(pos) instanceof CustomizableSCTE) {
					((CustomizableSCTE) getWorld(player).getTileEntity(pos)).customOptions()[id].toggle();
					((CustomizableSCTE) getWorld(player).getTileEntity(pos)).onOptionChanged(((CustomizableSCTE) getWorld(player).getTileEntity(pos)).customOptions()[id]);
					((CustomizableSCTE) getWorld(player).getTileEntity(pos)).sync();
				}
			});

			return null;
		}
	}

}
