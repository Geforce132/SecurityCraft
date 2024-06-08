package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReinforcedDoorBlockEntity extends OwnableBlockEntity {
	@Override
	public void onOwnerChanged(IBlockState state, World world, BlockPos pos, EntityPlayer player, Owner oldOwner, Owner newOwner) {
		TileEntity te;

		pos = state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER ? pos.down() : pos.up();
		te = world.getTileEntity(pos);

		if (te instanceof ReinforcedDoorBlockEntity) {
			((ReinforcedDoorBlockEntity) te).setOwner(getOwner().getUUID(), getOwner().getName());

			if (!world.isRemote)
				world.getMinecraftServer().getPlayerList().sendPacketToAllPlayers(te.getUpdatePacket());
		}
	}
}