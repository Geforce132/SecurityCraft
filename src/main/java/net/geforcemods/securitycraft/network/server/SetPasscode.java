package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadDoorBlockEntity;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetPasscode implements IMessage {
	private String passcode;
	private int x, y, z;

	public SetPasscode() {}

	public SetPasscode(int x, int y, int z, String code) {
		this.x = x;
		this.y = y;
		this.z = z;
		passcode = PasscodeUtils.hashPasscodeWithoutSalt(code);
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

	public static class Handler implements IMessageHandler<SetPasscode, IMessage> {
		@Override
		public IMessage onMessage(SetPasscode message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				BlockPos pos = new BlockPos(message.x, message.y, message.z);
				EntityPlayer player = ctx.getServerHandler().player;
				World world = player.world;
				TileEntity tile = world.getTileEntity(pos);

				if (tile instanceof IPasscodeProtected && (!(tile instanceof IOwnable) || ((IOwnable) tile).isOwnedBy(player))) {
					IPasscodeProtected be = (IPasscodeProtected) tile;

					be.hashAndSetPasscode(message.passcode, b -> be.openPasscodeGUI(player.world, pos, player));

					if (be instanceof KeypadChestBlockEntity)
						checkAndUpdateAdjacentChest(((KeypadChestBlockEntity) be), world, pos, message.passcode, be.getSalt());
					else if (be instanceof KeypadDoorBlockEntity)
						checkAndUpdateAdjacentDoor(((KeypadDoorBlockEntity) be), world, message.passcode, be.getSalt());
				}
			});

			return null;
		}

		private void checkAndUpdateAdjacentChest(KeypadChestBlockEntity te, World world, BlockPos pos, String codeToSet, byte[] salt) {
			for (EnumFacing facing : EnumFacing.HORIZONTALS) {
				BlockPos offsetPos = pos.offset(facing);
				TileEntity otherTe = world.getTileEntity(offsetPos);

				if (otherTe instanceof KeypadChestBlockEntity && te.getOwner().owns(((KeypadChestBlockEntity) otherTe))) {
					IBlockState state = world.getBlockState(offsetPos);

					te.hashAndSetPasscode(codeToSet, salt);
					world.notifyBlockUpdate(offsetPos, state, state, 2);
					break;
				}
			}
		}

		private void checkAndUpdateAdjacentDoor(KeypadDoorBlockEntity be, World level, String codeToSet, byte[] salt) {
			be.runForOtherHalf(otherBe -> {
				BlockPos otherPos = otherBe.getPos();
				IBlockState state = level.getBlockState(otherPos);

				if (be.getOwner().owns(otherBe)) {
					otherBe.hashAndSetPasscode(codeToSet, salt);
					level.notifyBlockUpdate(otherPos, state, state, 2);
				}
			});
		}
	}
}
