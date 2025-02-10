package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.blockentities.FloorTrapBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class FloorTrapBlock extends SometimesVisibleBlock {
	public FloorTrapBlock(Material material) {
		super(material);
		setDefaultState(blockState.getBaseState().withProperty(INVISIBLE, false));
		setSoundType(SoundType.METAL);
		setHardness(5.0F);
		setHarvestLevel("pickaxe", 1);
	}

	@Override
	public void neighborChanged(IBlockState state, World level, BlockPos pos, Block block, BlockPos neighborPos) {
		if (pos.getY() == neighborPos.getY()) {
			TileEntity tile1 = level.getTileEntity(pos);

			if (tile1 instanceof FloorTrapBlockEntity && ((FloorTrapBlockEntity) tile1).isModuleEnabled(ModuleType.SMART)) {
				FloorTrapBlockEntity trap1 = (FloorTrapBlockEntity) tile1;
				TileEntity trap2 = level.getTileEntity(neighborPos);

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
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		IBlockState adjacentState = world.getBlockState(pos.offset(side));

		return (adjacentState.getBlock() == this && !adjacentState.getValue(INVISIBLE)) || super.shouldSideBeRendered(state, world, pos, side);
	}

	@Override
	public float getAmbientOcclusionLightValue(IBlockState state) {
		return 1.0F;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return state.getValue(INVISIBLE) ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.MODEL;
	}

	@Override
	public AxisAlignedBB getCollisionShapeWhenInvisible() {
		return EMPTY_AABB;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new FloorTrapBlockEntity();
	}
}
