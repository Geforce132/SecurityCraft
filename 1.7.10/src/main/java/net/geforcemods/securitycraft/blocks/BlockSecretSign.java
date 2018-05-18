package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSecretSign extends BlockContainer
{
	private boolean isStanding;

	public BlockSecretSign(boolean p_i45426_2_)
	{
		super(Material.wood);
		isStanding = p_i45426_2_;
		float f = 0.25F;
		float f1 = 1.0F;
		setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
		setStepSound(soundTypeWood);
	}

	@Override
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer)
	{
		if(world.getBlock(x, y, z) instanceof BlockSecretSign)
		{
			effectRenderer.addBlockDestroyEffects(x, y, z, Blocks.planks, 0);
			return true;
		}
		else return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta)
	{
		return Blocks.planks.getBlockTextureFromSide(side);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
	{
		return null;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
	{
		if (!isStanding)
		{
			int l = worldIn.getBlockMetadata(x, y, z);
			float f = 0.28125F;
			float f1 = 0.78125F;
			float f2 = 0.0F;
			float f3 = 1.0F;
			float f4 = 0.125F;
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

			if (l == 2)
			{
				setBlockBounds(f2, f, 1.0F - f4, f3, f1, 1.0F);
			}

			if (l == 3)
			{
				setBlockBounds(f2, f, 0.0F, f3, f1, f4);
			}

			if (l == 4)
			{
				setBlockBounds(1.0F - f4, f, f2, 1.0F, f1, f3);
			}

			if (l == 5)
			{
				setBlockBounds(0.0F, f, f2, f4, f1, f3);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(worldIn, x, y, z);
		return super.getSelectedBoundingBoxFromPool(worldIn, x, y, z);
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, int x, int y, int z)
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
	public Item getItemDropped(int meta, Random random, int fortune)
	{
		return SCContent.secretSignItem;
	}

	@Override
	public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
	{
		boolean flag = false;

		if (isStanding)
		{
			if (!worldIn.getBlock(x, y - 1, z).getMaterial().isSolid())
			{
				flag = true;
			}
		}
		else
		{
			int l = worldIn.getBlockMetadata(x, y, z);
			flag = true;

			if (l == 2 && worldIn.getBlock(x, y, z + 1).getMaterial().isSolid())
			{
				flag = false;
			}

			if (l == 3 && worldIn.getBlock(x, y, z - 1).getMaterial().isSolid())
			{
				flag = false;
			}

			if (l == 4 && worldIn.getBlock(x + 1, y, z).getMaterial().isSolid())
			{
				flag = false;
			}

			if (l == 5 && worldIn.getBlock(x - 1, y, z).getMaterial().isSolid())
			{
				flag = false;
			}
		}

		if (flag)
		{
			dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
			worldIn.setBlockToAir(x, y, z);
		}

		super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Item getItem(World worldIn, int x, int y, int z)
	{
		return SCContent.secretSignItem;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister reg) {}
}