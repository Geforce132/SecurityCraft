package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateSliderValue implements IMessage {
	private BlockPos pos;
	private int id;
	private double value;

	public UpdateSliderValue() {}

	public UpdateSliderValue(BlockPos pos, int id, double v) {
		this.pos = pos;
		this.id = id;
		value = v;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeInt(id);
		buf.writeDouble(value);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		id = buf.readInt();
		value = buf.readDouble();
	}

	public static class Handler implements IMessageHandler<UpdateSliderValue, IMessage> {
		@Override
		public IMessage onMessage(UpdateSliderValue message, MessageContext context) {
			Utils.addScheduledTask(context.getServerHandler().player.world, () -> {
				EntityPlayer player = context.getServerHandler().player;
				TileEntity te = player.world.getTileEntity(message.pos);

				if (!player.isSpectator() && te instanceof ICustomizable && !(te instanceof IOwnable) || ((IOwnable) te).isOwnedBy(player)) {
					Option<?> o = ((ICustomizable) te).customOptions()[message.id];

					if (o instanceof DoubleOption)
						((DoubleOption) o).setValue(message.value);
					else if (o instanceof IntOption)
						((IntOption) o).setValue((int) message.value);

					((ICustomizable) te).onOptionChanged(((ICustomizable) te).customOptions()[message.id]);

					if (te instanceof CustomizableBlockEntity)
						((CustomizableBlockEntity) te).sync();
				}
			});

			return null;
		}
	}
}
