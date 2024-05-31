package net.geforcemods.securitycraft.network.server;

import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CheckPasscode implements IMessage {
	private String passcode;
	private int x, y, z;

	public CheckPasscode() {}

	public CheckPasscode(int x, int y, int z, String passcode) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, passcode);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		passcode = ByteBufUtils.readUTF8String(buf);
	}

	public static class Handler implements IMessageHandler<CheckPasscode, IMessage> {
		@Override
		public IMessage onMessage(CheckPasscode message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				BlockPos pos = new BlockPos(message.x, message.y, message.z);
				EntityPlayer player = ctx.getServerHandler().player;
				TileEntity te = player.world.getTileEntity(pos);

				if (PasscodeUtils.isOnCooldown(player))
					PlayerUtils.sendMessageToPlayer(player, new TextComponentString("SecurityCraft"), Utils.localize("messages.securitycraft:passcodeProtected.onCooldown"), TextFormatting.RED);
				else if (te instanceof IPasscodeProtected) {
					IPasscodeProtected be = (IPasscodeProtected) te;

					if (be.isOnCooldown())
						return;

					PasscodeUtils.setOnCooldown(player);
					PasscodeUtils.hashPasscode(message.passcode, be.getSalt(), p -> {
						if (Arrays.equals(be.getPasscode(), p)) {
							player.closeScreen();
							be.activate(player);
						}
						else
							be.onIncorrectPasscodeEntered(player, message.passcode);
					});
				}
			});

			return null;
		}
	}
}
