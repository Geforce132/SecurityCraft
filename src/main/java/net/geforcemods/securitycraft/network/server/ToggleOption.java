package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ToggleOption implements IMessage{

	private int x, y, z, id;

	public ToggleOption(){ }

	public ToggleOption(int x, int y, int z, int id){
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

	public static class Handler implements IMessageHandler<ToggleOption, IMessage> {

		@Override
		public IMessage onMessage(ToggleOption packet, MessageContext message) {
			WorldUtils.addScheduledTask(message.getServerHandler().player.world, () -> {
				int x = packet.x;
				int y = packet.y;
				int z = packet.z;
				BlockPos pos = BlockUtils.toPos(x, y, z);
				int id = packet.id;
				EntityPlayer player = message.getServerHandler().player;
				TileEntity te = player.world.getTileEntity(pos);

				if(te instanceof ICustomizable && !(te instanceof IOwnable) || ((IOwnable)te).getOwner().isOwner(player)) {
					((ICustomizable)te).customOptions()[id].toggle();
					((ICustomizable)te).onOptionChanged(((ICustomizable)te).customOptions()[id]);

					if(te instanceof CustomizableSCTE)
						((CustomizableSCTE)te).sync();
				}
			});

			return null;
		}
	}

}
