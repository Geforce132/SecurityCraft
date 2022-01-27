package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReinforcedDoorBlockEntity extends OwnableBlockEntity {
	@Override
	public void onOwnerChanged(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		TileEntity te;

		pos = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();
		te = world.getBlockEntity(pos);

		if (te instanceof ReinforcedDoorBlockEntity) {
			((ReinforcedDoorBlockEntity) te).setOwner(getOwner().getUUID(), getOwner().getName());

			if (!world.isClientSide)
				world.getServer().getPlayerList().broadcastAll(te.getUpdatePacket());
		}
	}
}
