package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.network.client.BlockPocketManagerFailedActivation;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.NetworkEvent;

public class ToggleBlockPocketManager {
	private BlockPos pos;
	private int size;
	private boolean enabling;

	public ToggleBlockPocketManager() {}

	public ToggleBlockPocketManager(BlockPocketManagerBlockEntity be, boolean enabling) {
		pos = be.getBlockPos();
		size = be.getSize();
		this.enabling = enabling;
	}

	public ToggleBlockPocketManager(FriendlyByteBuf buf) {
		pos = BlockPos.of(buf.readLong());
		size = buf.readInt();
		enabling = buf.readBoolean();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeLong(pos.asLong());
		buf.writeInt(size);
		buf.writeBoolean(enabling);
	}

	public void handle(NetworkEvent.Context ctx) {
		Player player = ctx.getSender();

		if (player.level().getBlockEntity(pos) instanceof BlockPocketManagerBlockEntity be && be.isOwnedBy(player)) {
			MutableComponent feedback;

			be.setSize(size);

			if (enabling)
				feedback = be.enableMultiblock();
			else
				feedback = be.disableMultiblock();

			if (feedback != null) {
				if (enabling && !be.isEnabled())
					SecurityCraft.CHANNEL.reply(new BlockPocketManagerFailedActivation(pos), ctx);

				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getDescriptionId()), feedback, ChatFormatting.DARK_AQUA, false);
			}

			be.setChanged();
		}
	}
}
