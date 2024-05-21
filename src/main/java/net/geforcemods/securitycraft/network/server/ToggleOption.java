package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ToggleOption implements CustomPacketPayload {
	public static final Type<ToggleOption> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "toggle_option"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ToggleOption> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public ToggleOption decode(RegistryFriendlyByteBuf buf) {
			if (buf.readBoolean())
				return new ToggleOption(buf.readBlockPos(), buf.readVarInt());
			else
				return new ToggleOption(buf.readVarInt(), buf.readVarInt());
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, ToggleOption packet) {
			boolean hasPos = packet.pos != null;

			buf.writeBoolean(hasPos);

			if (hasPos)
				buf.writeBlockPos(packet.pos);
			else
				buf.writeVarInt(packet.entityId);

			buf.writeVarInt(packet.optionId);
		}
	};
	private BlockPos pos;
	private int optionId, entityId;

	public ToggleOption(BlockPos pos, int optionId) {
		this.pos = pos;
		this.optionId = optionId;
	}

	public ToggleOption(int entityId, int optionId) {
		this.entityId = entityId;
		this.optionId = optionId;
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
			customizable.customOptions()[optionId].toggle();
			customizable.onOptionChanged(customizable.customOptions()[optionId]);

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
