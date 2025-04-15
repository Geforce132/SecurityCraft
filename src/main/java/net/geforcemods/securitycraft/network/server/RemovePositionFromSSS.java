package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class RemovePositionFromSSS {
	private BlockPos pos;

	public RemovePositionFromSSS() {}

	public RemovePositionFromSSS(BlockPos pos) {
		this.pos = pos;
	}

	public RemovePositionFromSSS(PacketBuffer buf) {
		pos = buf.readBlockPos();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.SONIC_SECURITY_SYSTEM_ITEM.get());

		if (!player.isSpectator() && !stack.isEmpty())
			SonicSecuritySystemItem.removeLinkedBlock(stack.getOrCreateTag(), pos);
	}
}
