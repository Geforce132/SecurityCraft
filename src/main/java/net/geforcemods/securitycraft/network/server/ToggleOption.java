package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class ToggleOption {
	private BlockPos pos;
	private int optionId;

	public ToggleOption() {}

	public ToggleOption(BlockPos pos, int opionId) {
		this.pos = pos;
		this.optionId = opionId;
	}

	public ToggleOption(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		optionId = buf.readInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(optionId);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		BlockEntity be = player.level.getBlockEntity(pos);

		if (!player.isSpectator() && be instanceof ICustomizable customizable && (!(be instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
			customizable.customOptions()[optionId].toggle();
			customizable.onOptionChanged(customizable.customOptions()[optionId]);
			player.level.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
		}
	}
}
