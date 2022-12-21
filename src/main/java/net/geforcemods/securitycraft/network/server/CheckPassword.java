package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CheckPassword {
	private String password;
	private int x, y, z;

	public CheckPassword() {}

	public CheckPassword(int x, int y, int z, String code) {
		this.x = x;
		this.y = y;
		this.z = z;
		password = code;
	}

	public static void encode(CheckPassword message, FriendlyByteBuf buf) {
		buf.writeInt(message.x);
		buf.writeInt(message.y);
		buf.writeInt(message.z);
		buf.writeUtf(message.password);
	}

	public static CheckPassword decode(FriendlyByteBuf buf) {
		CheckPassword message = new CheckPassword();

		message.x = buf.readInt();
		message.y = buf.readInt();
		message.z = buf.readInt();
		message.password = buf.readUtf(Integer.MAX_VALUE / 4);
		return message;
	}

	public static void onMessage(CheckPassword message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			String password = message.password;
			ServerPlayer player = ctx.get().getSender();

			if (player.level.getBlockEntity(pos) instanceof IPasswordProtected passwordProtected) {
				boolean isPasscodeCorrect = passwordProtected.getPassword().equals(password);

				if (passwordProtected.isOnCooldown())
					return;
				else if (isPasscodeCorrect) {
					passwordProtected.activate(player);
					player.closeContainer();
				}
				else
					passwordProtected.onIncorrectPasscodeEntered(player, password);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
