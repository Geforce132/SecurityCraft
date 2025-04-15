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
import net.minecraftforge.network.NetworkEvent;

public class UpdateSliderValue {
	private BlockPos pos;
	private String optionName;
	private double value;

	public UpdateSliderValue() {}

	public UpdateSliderValue(BlockPos pos, Option<?> option, double v) {
		this.pos = pos;
		optionName = option.getName();
		value = v;
	}

	public UpdateSliderValue(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		optionName = buf.readUtf();
		value = buf.readDouble();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeUtf(optionName);
		buf.writeDouble(value);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		BlockEntity be = player.level.getBlockEntity(pos);

		if (!player.isSpectator() && be instanceof ICustomizable customizable && (!(be instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
			Option<?> option = null;

			for (Option<?> o : customizable.customOptions()) {
				if (o.getName().equals(optionName)) {
					option = o;
					break;
				}
			}

			if (option == null)
				return;

			if (option instanceof DoubleOption o)
				o.setValue(value);
			else if (option instanceof IntOption o)
				o.setValue((int) value);

			customizable.onOptionChanged(option);

			if (be instanceof CustomizableBlockEntity)
				player.level.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
		}
	}
}
