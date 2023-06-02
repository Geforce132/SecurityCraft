package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedIronTrapDoorBlock extends BaseIronTrapDoorBlock implements IReinforcedBlock {
	public ReinforcedIronTrapDoorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean flag) {
		boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(level, pos);

		if (hasActiveSCBlock != state.getValue(OPEN)) {
			level.setBlock(pos, state.setValue(OPEN, hasActiveSCBlock), 2);
			playSound((Player) null, level, pos, hasActiveSCBlock);
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
