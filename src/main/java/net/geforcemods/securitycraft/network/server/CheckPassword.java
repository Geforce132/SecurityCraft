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

	public CheckPassword(FriendlyByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		password = buf.readUtf(Integer.MAX_VALUE / 4);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeUtf(password);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		BlockPos pos = new BlockPos(x, y, z);
		ServerPlayer player = ctx.get().getSender();

		if (player.level.getBlockEntity(pos) instanceof IPasswordProtected passwordProtected) {
			boolean isPasscodeCorrect = passwordProtected.getPassword().equals(password);

			if (passwordProtected.isOnCooldown())
				return;
			else if (isPasscodeCorrect) {
				player.closeContainer();
				passwordProtected.activate(player);
			}
			else
				passwordProtected.onIncorrectPasscodeEntered(player, password);
		}
	}
}
