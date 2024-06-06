package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class ReinforcedIronTrapDoorBlock extends BaseIronTrapDoorBlock implements IReinforcedBlock {
	public ReinforcedIronTrapDoorBlock(BlockBehaviour.Properties properties, BlockSetType blockSetType) {
		super(properties, blockSetType);
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
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(ctx.getLevel(), ctx.getClickedPos());

		return super.getStateForPlacement(ctx).setValue(OPEN, hasActiveSCBlock).setValue(POWERED, hasActiveSCBlock);
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.IRON_TRAPDOOR;
	}

	@Override
	public BlockState convertToReinforced(Level level, BlockPos pos, BlockState vanillaState) {
		return defaultBlockState().setValue(FACING, vanillaState.getValue(FACING)).setValue(OPEN, BlockUtils.hasActiveSCBlockNextTo(level, pos)).setValue(HALF, vanillaState.getValue(HALF)).setValue(POWERED, false).setValue(WATERLOGGED, vanillaState.getValue(WATERLOGGED));
	}

	@Override
	public BlockState convertToVanilla(Level level, BlockPos pos, BlockState reinforcedState) {
		boolean isPowered = level.hasNeighborSignal(pos);

		return defaultBlockState().setValue(FACING, reinforcedState.getValue(FACING)).setValue(OPEN, isPowered).setValue(HALF, reinforcedState.getValue(HALF)).setValue(POWERED, isPowered).setValue(WATERLOGGED, reinforcedState.getValue(WATERLOGGED));
	}
}
