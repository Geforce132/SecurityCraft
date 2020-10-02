package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortableRadar extends BlockContainer {

	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockPortableRadar(Material material) {
		super(material);
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube(IBlockState state){
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return new AxisAlignedBB(0.3F, 0.0F, 0.3F, 0.7F, 0.45F, 0.7F);
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos)
	{
		return world.getBlockState(pos.down()).isSideSolid(world, pos, EnumFacing.UP);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		if (world.getBlockState(pos.down()).getMaterial() != Material.AIR)
			return;
		else
			world.destroyBlock(pos, true);
	}

	public static void togglePowerOutput(World world, BlockPos pos, boolean par5) {
		IBlockState state = world.getBlockState(pos);
		boolean powered = state.getValue(POWERED);

		if(par5 && !powered)
			world.setBlockState(pos, state.withProperty(POWERED, true));
		else if(!par5 && powered)
			world.setBlockState(pos, state.withProperty(POWERED, false));
	}

	@Override
	public boolean canProvidePower(IBlockState state)
	{
		return true;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side){
		if(state.getValue(POWERED) && ((IModuleInventory) world.getTileEntity(pos)).hasModule(EnumModuleType.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return meta == 1 ? getDefaultState().withProperty(POWERED, true) : getDefaultState().withProperty(POWERED, false);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(POWERED) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, POWERED);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityPortableRadar().nameable();
	}

}
