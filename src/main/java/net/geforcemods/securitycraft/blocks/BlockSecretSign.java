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

	public BlockSecretSign(boolean isStanding)
	{
		super(Material.wood);
		this.isStanding = isStanding;
		float fourth = 0.25F;
		float full = 1.0F;
		setBlockBounds(0.5F - fourth, 0.0F, 0.5F - fourth, 0.5F + fourth, full, 0.5F + fourth);
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
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		if (!isStanding)
		{
			int meta = world.getBlockMetadata(x, y, z);
			float fourthAndHalf = 0.28125F;
			float twelfthAndHalf = 0.78125F;
			float nothing = 0.0F;
			float full = 1.0F;
			float eigth = 0.125F;
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

			if (meta == 2)
			{
				setBlockBounds(nothing, fourthAndHalf, 1.0F - eigth, full, twelfthAndHalf, 1.0F);
			}

			if (meta == 3)
			{
				setBlockBounds(nothing, fourthAndHalf, 0.0F, full, twelfthAndHalf, eigth);
			}

			if (meta == 4)
			{
				setBlockBounds(1.0F - eigth, fourthAndHalf, nothing, 1.0F, twelfthAndHalf, full);
			}

			if (meta == 5)
			{
				setBlockBounds(0.0F, fourthAndHalf, nothing, eigth, twelfthAndHalf, full);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
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
	public boolean isPassable(IBlockAccess world, int x, int y, int z)
	{
		return true;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntitySecretSign();
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune)
	{
		return SCContent.secretSignItem;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
	{
		boolean flag = false;

		if (isStanding)
		{
			if (!world.getBlock(x, y - 1, z).getMaterial().isSolid())
			{
				flag = true;
			}
		}
		else
		{
			int meta = world.getBlockMetadata(x, y, z);
			flag = true;

			if (meta == 2 && world.getBlock(x, y, z + 1).getMaterial().isSolid())
			{
				flag = false;
			}

			if (meta == 3 && world.getBlock(x, y, z - 1).getMaterial().isSolid())
			{
				flag = false;
			}

			if (meta == 4 && world.getBlock(x + 1, y, z).getMaterial().isSolid())
			{
				flag = false;
			}

			if (meta == 5 && world.getBlock(x - 1, y, z).getMaterial().isSolid())
			{
				flag = false;
			}
		}

		if (flag)
		{
			dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
		}

		super.onNeighborBlockChange(world, x, y, z, neighbor);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Item getItem(World world, int x, int y, int z)
	{
		return SCContent.secretSignItem;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister reg) {}
}