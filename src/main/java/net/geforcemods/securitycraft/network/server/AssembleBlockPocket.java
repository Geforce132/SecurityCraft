package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class AssembleBlockPocket {
	private BlockPos pos;
	private int size;

	public AssembleBlockPocket() {}

	public AssembleBlockPocket(BlockPocketManagerBlockEntity be) {
		pos = be.getBlockPos();
		size = be.getSize();
	}

	public AssembleBlockPocket(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		size = buf.readInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(size);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();

		if (!player.isSpectator() && player.level().getBlockEntity(pos) instanceof BlockPocketManagerBlockEntity be && be.isOwnedBy(player)) {
			MutableComponent feedback;

			be.setSize(size);
			feedback = be.autoAssembleMultiblock();
			be.setChanged();

			if (feedback != null)
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getDescriptionId()), feedback, ChatFormatting.DARK_AQUA);
		}
	}
}
