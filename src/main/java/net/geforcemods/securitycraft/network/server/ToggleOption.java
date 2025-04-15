package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ToggleOption implements IMessage {
	private int x, y, z, id;

	public ToggleOption() {}

	public ToggleOption(int x, int y, int z, int id) {
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
		public IMessage onMessage(ToggleOption message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				BlockPos pos = new BlockPos(message.x, message.y, message.z);
				EntityPlayer player = ctx.getServerHandler().player;
				TileEntity te = player.world.getTileEntity(pos);

				if (!player.isSpectator() && te instanceof ICustomizable && !(te instanceof IOwnable) || ((IOwnable) te).isOwnedBy(player)) {
					((ICustomizable) te).customOptions()[message.id].toggle();
					((ICustomizable) te).onOptionChanged(((ICustomizable) te).customOptions()[message.id]);

					if (te instanceof CustomizableBlockEntity)
						((CustomizableBlockEntity) te).sync();
				}
			});

			return null;
		}
	}
}
