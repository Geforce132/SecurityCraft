package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReinforcedDoorTileEntity extends OwnableTileEntity {
	@Override
	public void onOwnerChanged(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		TileEntity te;

		pos = state.get(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos.up();
		te = world.getTileEntity(pos);

		if (te instanceof ReinforcedDoorTileEntity) {
			((ReinforcedDoorTileEntity) te).setOwner(getOwner().getUUID(), getOwner().getName());

			if (!world.isRemote)
				world.getServer().getPlayerList().sendPacketToAllPlayers(te.getUpdatePacket());
		}
	}
}
