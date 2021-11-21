package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityProtecto;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockProtecto extends BlockDisguisable {

	public static final PropertyBool ACTIVATED = PropertyBool.create("activated");

	public BlockProtecto(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos){
		return world.isSideSolid(pos.down(), EnumFacing.UP);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(ACTIVATED, false);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(ACTIVATED, meta == 1 ? true : false);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(ACTIVATED) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, ACTIVATED);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityProtecto();
	}

}
