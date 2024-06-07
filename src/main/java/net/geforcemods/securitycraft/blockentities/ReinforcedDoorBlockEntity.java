package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReinforcedDoorBlockEntity extends OwnableBlockEntity {
	@Override
	public void onOwnerChanged(BlockState state, World level, BlockPos pos, PlayerEntity player, Owner oldOwner, Owner newOwner) {
		TileEntity be;

		pos = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();
		be = level.getBlockEntity(pos);

		if (be instanceof ReinforcedDoorBlockEntity) {
			((ReinforcedDoorBlockEntity) be).setOwner(getOwner().getUUID(), getOwner().getName());

			if (!level.isClientSide)
				level.getServer().getPlayerList().broadcastAll(be.getUpdatePacket());
		}

		super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);
	}
}
