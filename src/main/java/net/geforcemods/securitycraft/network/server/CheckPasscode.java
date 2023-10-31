package net.geforcemods.securitycraft.network.server;

import java.util.Arrays;

import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;

public class CheckPasscode {
	private String passcode;
	private int x, y, z;

	public CheckPasscode() {}

	public CheckPasscode(int x, int y, int z, String passcode) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	public CheckPasscode(FriendlyByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		passcode = buf.readUtf(Integer.MAX_VALUE / 4);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeUtf(passcode);
	}

	public void handle(NetworkEvent.Context ctx) {
		BlockPos pos = new BlockPos(x, y, z);
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
