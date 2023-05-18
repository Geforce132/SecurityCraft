package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

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

	public CheckPassword(PacketBuffer buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		password = buf.readUtf(Integer.MAX_VALUE / 4);
	}

	public void encode(PacketBuffer buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeUtf(password);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		BlockPos pos = new BlockPos(x, y, z);
		PlayerEntity player = ctx.get().getSender();
		TileEntity te = player.level.getBlockEntity(pos);

		if (te instanceof IPasswordProtected) {
			IPasswordProtected passwordProtected = (IPasswordProtected) te;
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
