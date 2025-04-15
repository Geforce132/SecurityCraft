package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetListModuleData {
	private CompoundNBT tag;

	public SetListModuleData() {}

	public SetListModuleData(CompoundNBT tag) {
		this.tag = tag;
	}

	public SetListModuleData(PacketBuffer buf) {
		tag = buf.readNbt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeNbt(tag);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.ALLOWLIST_MODULE.get());

		if (stack.isEmpty())
			stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.DENYLIST_MODULE.get());

		if (!player.isSpectator() && !stack.isEmpty()) {
			CompoundNBT clientTag = tag;
			CompoundNBT serverTag = stack.getOrCreateTag();

			for (int i = 1; i <= ModuleItem.MAX_PLAYERS; i++) {
				String key = "Player" + i;

				if (clientTag.contains(key))
					serverTag.putString(key, clientTag.getString(key));
				else //prevent two same players being on the list
					serverTag.remove(key);
			}

			if (clientTag.contains("ListedTeams")) {
				ListNBT listedTeams = new ListNBT();

				for (INBT teamTag : clientTag.getList("ListedTeams", Constants.NBT.TAG_STRING)) {
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
