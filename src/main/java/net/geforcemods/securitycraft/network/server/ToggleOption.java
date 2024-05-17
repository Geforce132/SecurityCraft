package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ToggleOption implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "toggle_option");
	private BlockPos pos;
	private int optionId, entityId;

	public ToggleOption() {}

	public ToggleOption(BlockPos pos, int optionId) {
		this.pos = pos;
		this.optionId = optionId;
	}

	public ToggleOption(int entityId, int optionId) {
		this.entityId = entityId;
		this.optionId = optionId;
	}

	public ToggleOption(FriendlyByteBuf buf) {
		if (buf.readBoolean())
			pos = buf.readBlockPos();
		else
			entityId = buf.readVarInt();

		optionId = buf.readVarInt();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		boolean hasPos = pos != null;

		buf.writeBoolean(hasPos);

		if (hasPos)
			buf.writeBlockPos(pos);
		else
			buf.writeVarInt(entityId);

		buf.writeVarInt(optionId);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		Level level = player.level();
		ICustomizable customizable = null;

		if (pos != null && level.getBlockEntity(pos) instanceof ICustomizable be)
			customizable = be;
		else if (pos == null && level.getEntity(entityId) instanceof ICustomizable entity)
			customizable = entity;

		if (customizable != null && (!(customizable instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
			customizable.customOptions()[optionId].toggle();
			customizable.onOptionChanged(customizable.customOptions()[optionId]);

			if (customizable instanceof BlockEntity be)
				level.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
		}
	}
}
