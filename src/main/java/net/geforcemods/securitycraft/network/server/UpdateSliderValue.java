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
	private String option;
	private double value;

	public UpdateSliderValue() {}

	public UpdateSliderValue(BlockPos pos, Option<?> option, double v) {
		this.pos = pos;
		this.option = option.getName();
		value = v;
	}

	public static void encode(UpdateSliderValue message, PacketBuffer buf) {
		buf.writeBlockPos(message.pos);
		buf.writeString(message.option);
		buf.writeDouble(message.value);
	}

	public static UpdateSliderValue decode(PacketBuffer buf) {
		UpdateSliderValue message = new UpdateSliderValue();

		message.pos = buf.readBlockPos();
		message.option = buf.readString();
		message.value = buf.readDouble();
		return message;
	}

	public static void onMessage(UpdateSliderValue message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			String optionName = message.option;
			double value = message.value;
			PlayerEntity player = ctx.get().getSender();
			TileEntity te = player.world.getTileEntity(pos);

			if (te instanceof ICustomizable && (!(te instanceof IOwnable) || ((IOwnable) te).getOwner().isOwner(player))) {
				ICustomizable customizable = (ICustomizable) te;
				Option<?> option = null;

				for (Option<?> o : customizable.customOptions()) {
					if (o.getName().equals(optionName)) {
						option = o;
						break;
					}
				}

				if (option == null)
					return;

				if (option instanceof DoubleOption)
					((DoubleOption) option).setValue(value);
				else if (option instanceof IntOption)
					((IntOption) option).setValue((int) value);

				customizable.onOptionChanged(option);

				if (te instanceof CustomizableTileEntity)
					player.world.notifyBlockUpdate(pos, te.getBlockState(), te.getBlockState(), 3);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
