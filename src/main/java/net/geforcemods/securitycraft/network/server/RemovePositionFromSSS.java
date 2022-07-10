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

	public static void encode(RemovePositionFromSSS message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
	}

	public static RemovePositionFromSSS decode(FriendlyByteBuf buf) {
		RemovePositionFromSSS message = new RemovePositionFromSSS();

		message.pos = buf.readBlockPos();
		return message;
	}

	public static void onMessage(RemovePositionFromSSS message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();
			ItemStack stack = PlayerUtils.getSelectedItemStack(player, SCContent.SONIC_SECURITY_SYSTEM_ITEM.get());

			if (!stack.isEmpty())
				SonicSecuritySystemItem.removeLinkedBlock(stack.getOrCreateTag(), message.pos);
		});

		ctx.get().setPacketHandled(true);
	}
}
