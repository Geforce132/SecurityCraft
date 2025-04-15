package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
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
	private String optionName;
	private double value;

	public UpdateSliderValue() {}

	public UpdateSliderValue(BlockPos pos, Option<?> option, double v) {
		this.pos = pos;
		this.optionName = option.getName();
		value = v;
	}

	public UpdateSliderValue(PacketBuffer buf) {
		pos = buf.readBlockPos();
		optionName = buf.readUtf();
		value = buf.readDouble();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeUtf(optionName);
		buf.writeDouble(value);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		TileEntity be = player.level.getBlockEntity(pos);

		if (!player.isSpectator() && be instanceof ICustomizable && (!(be instanceof IOwnable) || ((IOwnable) be).isOwnedBy(player))) {
			ICustomizable customizable = (ICustomizable) be;
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

			if (be instanceof CustomizableBlockEntity)
				player.level.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
		}
	}
}
