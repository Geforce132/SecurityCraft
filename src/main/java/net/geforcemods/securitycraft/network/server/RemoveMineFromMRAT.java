package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class RemoveMineFromMRAT {
	private int mineIndex;

	public RemoveMineFromMRAT() {}

	public RemoveMineFromMRAT(int mineIndex) {
		this.mineIndex = mineIndex;
	}

	public static void encode(RemoveMineFromMRAT message, PacketBuffer buf) {
		buf.writeVarInt(message.mineIndex);
	}

	public static RemoveMineFromMRAT decode(PacketBuffer buf) {
		RemoveMineFromMRAT message = new RemoveMineFromMRAT();

		message.mineIndex = buf.readVarInt();
		return message;
	}

	public static void onMessage(RemoveMineFromMRAT message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();
			ItemStack stack = PlayerUtils.getSelectedItemStack(player, SCContent.REMOTE_ACCESS_MINE.get());

			if (!stack.isEmpty()) {
				CompoundNBT tag = stack.getOrCreateTag();
				String key = "mine" + message.mineIndex;

				if (tag.contains(key)) {
					stack.getTag().putIntArray(key, new int[] {
							0, 0, 0
					});
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
