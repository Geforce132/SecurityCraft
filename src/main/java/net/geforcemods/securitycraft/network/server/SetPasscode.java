package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetPasscode {
	private String passcode;
	private int x, y, z;

	public SetPasscode() {}

	public SetPasscode(int x, int y, int z, String code) {
		this.x = x;
		this.y = y;
		this.z = z;
		passcode = PasscodeUtils.hashPasscodeWithoutSalt(code);
	}

	public SetPasscode(PacketBuffer buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		passcode = buf.readUtf(Integer.MAX_VALUE / 4);
	}

	public void encode(PacketBuffer buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeUtf(passcode);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		BlockPos pos = new BlockPos(x, y, z);
		PlayerEntity player = ctx.get().getSender();
		World level = player.level;
		TileEntity te = level.getBlockEntity(pos);

		if (!player.isSpectator() && te instanceof IPasscodeProtected && (!(te instanceof IOwnable) || ((IOwnable) te).isOwnedBy(player))) {
			IPasscodeProtected passcodeProtected = (IPasscodeProtected) te;

			passcodeProtected.hashAndSetPasscode(passcode, b -> passcodeProtected.openPasscodeGUI(level, pos, player));
			passcodeProtected.setPasscodeInAdjacentBlock(passcode);
		}
	}
}
