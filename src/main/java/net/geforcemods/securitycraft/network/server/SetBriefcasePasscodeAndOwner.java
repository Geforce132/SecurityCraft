package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.util.PasscodeUtils;
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

public record SetBriefcasePasscodeAndOwner(String passcode) implements CustomPacketPayload {
	public static final Type<SetBriefcasePasscodeAndOwner> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "set_briefcase_passcode_and_owner"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SetBriefcasePasscodeAndOwner> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, packet -> packet.passcode.isEmpty() ? packet.passcode : PasscodeUtils.hashPasscodeWithoutSalt(packet.passcode),
			SetBriefcasePasscodeAndOwner::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.BRIEFCASE.get());

		if (!stack.isEmpty()) {
			CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
				if (!tag.contains("owner")) {
					tag.putString("owner", player.getName().getString());
					tag.putString("ownerUUID", player.getUUID().toString());
				}

				if (!passcode.isEmpty() && !tag.contains("passcode"))
					BriefcaseItem.hashAndSetPasscode(tag, passcode, p -> {});
			});
		}
	}
}
