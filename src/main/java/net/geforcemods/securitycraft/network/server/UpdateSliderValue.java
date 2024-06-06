package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.EntityDataWrappedOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class UpdateSliderValue implements CustomPacketPayload {
	public static final Type<UpdateSliderValue> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "update_slider_value"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSliderValue> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public UpdateSliderValue decode(RegistryFriendlyByteBuf buf) {
			if (buf.readBoolean())
				return new UpdateSliderValue(buf.readBlockPos(), buf.readUtf(), buf.readDouble());
			else
				return new UpdateSliderValue(buf.readVarInt(), buf.readUtf(), buf.readDouble());
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, UpdateSliderValue packet) {
			boolean hasPos = packet.pos != null;

			buf.writeBoolean(hasPos);

			if (hasPos)
				buf.writeBlockPos(packet.pos);
			else
				buf.writeVarInt(packet.entityId);

			buf.writeUtf(packet.optionName);
			buf.writeDouble(packet.value);
		}
	};
	private BlockPos pos;
	private String optionName;
	private double value;
	private int entityId;

	public UpdateSliderValue(BlockPos pos, Option<?> option, double v) {
		this(pos, option.getName(), v);
	}

	public UpdateSliderValue(BlockPos pos, String option, double v) {
		this.pos = pos;
		optionName = option;
		value = v;
	}

	public UpdateSliderValue(int entityId, Option<?> option, double v) {
		this(entityId, option.getName(), v);
	}

	public UpdateSliderValue(int entityId, String option, double v) {
		this.entityId = entityId;
		optionName = option;
		value = v;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();
		ICustomizable customizable = getCustomizable(level);

		if (customizable != null && (!(customizable instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
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
