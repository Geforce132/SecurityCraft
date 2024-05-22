package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class ToggleOption {
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

	public void encode(FriendlyByteBuf buf) {
		boolean hasPos = pos != null;

		buf.writeBoolean(hasPos);

		if (hasPos)
			buf.writeBlockPos(pos);
		else
			buf.writeVarInt(entityId);

		buf.writeVarInt(optionId);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
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
