package net.geforcemods.securitycraft.blocks;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.BlockPocketBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockPocketBlock extends BaseReinforcedBlock implements IBlockPocket {
	public BlockPocketBlock(AbstractBlock.Properties properties, Supplier<Block> vB) {
		super(properties, vB);
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity be = level.getBlockEntity(pos);

			if (be instanceof BlockPocketBlockEntity) {
				BlockPocketManagerBlockEntity manager = ((BlockPocketBlockEntity) be).getManager();

				if (manager != null)
					manager.disableMultiblock();
			}
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new BlockPocketBlockEntity();
	}
}
