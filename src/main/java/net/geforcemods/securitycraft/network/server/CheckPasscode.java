package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CheckPasscode {
	private String passcode;
	private int x, y, z;

	public CheckPasscode() {}

	public CheckPasscode(int x, int y, int z, String code) {
		this.x = x;
		this.y = y;
		this.z = z;
		passcode = Utils.hashPasscodeWithoutSalt(passcode);
	}

	public static void encode(CheckPasscode message, FriendlyByteBuf buf) {
		buf.writeInt(message.x);
		buf.writeInt(message.y);
		buf.writeInt(message.z);
		buf.writeUtf(message.passcode);
	}

	public static CheckPasscode decode(FriendlyByteBuf buf) {
		CheckPasscode message = new CheckPasscode();

		message.x = buf.readInt();
		message.y = buf.readInt();
		message.z = buf.readInt();
		message.passcode = buf.readUtf(Integer.MAX_VALUE / 4);
		return message;
	}

	public static void onMessage(CheckPasscode message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			String passcode = message.passcode;
			ServerPlayer player = ctx.get().getSender();

			if (player.level.getBlockEntity(pos) instanceof IPasscodeProtected passcodeProtected) {
				if (passcodeProtected.isOnCooldown())
					return;

				boolean isPasscodeCorrect = passcodeProtected.checkPasscode(passcode);

				if (isPasscodeCorrect) {
					player.closeContainer();
					passcodeProtected.activate(player);
				}
				else
					passcodeProtected.onIncorrectPasscodeEntered(player, passcode);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
