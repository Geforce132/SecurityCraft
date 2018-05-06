package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityScannerDoor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockScannerDoor extends BlockContainer
{
	@SideOnly(Side.CLIENT)
	private IIcon[] upperIcons;
	@SideOnly(Side.CLIENT)
	private IIcon[] lowerIcons;

	public BlockScannerDoor(Material mat)
	{
		super(mat);
		isBlockContainer = true;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return 7;
	}

	@Override
	public boolean isPassable(IBlockAccess access, int x, int y, int z)
	{
		return (getDoorMeta(access, x, y, z) & 4) != 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z)
	{
		setBoundsBasedOnMeta(getDoorMeta(access, x, y, z));
	}

	private void setBoundsBasedOnMeta(int meta)
	{
		float f = 0.1875F;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
		int facing = meta & 3;
		boolean isOpen = (meta & 4) != 0;
		boolean isRightDoor = (meta & 16) != 0;

		if(facing == 0)
		{
			if(isOpen)
			{
				if(!isRightDoor)
					setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
				else
					setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
			}
			else
				setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
		}
		else if(facing == 1)
		{
			if (isOpen)
			{
				if (!isRightDoor)
					setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				else
					setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
			}
			else
				setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
		}
		else if(facing == 2)
		{
			if(isOpen)
			{
				if(!isRightDoor)
					setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
				else
					setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
			}
			else
				setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
		else if(facing == 3)
			if(isOpen)
			{
				if(!isRightDoor)
					setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
				else
					setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			}
			else
				setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
	}

	public void changeDoorState(World world, int x, int y, int z, boolean open)
	{
		int meta = getDoorMeta(world, x, y, z);
		boolean isOpen = (meta & 4) != 0;

		if(isOpen != open)
		{
			int newMeta = meta & 7;
			newMeta ^= 4;

			if((meta & 8) == 0)
			{
				world.setBlockMetadataWithNotify(x, y, z, newMeta, 2);
				world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
			}
			else
			{
				world.setBlockMetadataWithNotify(x, y - 1, z, newMeta, 2);
				world.markBlockRangeForRenderUpdate(x, y - 1, z, x, y, z);
			}

			world.playAuxSFXAtEntity((EntityPlayer)null, 1003, x, y, z, 0);
		}
	}

	@Override
	public void onNeighborBlockChange(World world	, int x, int y, int z, Block block)
	{
		int meta = world.getBlockMetadata(x, y, z);

		if((meta & 8) == 0)
		{
			boolean flag = false;

			if(world.getBlock(x, y + 1, z) != this)
			{
				world.setBlockToAir(x, y, z);
				flag = true;
			}

			if(flag)
				if(!world.isRemote)
					this.dropBlockAsItem(world, x, y, z, meta, 0);
		}
		else
		{
			if(world.getBlock(x, y - 1, z) != this)
				world.setBlockToAir(x, y, z);

			if(block != this)
				onNeighborBlockChange(world, x, y - 1, z, block);
		}
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune)
	{
		return (meta & 8) != 0 ? null : SCContent.scannerDoorItem;
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end)
	{
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.collisionRayTrace(world, x, y, z, start, end);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		return y >= 255 ? false : World.doesBlockHaveSolidTopSurface(world, x, y - 1, z) && super.canPlaceBlockAt(world, x, y, z) && super.canPlaceBlockAt(world, x, y + 1, z);
	}

	@Override
	public int getMobilityFlag()
	{
		return 1;
	}

	public int getDoorMeta(IBlockAccess access, int x, int y, int z)
	{
		int meta = access.getBlockMetadata(x, y, z);
		boolean isOpen = (meta & 8) != 0;
		int i1;
		int j1;

		if(isOpen)
		{
			i1 = access.getBlockMetadata(x, y - 1, z);
			j1 = meta;
		}
		else
		{
			i1 = meta;
			j1 = access.getBlockMetadata(x, y + 1, z);
		}

		boolean isRightDoor = (j1 & 1) != 0;

		return i1 & 7 | (isOpen ? 8 : 0) | (isRightDoor ? 16 : 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z)
	{
		return SCContent.scannerDoorItem;
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player)
	{
		if (player.capabilities.isCreativeMode && (meta & 8) != 0 && world.getBlock(x, y - 1, z) == this)
			world.setBlockToAir(x, y - 1, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		return lowerIcons[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side)
	{
		if(side != 1 && side != 0)
		{
			int meta = getDoorMeta(access, x, y, z);
			int facing = meta & 3;
			boolean flag = (meta & 4) != 0;
			boolean flag1 = false;
			boolean flag2 = (meta & 8) != 0;

			if(flag)
			{
				if(facing == 0 && side == 2)
					flag1 = !flag1;
				else if(facing == 1 && side == 5)
					flag1 = !flag1;
				else if(facing == 2 && side == 3)
					flag1 = !flag1;
				else if(facing == 3 && side == 4)
					flag1 = !flag1;
			}
			else
			{
				if(facing == 0 && side == 5)
					flag1 = !flag1;
				else if(facing == 1 && side == 3)
					flag1 = !flag1;
				else if(facing == 2 && side == 4)
					flag1 = !flag1;
				else if(facing == 3 && side == 2)
					flag1 = !flag1;

				if((meta & 16) != 0)
					flag1 = !flag1;
			}

			return flag2 ? upperIcons[flag1?1:0] : lowerIcons[flag1?1:0];
		}
		else
			return lowerIcons[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		upperIcons = new IIcon[2];
		lowerIcons = new IIcon[2];
		upperIcons[0] = register.registerIcon("securitycraft:scannerDoorUpper");
		lowerIcons[0] = register.registerIcon("securitycraft:reinforcedDoorLower");
		upperIcons[1] = new IconFlipped(upperIcons[0], true, false);
		lowerIcons[1] = new IconFlipped(lowerIcons[0], true, false);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityScannerDoor().activatedByView();
	}
}