package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class RemoveMineFromMRAT {
	private int mineIndex;

	public RemoveMineFromMRAT() {}

	public RemoveMineFromMRAT(int mineIndex) {
		this.mineIndex = mineIndex;
	}

	public static void encode(RemoveMineFromMRAT message, FriendlyByteBuf buf) {
		buf.writeVarInt(message.mineIndex);
	}

	public static RemoveMineFromMRAT decode(FriendlyByteBuf buf) {
		RemoveMineFromMRAT message = new RemoveMineFromMRAT();

		message.mineIndex = buf.readVarInt();
		return message;
	}

	public static void onMessage(RemoveMineFromMRAT message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();
			ItemStack stack = PlayerUtils.getSelectedItemStack(player, SCContent.REMOTE_ACCESS_MINE.get());

			if (!stack.isEmpty()) {
				CompoundTag tag = stack.getOrCreateTag();

				if (tag.contains("mine" + message.mineIndex))
					tag.remove("mine" + message.mineIndex);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
