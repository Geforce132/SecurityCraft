package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReinforcedIronTrapDoorBlock extends BaseIronTrapDoorBlock implements IReinforcedBlock {
	public ReinforcedIronTrapDoorBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos neighbor, boolean flag) {
		boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(level, pos);

		if (hasActiveSCBlock != state.getValue(OPEN)) {
			level.setBlock(pos, state.setValue(OPEN, hasActiveSCBlock), 2);
			playSound((PlayerEntity) null, level, pos, hasActiveSCBlock);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(ctx.getLevel(), ctx.getClickedPos());

		return super.getStateForPlacement(ctx).setValue(OPEN, hasActiveSCBlock).setValue(POWERED, hasActiveSCBlock);
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.IRON_TRAPDOOR;
	}

	@Override
	public BlockState convertToReinforced(World level, BlockPos pos, BlockState vanillaState) {
		return defaultBlockState().setValue(FACING, vanillaState.getValue(FACING)).setValue(OPEN, BlockUtils.hasActiveSCBlockNextTo(level, pos)).setValue(HALF, vanillaState.getValue(HALF)).setValue(POWERED, false).setValue(WATERLOGGED, vanillaState.getValue(WATERLOGGED));
	}

	@Override
	public BlockState convertToVanilla(World level, BlockPos pos, BlockState reinforcedState) {
		boolean isPowered = level.hasNeighborSignal(pos);

		return defaultBlockState().setValue(FACING, reinforcedState.getValue(FACING)).setValue(OPEN, isPowered).setValue(HALF, reinforcedState.getValue(HALF)).setValue(POWERED, isPowered).setValue(WATERLOGGED, reinforcedState.getValue(WATERLOGGED));
	}
}