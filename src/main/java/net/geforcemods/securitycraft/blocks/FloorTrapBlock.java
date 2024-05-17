package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.blockentities.FloorTrapBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class FloorTrapBlock extends SometimesVisibleBlock {
	public FloorTrapBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(INVISIBLE, false).setValue(WATERLOGGED, false));
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		if (pos.getY() == neighborPos.getY()) {
			TileEntity tile1 = level.getBlockEntity(pos);

			if (tile1 instanceof FloorTrapBlockEntity && ((FloorTrapBlockEntity) tile1).isModuleEnabled(ModuleType.SMART)) {
				FloorTrapBlockEntity trap1 = (FloorTrapBlockEntity) tile1;
				TileEntity trap2 = level.getBlockEntity(neighborPos);

				if (trap2 instanceof FloorTrapBlockEntity && trap1.getOwner().owns(((FloorTrapBlockEntity) trap2)) && level.getBlockState(neighborPos).getValue(INVISIBLE)) {
					if (trap1.shouldDisappearInstantlyInChains())
						trap1.scheduleDisappear(0, true);
					else
						trap1.scheduleDisappear(true);
				}
			}
		}
	}

	@Override
	public boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
		return (adjacentState.is(this) && !adjacentState.getValue(INVISIBLE)) || super.skipRendering(state, adjacentState, side);
	}

	@Override
	public float getShadeBrightness(BlockState state, IBlockReader level, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}

	@Override
	public VoxelShape getCollisionShapeWhenInvisible() {
		return VoxelShapes.empty();
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new FloorTrapBlockEntity();
	}
}
