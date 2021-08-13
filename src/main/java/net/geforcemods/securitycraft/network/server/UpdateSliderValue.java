package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

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

	public static void encode(UpdateSliderValue message, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(message.pos);
		buf.writeInt(message.id);
		buf.writeDouble(message.value);
	}

	public static UpdateSliderValue decode(FriendlyByteBuf buf)
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
			Player player = ctx.get().getSender();
			BlockEntity te = player.level.getBlockEntity(pos);

			if(te instanceof ICustomizable customizable && (!(te instanceof IOwnable ownable) || ownable.getOwner().isOwner(player))) {
				Option<?> option =customizable.customOptions()[id];

				if(option instanceof DoubleOption o)
					o.setValue(value);
				else if(option instanceof IntOption o)
					o.setValue((int)value);

				customizable.onOptionChanged(customizable.customOptions()[id]);

				if(te instanceof CustomizableBlockEntity customizableTe)
					customizableTe.sync();
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
