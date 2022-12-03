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
	private String option;
	private double value;

	public UpdateSliderValue() {}

	public UpdateSliderValue(BlockPos pos, Option<?> option, double v) {
		this.pos = pos;
		this.option = option.getName();
		value = v;
	}

	public static void encode(UpdateSliderValue message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
		buf.writeUtf(message.option);
		buf.writeDouble(message.value);
	}

	public static UpdateSliderValue decode(FriendlyByteBuf buf) {
		UpdateSliderValue message = new UpdateSliderValue();

		message.pos = buf.readBlockPos();
		message.option = buf.readUtf();
		message.value = buf.readDouble();
		return message;
	}

	public static void onMessage(UpdateSliderValue message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			String optionName = message.option;
			double value = message.value;
			Player player = ctx.get().getSender();
			BlockEntity be = player.level.getBlockEntity(pos);

			if (be instanceof ICustomizable customizable && (!(be instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
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
		});

		ctx.get().setPacketHandled(true);
	}
}
