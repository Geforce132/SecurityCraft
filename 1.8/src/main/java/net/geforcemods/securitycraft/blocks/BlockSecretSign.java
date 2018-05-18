package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSecretSign extends BlockContainer
{
	protected BlockSecretSign()
	{
		super(Material.wood);
		float f = 0.25F;
		float f1 = 1.0F;
		setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
		setStepSound(soundTypeWood);
	}

	@Override
	public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer)
	{
		if(world.getBlockState(pos).getBlock() instanceof BlockSecretSign)
		{
			effectRenderer.addBlockDestroyEffects(pos, Blocks.planks.getDefaultState());
			return true;
		}
		else return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
	{
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos)
	{
		setBlockBoundsBasedOnState(worldIn, pos);
		return super.getSelectedBoundingBox(worldIn, pos);
	}

	@Override
	public boolean isFullCube()
	{
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntitySecretSign();
	}


	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return SCContent.secretSignItem;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (worldIn.isRemote)
		{
			return true;
		}
		else
		{
			TileEntity tileentity = worldIn.getTileEntity(pos);
			return tileentity instanceof TileEntitySecretSign ? ((TileEntitySecretSign)tileentity).executeCommand(playerIn) : false;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Item getItem(World worldIn, BlockPos pos)
	{
		return SCContent.secretSignItem;
	}
}