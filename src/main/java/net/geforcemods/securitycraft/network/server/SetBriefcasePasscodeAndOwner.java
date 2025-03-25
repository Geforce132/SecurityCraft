package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.components.OwnerData;
import net.geforcemods.securitycraft.components.PasscodeData;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetBriefcasePasscodeAndOwner(String passcode) implements CustomPacketPayload {
	public static final Type<SetBriefcasePasscodeAndOwner> TYPE = new Type<>(SecurityCraft.resLoc("set_briefcase_passcode_and_owner"));
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
			if (!stack.has(SCContent.OWNER_DATA))
				stack.set(SCContent.OWNER_DATA, OwnerData.fromPlayer(player));

			if (!passcode.isEmpty() && !stack.has(SCContent.PASSCODE_DATA)) {
				PasscodeData.hashAndSetPasscode(stack, passcode, newPasscodeData -> {
					PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenScreen(OpenScreen.DataType.CHECK_PASSCODE_FOR_BRIEFCASE));
				});
			}
		}
	}
}
