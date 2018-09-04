package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortableRadar extends BlockContainer {

	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockPortableRadar(Material material) {
		super(material);
		setBlockBounds(0.3F, 0.0F, 0.3F, 0.7F, 0.45F, 0.7F);
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube(){
		return false;
	}

	@Override
	public int getRenderType(){
		return 3;
	}

	public static void togglePowerOutput(World world, BlockPos pos, boolean toggleOn) {
		if(toggleOn && !world.getBlockState(pos).getValue(POWERED).booleanValue()){
			BlockUtils.setBlockProperty(world, pos, POWERED, true, true);
			BlockUtils.updateAndNotify(world, pos, BlockUtils.getBlock(world, pos), 1, false);
		}else if(!toggleOn && world.getBlockState(pos).getValue(POWERED).booleanValue()){
			BlockUtils.setBlockProperty(world, pos, POWERED, false, true);
			BlockUtils.updateAndNotify(world, pos, BlockUtils.getBlock(world, pos), 1, false);
		}
	}

	@Override
	public boolean canProvidePower()
	{
		return true;
	}

	@Override
	public int getWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side){
		if(((CustomizableSCTE) world.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE) && state.getValue(POWERED).booleanValue())
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
		return state.getValue(POWERED).booleanValue() ? 1 : 0;
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {POWERED});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityPortableRadar().attacks(EntityPlayer.class, SecurityCraft.config.portableRadarSearchRadius, SecurityCraft.config.portableRadarDelay).nameable();
	}

}
