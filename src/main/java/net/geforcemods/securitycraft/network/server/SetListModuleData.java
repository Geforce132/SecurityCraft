package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.components.ListModuleData;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetListModuleData(ListModuleData listModuleData) implements CustomPacketPayload {
	public static final Type<SetListModuleData> TYPE = new Type<>(SecurityCraft.resLoc("set_list_module_data"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SetListModuleData> STREAM_CODEC = StreamCodec.composite(
			ListModuleData.STREAM_CODEC, SetListModuleData::listModuleData,
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
			//@formatter:off
			stack.set(SCContent.LIST_MODULE_DATA, new ListModuleData(
					listModuleData.players().stream().distinct().toList(),
					listModuleData.teams().stream().filter(player.getScoreboard().getTeamNames()::contains).toList(),
					listModuleData.affectEveryone()));
			//@formatter:on
		}
	}
}
