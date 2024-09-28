package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadDoorBlockEntity;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.properties.ChestType;
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

		if (te instanceof IPasscodeProtected && (!(te instanceof IOwnable) || ((IOwnable) te).isOwnedBy(player))) {
			IPasscodeProtected be = (IPasscodeProtected) te;

			be.hashAndSetPasscode(passcode, b -> be.openPasscodeGUI(level, pos, player));

			if (be instanceof KeypadChestBlockEntity)
				checkAndUpdateAdjacentChest(((KeypadChestBlockEntity) be), level, pos, passcode, be.getSalt());
			else if (be instanceof KeypadDoorBlockEntity)
				checkAndUpdateAdjacentDoor(((KeypadDoorBlockEntity) be), level, passcode, be.getSalt());
		}
	}

	private static void checkAndUpdateAdjacentChest(KeypadChestBlockEntity be, World level, BlockPos pos, String codeToSet, byte[] salt) {
		if (be.getBlockState().getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
			BlockPos offsetPos = pos.relative(ChestBlock.getConnectedDirection(be.getBlockState()));
			TileEntity otherBe = level.getBlockEntity(offsetPos);

			if (otherBe instanceof KeypadChestBlockEntity && be.getOwner().owns(((KeypadChestBlockEntity) otherBe))) {
				((KeypadChestBlockEntity) otherBe).hashAndSetPasscode(codeToSet, salt);
				level.sendBlockUpdated(offsetPos, otherBe.getBlockState(), otherBe.getBlockState(), 2);
			}
		}
	}

	private static void checkAndUpdateAdjacentDoor(KeypadDoorBlockEntity be, World level, String codeToSet, byte[] salt) {
		be.runForOtherHalf(otherBe -> {
			if (be.getOwner().owns(otherBe)) {
				otherBe.hashAndSetPasscode(codeToSet, salt);
				level.sendBlockUpdated(otherBe.getBlockPos(), otherBe.getBlockState(), otherBe.getBlockState(), 2);
			}
		});
	}
}
