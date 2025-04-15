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
import net.minecraftforge.network.NetworkEvent;

public class SetListModuleData {
	private CompoundTag tag;

	public SetListModuleData() {}

	public SetListModuleData(CompoundTag tag) {
		this.tag = tag;
	}

	public SetListModuleData(FriendlyByteBuf buf) {
		tag = buf.readNbt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(tag);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.ALLOWLIST_MODULE.get());

		if (stack.isEmpty())
			stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.DENYLIST_MODULE.get());

		if (!player.isSpectator() && !stack.isEmpty()) {
			CompoundTag clientTag = tag;
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

				for (Tag teamTag : clientTag.getList("ListedTeams", Tag.TAG_STRING)) {
					//make sure the team the client sent is actually a team that exists
					if (player.getScoreboard().getTeamNames().contains(teamTag.getAsString()))
						listedTeams.add(teamTag);
				}

				serverTag.put("ListedTeams", listedTeams);
			}

			serverTag.putBoolean("affectEveryone", clientTag.getBoolean("affectEveryone"));
		}
	}
}
