package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetListModuleData(CompoundTag clientTag) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "set_list_module_data");
	public static final Type<SetListModuleData> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "set_list_module_data"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SetListModuleData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.COMPOUND_TAG, SetListModuleData::clientTag,
			SetListModuleData::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.ALLOWLIST_MODULE.get());

		if (stack.isEmpty())
			stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.DENYLIST_MODULE.get());

		if (!stack.isEmpty()) {
			CustomData.update(DataComponents.CUSTOM_DATA, stack, serverTag -> {
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
			});
		}
	}
}
