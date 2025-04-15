package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class RemovePositionFromSSS {
	private BlockPos pos;

	public RemovePositionFromSSS() {}

	public RemovePositionFromSSS(BlockPos pos) {
		this.pos = pos;
	}

	public RemovePositionFromSSS(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.SONIC_SECURITY_SYSTEM_ITEM.get());

		if (!player.isSpectator() && !stack.isEmpty())
			SonicSecuritySystemItem.removeLinkedBlock(stack.getOrCreateTag(), pos);
	}
}
