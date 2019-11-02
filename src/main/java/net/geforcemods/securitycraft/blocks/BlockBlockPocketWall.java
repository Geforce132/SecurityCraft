package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocket;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockBlockPocketWall extends BlockOwnable implements ITileEntityProvider, IOverlayDisplay
{
	public static final BooleanProperty SEE_THROUGH = BooleanProperty.create("see_through");

	public BlockBlockPocketWall()
	{
		super(Block.Properties.create(Material.ROCK).hardnessAndResistance(-1.0F, 6000000.0F));

		setDefaultState(stateContainer.getBaseState().with(SEE_THROUGH, false));
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public VoxelShape getCollisionShape(IBlockState state, IBlockReader world, BlockPos pos)
	{
		return VoxelShapes.empty(); //no entity based collision, 1.13 is limited here
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public float getAmbientOcclusionLightValue(IBlockState state)
	{
		return state.get(SEE_THROUGH) ? 1.0F : super.getAmbientOcclusionLightValue(state);
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext context)
	{
		return super.getStateForPlacement(context).with(SEE_THROUGH, true);
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(SEE_THROUGH);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world)
	{
		return new TileEntityBlockPocket();
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos)
	{
		return new ItemStack(SCContent.blockPocketWall, 1);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos)
	{
		return true;
	}
}
