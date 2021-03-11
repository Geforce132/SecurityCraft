package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateSliderValue {

	private BlockPos pos;
	private int id;
	private double value;

	public UpdateSliderValue(){ }

	public UpdateSliderValue(BlockPos pos, int id, double v){
		this.pos = pos;
		this.id = id;
		value = v;
	}

	public static void encode(UpdateSliderValue message, PacketBuffer buf)
	{
		buf.writeBlockPos(message.pos);
		buf.writeInt(message.id);
		buf.writeDouble(message.value);
	}

	public static UpdateSliderValue decode(PacketBuffer buf)
	{
		UpdateSliderValue message = new UpdateSliderValue();

		message.pos = buf.readBlockPos();
		message.id = buf.readInt();
		message.value = buf.readDouble();
		return message;
	}

	public static void onMessage(UpdateSliderValue message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			int id = message.id;
			double value = message.value;
			PlayerEntity player = ctx.get().getSender();
			TileEntity te = player.world.getTileEntity(pos);

			if(te instanceof ICustomizable && (!(te instanceof IOwnable) || ((IOwnable)te).getOwner().isOwner(player))) {
				Option<?> o = ((ICustomizable)te).customOptions()[id];

				if(o instanceof DoubleOption)
					((DoubleOption)o).setValue(value);
				else if(o instanceof IntOption)
					((IntOption)o).setValue((int)value);

				((ICustomizable)te).onOptionChanged(((ICustomizable)te).customOptions()[id]);

				if(te instanceof CustomizableTileEntity)
					((CustomizableTileEntity)te).sync();
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
