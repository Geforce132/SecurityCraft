package net.geforcemods.securitycraft.blocks;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.tileentity.TileEntityAlarm;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockAlarm extends BlockContainer {

	private final boolean isLit;

	public BlockAlarm(Material material, boolean isLit) {
		super(material);

		this.isLit = isLit;

		if(this.isLit)
			setLightLevel(1.0F);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean isNormalCube(){
		return false;
	}

	@Override
	public int getRenderType(){
		return -1;
	}

	/**
	 * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
	 */
	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side)
	{
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		return (dir == DOWN  && world.isSideSolid(x, y + 1, z, DOWN )) ||
				(dir == UP    && world.isSideSolid(x, y - 1, z, UP   )) ||
				(dir == NORTH && world.isSideSolid(x, y, z + 1, NORTH)) ||
				(dir == SOUTH && world.isSideSolid(x, y, z - 1, SOUTH)) ||
				(dir == WEST  && world.isSideSolid(x + 1, y, z, WEST )) ||
				(dir == EAST  && world.isSideSolid(x - 1, y, z, EAST ));
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		return world.isSideSolid(x - 1, y, z, EAST ) ||
				world.isSideSolid(x + 1, y, z, WEST ) ||
				world.isSideSolid(x, y, z - 1, SOUTH) ||
				world.isSideSolid(x, y, z + 1, NORTH) ||
				world.isSideSolid(x, y - 1, z, UP   ) ||
				world.isSideSolid(x, y + 1, z, DOWN );
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		if(world.isRemote)
			return;
		else
			world.scheduleBlockUpdate(x, y, z, this, 1);
	}

	/**
	 * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
	 */
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
	{
		int k1 = meta & 8;
		byte b0 = -1;

		if (side == 0 && world.isSideSolid(x, y + 1, z, DOWN))
			b0 = 0;

		if (side == 1 && world.isSideSolid(x, y - 1, z, UP))
			b0 = 5;

		if (side == 2 && world.isSideSolid(x, y, z + 1, NORTH))
			b0 = 4;

		if (side == 3 && world.isSideSolid(x, y, z - 1, SOUTH))
			b0 = 3;

		if (side == 4 && world.isSideSolid(x + 1, y, z, WEST))
			b0 = 2;

		if (side == 5 && world.isSideSolid(x - 1, y, z, EAST))
			b0 = 1;

		return b0 + k1;
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack){
		int meta = world.getBlockMetadata(x, y, z);

		if (meta == 0)
			world.setBlockMetadataWithNotify(x, y, z, 0, 2);
		else if (meta == 1)
			world.setBlockMetadataWithNotify(x, y, z, 1, 2);
		else if (meta == 2)
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
		else if (meta == 3)
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);
		else if(meta == 4)
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
		else if(meta == 5)
			world.setBlockMetadataWithNotify(x, y, z, 5, 2);
	}

	public static int invertMetadata(int meta)
	{
		switch (meta)
		{
			case 0:
				return 0;
			case 1:
				return 5;
			case 2:
				return 4;
			case 3:
				return 3;
			case 4:
				return 2;
			case 5:
				return 1;
			default:
				return -1;
		}
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World world, int x, int y, int z, Random random){
		if(!world.isRemote){
			playSoundAndUpdate(world, x, y, z);

			world.scheduleBlockUpdate(x, y, z, this, 5);
		}
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block){
		if(!world.isRemote)
			playSoundAndUpdate(world, x, y, z);

		if (canPlaceAt(world, x, y, z))
		{
			int meta = world.getBlockMetadata(x, y, z) & 7;
			boolean notSolid = false;

			if (!world.isSideSolid(x, y + 1, z, DOWN) && meta == 0)
				notSolid = true;
			else if (!world.isSideSolid(x - 1, y, z, EAST) && meta == 1)
				notSolid = true;
			else if (!world.isSideSolid(x + 1, y, z, WEST) && meta == 2)
				notSolid = true;
			else if (!world.isSideSolid(x, y, z - 1, SOUTH) && meta == 3)
				notSolid = true;
			else if (!world.isSideSolid(x, y, z + 1, NORTH) && meta == 4)
				notSolid = true;
			else if (!world.isSideSolid(x, y - 1, z, UP) && meta == 5)
				notSolid = true;

			if (notSolid)
			{
				this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
				world.setBlockToAir(x, y, z);
			}
		}
	}

	/**
	 * Also automatically drops the block and sets it to air if not possible to place the block at the given position
	 */
	private boolean canPlaceAt(World world, int x, int y, int z)
	{
		if (!canPlaceBlockAt(world, x, y, z))
		{
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
			return false;
		}
		else
			return true;
	}

	/**
	 * Updates the blocks bounds based on its current state. Args: world, x, y, z
	 */
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z)
	{
		int meta = access.getBlockMetadata(x, y, z) & 7;
		float f = 0.1875F;
		float ySideMin = 0.44F - f; //bottom of the alarm when placed on a block side
		float ySideMax = 0.56F + f; //top of the alarm when placed on a block side
		float hSideMin = 0.44F - f; //the left start for s/w and right start for n/e
		float hSideMax = 0.56F + f; //the left start for n/e and right start for s/w
		float px = 1.0F / 16.0F; //one sixteenth of a block

		switch(meta)
		{
			case 0: //down
				setBlockBounds(0.5F - f - px, 0.5F, 0.5F - f - px, 0.5F + f + px, 1.0F, 0.5F + f + px);
				break;
			case 1: //east
				setBlockBounds(0.0F, ySideMin, hSideMin, 0.5F, ySideMax, hSideMax);
				break;
			case 2: //west
				setBlockBounds(0.5F, ySideMin, hSideMin, 1.0F, ySideMax, hSideMax);
				break;
			case 3: //north
				setBlockBounds(hSideMin, ySideMin, 0.0F, hSideMax, ySideMax, 0.5F);
				break;
			case 4: //south
				setBlockBounds(hSideMin, ySideMin, 0.5F, hSideMax, ySideMax, 1.0F);
				break;
			case 5: //up
				setBlockBounds(0.5F - f - px, 0.0F, 0.5F - f - px, 0.5F + f + px, 0.5F, 0.5F + f + px);
		}
	}

	private void playSoundAndUpdate(World world, int x, int y, int z){
		if(!(world.getTileEntity(x, y, z) instanceof TileEntityAlarm)) return;

		if(world.isBlockIndirectlyGettingPowered(x, y, z)){
			boolean isPowered = ((TileEntityAlarm) world.getTileEntity(x, y, z)).isPowered();

			if(!isPowered){
				Owner owner = ((TileEntityAlarm) world.getTileEntity(x, y, z)).getOwner();
				world.setBlock(x, y, z, SCContent.alarmLit, world.getBlockMetadata(x, y, z), 3);
				((TileEntityAlarm) world.getTileEntity(x, y, z)).getOwner().set(owner);
				((TileEntityAlarm) world.getTileEntity(x, y, z)).setPowered(true);
			}

		}else{
			boolean isPowered = ((TileEntityAlarm) world.getTileEntity(x, y, z)).isPowered();

			if(isPowered){
				Owner owner = ((TileEntityAlarm) world.getTileEntity(x, y, z)).getOwner();
				world.setBlock(x, y, z, SCContent.alarm, world.getBlockMetadata(x, y, z), 3);
				((TileEntityAlarm) world.getTileEntity(x, y, z)).getOwner().set(owner);
				((TileEntityAlarm) world.getTileEntity(x, y, z)).setPowered(false);
			}
		}
	}

	/**
	 * Gets an item for the block being called on. Args: world, x, y, z
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z){
		return Item.getItemFromBlock(SCContent.alarm);
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune){
		return Item.getItemFromBlock(SCContent.alarm);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityAlarm();
	}

}
