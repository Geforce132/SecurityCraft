package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityReinforcedDoor extends TileEntityOwnable {
	@Override
	public void onOwnerChanged(IBlockState state, World world, BlockPos pos, EntityPlayer player) {
		TileEntity te;

		pos = state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER ? pos.down() : pos.up();
		te = world.getTileEntity(pos);

		if (te instanceof TileEntityReinforcedDoor) {
			((TileEntityReinforcedDoor) te).setOwner(getOwner().getUUID(), getOwner().getName());

			if (!world.isRemote)
				world.getMinecraftServer().getPlayerList().sendPacketToAllPlayers(te.getUpdatePacket());
		}
	}
}