package net.geforcemods.securitycraft.network.server;

import java.util.Arrays;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

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

	public CheckPasscode(PacketBuffer buf) {
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
		ServerPlayerEntity player = ctx.get().getSender();
		TileEntity te = player.level.getBlockEntity(pos);

		if (PasscodeUtils.isOnCooldown(player))
			PlayerUtils.sendMessageToPlayer(player, new StringTextComponent("SecurityCraft"), Utils.localize("messages.securitycraft:passcodeProtected.onCooldown"), TextFormatting.RED);
		else if (te instanceof IPasscodeProtected) {
			IPasscodeProtected be = (IPasscodeProtected) te;

			if (be.isOnCooldown())
				return;

			PasscodeUtils.setOnCooldown(player);
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
