package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOwnable extends BlockContainer {

	//if the color should be darkened, only used for reinforced blocks
	private boolean darkenBlock;

	public BlockOwnable(Material material) {
		this(material, false);
	}

	public BlockOwnable(Material mat, boolean darkenBlock)
	{
		super(mat);
		this.darkenBlock = darkenBlock;
	}

	@Override
	public int getRenderType()
	{
		return 3;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass)
	{
		return !darkenBlock ? super.colorMultiplier(world, pos, renderPass) : 0x999999;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(IBlockState state)
	{
		return !darkenBlock ? super.getRenderColor(state) : 0x999999;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return !darkenBlock ? super.getBlockColor() : 0x999999;
	}
}
