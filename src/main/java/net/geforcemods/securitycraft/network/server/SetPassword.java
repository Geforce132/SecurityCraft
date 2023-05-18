package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetPassword {
	private String password;
	private int x, y, z;

	public SetPassword() {}

	public SetPassword(int x, int y, int z, String code) {
		this.x = x;
		this.y = y;
		this.z = z;
		password = code;
	}

	public SetPassword(PacketBuffer buf) {
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
		World world = player.level;
		TileEntity te = world.getBlockEntity(pos);

		if (te instanceof IPasswordProtected && (!(te instanceof IOwnable) || ((IOwnable) te).isOwnedBy(player))) {
			((IPasswordProtected) te).setPassword(password);

			if (te instanceof KeypadChestBlockEntity)
				checkAndUpdateAdjacentChest((KeypadChestBlockEntity) te, world, pos, password, player);
		}
	}

	private void checkAndUpdateAdjacentChest(KeypadChestBlockEntity te, World world, BlockPos pos, String codeToSet, PlayerEntity player) {
		if (te.getBlockState().getValue(KeypadChestBlock.TYPE) != ChestType.SINGLE) {
			BlockPos offsetPos = pos.relative(KeypadChestBlock.getConnectedDirection(te.getBlockState()));
			TileEntity otherTe = world.getBlockEntity(offsetPos);

			if (otherTe instanceof KeypadChestBlockEntity && te.getOwner().owns((KeypadChestBlockEntity) otherTe)) {
				((KeypadChestBlockEntity) otherTe).setPassword(codeToSet);
				world.sendBlockUpdated(offsetPos, otherTe.getBlockState(), otherTe.getBlockState(), 2);
			}
		}
	}
}
