package net.geforcemods.securitycraft.network.server;

import java.util.Arrays;

import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;

public class CheckPasscode {
	private BlockPos pos;
	private String passcode;

	public CheckPasscode() {}

	public CheckPasscode(BlockPos pos, String passcode) {
		this.pos = pos;
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	public CheckPasscode(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		passcode = buf.readUtf(Integer.MAX_VALUE / 4);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeUtf(passcode);
	}

	public void handle(NetworkEvent.Context ctx) {
		ServerPlayer player = ctx.getSender();

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
