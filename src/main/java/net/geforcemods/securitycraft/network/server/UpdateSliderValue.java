package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.EntityDataWrappedOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class UpdateSliderValue {
	private BlockPos pos;
	private String optionName;
	private double value;
	private int entityId;

	public UpdateSliderValue() {}

	public UpdateSliderValue(BlockPos pos, Option<?> option, double v) {
		this.pos = pos;
		optionName = option.getName();
		value = v;
	}

	public UpdateSliderValue(int entityId, Option<?> option, double v) {
		this.entityId = entityId;
		optionName = option.getName();
		value = v;
	}

	public UpdateSliderValue(FriendlyByteBuf buf) {
		if (buf.readBoolean())
			pos = buf.readBlockPos();
		else
			entityId = buf.readVarInt();

		optionName = buf.readUtf();
		value = buf.readDouble();
	}

	public void encode(FriendlyByteBuf buf) {
		boolean hasPos = pos != null;

		buf.writeBoolean(hasPos);

		if (hasPos)
			buf.writeBlockPos(pos);
		else
			buf.writeVarInt(entityId);

		buf.writeUtf(optionName);
		buf.writeDouble(value);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		Level level = player.level;
		ICustomizable customizable = getCustomizable(level);

		if (!player.isSpectator() && customizable != null && (!(customizable instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
			Option<?> option = null;

			for (Option<?> o : customizable.customOptions()) {
				if (o.getName().equals(optionName)) {
					option = o;
					break;
				}
			}

			if (option == null)
				return;

			if (option instanceof EntityDataWrappedOption o) {
				Option<?> wrapped = o.getWrapped();

				if (wrapped instanceof DoubleOption)
					o.setValue(value);
				else if (wrapped instanceof IntOption)
					o.setValue((int) value);
			}
			else if (option instanceof DoubleOption o)
				o.setValue(value);
			else if (option instanceof IntOption o)
				o.setValue((int) value);

			customizable.onOptionChanged(option);

			if (customizable instanceof BlockEntity be)
				level.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
		}
	}

	private ICustomizable getCustomizable(Level level) {
		if (pos != null) {
			if (level.getBlockEntity(pos) instanceof ICustomizable be)
				return be;
		}
		else if (level.getEntity(entityId) instanceof ICustomizable entity)
			return entity;

		return null;
	}
}
