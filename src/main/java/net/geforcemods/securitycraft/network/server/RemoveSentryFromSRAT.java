package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemoveSentryFromSRAT(int sentryIndex) implements CustomPacketPayload {
	public static final Type<RemoveSentryFromSRAT> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "remove_sentry_from_srat"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, RemoveSentryFromSRAT> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, RemoveSentryFromSRAT::sentryIndex,
			RemoveSentryFromSRAT::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.SENTRY_REMOTE_ACCESS_TOOL.get());

		if (!stack.isEmpty()) {
			CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
				if (tag.contains("sentry" + sentryIndex))
					tag.remove("sentry" + sentryIndex);
			});
		}
	}
}
