package net.geforcemods.securitycraft.network.server;

import java.util.Arrays;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CheckPasscode(BlockPos pos, String passcode) implements CustomPacketPayload {
	public static final Type<CheckPasscode> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "check_passcode"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, CheckPasscode> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, CheckPasscode::pos,
			ByteBufCodecs.STRING_UTF8, packet -> PasscodeUtils.hashPasscodeWithoutSalt(packet.passcode),
			CheckPasscode::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();

		if (player.level().getBlockEntity(pos) instanceof IPasscodeProtected be) {
			if (be.isOnCooldown())
				return;

			PasscodeUtils.hashPasscode(passcode, be.getSalt(), p -> {
				if (Arrays.equals(be.getPasscode(), p)) {
					player.closeContainer();
					be.activate(player);
				}
				else
					be.onIncorrectPasscodeEntered(player, passcode);
			});
		}
	}
}
