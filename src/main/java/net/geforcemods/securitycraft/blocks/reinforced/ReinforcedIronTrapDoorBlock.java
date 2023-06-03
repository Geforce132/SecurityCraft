package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReinforcedIronTrapDoorBlock extends BaseIronTrapDoorBlock implements IReinforcedBlock {
	public ReinforcedIronTrapDoorBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos neighbor, boolean flag) {
		boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos);

		if (hasActiveSCBlock != state.getValue(OPEN)) {
			world.setBlock(pos, state.setValue(OPEN, hasActiveSCBlock), 2);
			playSound((PlayerEntity) null, world, pos, hasActiveSCBlock);
		}
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.IRON_TRAPDOOR;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState) {
		return defaultBlockState().setValue(FACING, vanillaState.getValue(FACING)).setValue(OPEN, false).setValue(HALF, vanillaState.getValue(HALF)).setValue(POWERED, false).setValue(WATERLOGGED, vanillaState.getValue(WATERLOGGED));
	}
}