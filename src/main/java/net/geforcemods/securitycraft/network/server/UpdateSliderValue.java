package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class UpdateSliderValue implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "update_slider_value");
	private BlockPos pos;
	private String optionName;
	private double value;

	public UpdateSliderValue() {}

	public UpdateSliderValue(BlockPos pos, Option<?> option, double v) {
		this.pos = pos;
		this.optionName = option.getName();
		value = v;
	}

	public UpdateSliderValue(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		optionName = buf.readUtf();
		value = buf.readDouble();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeUtf(optionName);
		buf.writeDouble(value);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		BlockEntity be = player.level().getBlockEntity(pos);

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
				player.level().sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
		}
	}
}
