package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateSliderValue(BlockPos pos, String optionName, double value) implements CustomPacketPayload {

	public static final Type<UpdateSliderValue> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "update_slider_value"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSliderValue> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, UpdateSliderValue::pos,
			ByteBufCodecs.STRING_UTF8, UpdateSliderValue::optionName,
			ByteBufCodecs.DOUBLE, UpdateSliderValue::value,
			UpdateSliderValue::new);
	//@formatter:on
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
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
