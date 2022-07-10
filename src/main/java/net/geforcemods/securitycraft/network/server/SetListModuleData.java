package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class SetListModuleData {
	private CompoundTag tag;

	public SetListModuleData() {}

	public SetListModuleData(CompoundTag tag) {
		this.tag = tag;
	}

	public static void encode(SetListModuleData message, FriendlyByteBuf buf) {
		buf.writeNbt(message.tag);
	}

	public static SetListModuleData decode(FriendlyByteBuf buf) {
		SetListModuleData message = new SetListModuleData();

		message.tag = buf.readNbt();
		return message;
	}

	public static void onMessage(SetListModuleData message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();
			ItemStack stack = PlayerUtils.getSelectedItemStack(player, SCContent.ALLOWLIST_MODULE.get());

			if (stack.isEmpty())
				stack = PlayerUtils.getSelectedItemStack(player, SCContent.DENYLIST_MODULE.get());

			if (!stack.isEmpty()) {
				CompoundTag clientTag = message.tag;
				CompoundTag serverTag = stack.getOrCreateTag();

				for (int i = 1; i <= ModuleItem.MAX_PLAYERS; i++) {
					String key = "Player" + i;

					if (clientTag.contains(key))
						serverTag.putString(key, clientTag.getString(key));
					else //prevent two same players being on the list
						serverTag.remove(key);
				}

				if (clientTag.contains("ListedTeams")) {
					ListTag listedTeams = new ListTag();

					for (Tag tag : clientTag.getList("ListedTeams", Tag.TAG_STRING)) {
						//make sure the team the client sent is actually a team that exists
						if (player.getScoreboard().getTeamNames().contains(tag.getAsString()))
							listedTeams.add(tag);
					}

					serverTag.put("ListedTeams", listedTeams);
				}

				serverTag.putBoolean("affectEveryone", clientTag.getBoolean("affectEveryone"));
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
