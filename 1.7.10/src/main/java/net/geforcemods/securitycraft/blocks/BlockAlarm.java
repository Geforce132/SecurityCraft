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
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityAlarm;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockAlarm extends BlockContainer {

	private final boolean isLit;

	public BlockAlarm(Material par1Material, boolean isLit) {
		super(par1Material);
		float f = 0.2F;
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.5F, 0.5F + f);

		this.isLit = isLit;

		if(this.isLit){
			this.setLightLevel(1.0F);
		}
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		return false;
	}
	
	public boolean isOpaqueCube(){
        return false;
    }
    
    public boolean isNormalCube(){
        return false;
    } 
    
    public int getRenderType(){
    	return -1;
    }

	/**
	 * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
	 */
	public boolean canPlaceBlockOnSide(World p_149707_1_, int p_149707_2_, int p_149707_3_, int p_149707_4_, int p_149707_5_)
	{
		ForgeDirection dir = ForgeDirection.getOrientation(p_149707_5_);
		return (dir == DOWN  && p_149707_1_.isSideSolid(p_149707_2_, p_149707_3_ + 1, p_149707_4_, DOWN )) ||
				(dir == UP    && p_149707_1_.isSideSolid(p_149707_2_, p_149707_3_ - 1, p_149707_4_, UP   )) ||
				(dir == NORTH && p_149707_1_.isSideSolid(p_149707_2_, p_149707_3_, p_149707_4_ + 1, NORTH)) ||
				(dir == SOUTH && p_149707_1_.isSideSolid(p_149707_2_, p_149707_3_, p_149707_4_ - 1, SOUTH)) ||
				(dir == WEST  && p_149707_1_.isSideSolid(p_149707_2_ + 1, p_149707_3_, p_149707_4_, WEST )) ||
				(dir == EAST  && p_149707_1_.isSideSolid(p_149707_2_ - 1, p_149707_3_, p_149707_4_, EAST ));
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	public boolean canPlaceBlockAt(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_)
	{
		return p_149742_1_.isSideSolid(p_149742_2_ - 1, p_149742_3_, p_149742_4_, EAST ) ||
				p_149742_1_.isSideSolid(p_149742_2_ + 1, p_149742_3_, p_149742_4_, WEST ) ||
				p_149742_1_.isSideSolid(p_149742_2_, p_149742_3_, p_149742_4_ - 1, SOUTH) ||
				p_149742_1_.isSideSolid(p_149742_2_, p_149742_3_, p_149742_4_ + 1, NORTH) ||
				p_149742_1_.isSideSolid(p_149742_2_, p_149742_3_ - 1, p_149742_4_, UP   ) ||
				p_149742_1_.isSideSolid(p_149742_2_, p_149742_3_ + 1, p_149742_4_, DOWN );
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	public void onBlockAdded(World par1World, int par2, int par3, int par4) {
		if(par1World.isRemote){
			return;
		}else{
			par1World.scheduleBlockUpdate(par2, par3, par4, this, 1);
		}
	}

	/**
	 * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
	 */
	public int onBlockPlaced(World p_149660_1_, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_)
	{
		int k1 = p_149660_9_ & 8;
		byte b0 = -1;

		if (p_149660_5_ == 0 && p_149660_1_.isSideSolid(p_149660_2_, p_149660_3_ + 1, p_149660_4_, DOWN))
		{
			b0 = 0;
		}

		if (p_149660_5_ == 1 && p_149660_1_.isSideSolid(p_149660_2_, p_149660_3_ - 1, p_149660_4_, UP))
		{
			b0 = 5;
		}

		if (p_149660_5_ == 2 && p_149660_1_.isSideSolid(p_149660_2_, p_149660_3_, p_149660_4_ + 1, NORTH))
		{
			b0 = 4;
		}

		if (p_149660_5_ == 3 && p_149660_1_.isSideSolid(p_149660_2_, p_149660_3_, p_149660_4_ - 1, SOUTH))
		{
			b0 = 3;
		}

		if (p_149660_5_ == 4 && p_149660_1_.isSideSolid(p_149660_2_ + 1, p_149660_3_, p_149660_4_, WEST))
		{
			b0 = 2;
		}

		if (p_149660_5_ == 5 && p_149660_1_.isSideSolid(p_149660_2_ - 1, p_149660_3_, p_149660_4_, EAST))
		{
			b0 = 1;
		}

		return b0 + k1;
	}

	/**
	 * Called when the block is placed in the world.
	 */
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack p_149689_6_){
		((TileEntityOwnable) par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());
		int l = par1World.getBlockMetadata(par2, par3, par4);

		if (l == 0)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 0, 2);
		else if (l == 1)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 2);
		else if (l == 2)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
		else if (l == 3)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
		else if(l == 4)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
		else if(l == 5)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);
	}

	public static int invertMetadata(int p_149819_0_)
	{
		switch (p_149819_0_)
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
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random){
		if(!par1World.isRemote){
			this.playSoundAndUpdate(par1World, par2, par3, par4);

			par1World.scheduleBlockUpdate(par2, par3, par4, this, 5);
		}
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5Block){
		if(!par1World.isRemote){
			this.playSoundAndUpdate(par1World, par2, par3, par4);
		}
		
		if (this.func_149820_e(par1World, par2, par3, par4))
		{
			int l = par1World.getBlockMetadata(par2, par3, par4) & 7;
			boolean flag = false;

			if (!par1World.isSideSolid(par2, par3 + 1, par4, DOWN) && l == 0)
				flag = true;
			else if (!par1World.isSideSolid(par2 - 1, par3, par4, EAST) && l == 1)
				flag = true;
			else if (!par1World.isSideSolid(par2 + 1, par3, par4, WEST) && l == 2)
				flag = true;
			else if (!par1World.isSideSolid(par2, par3, par4 - 1, SOUTH) && l == 3)
				flag = true;
			else if (!par1World.isSideSolid(par2, par3, par4 + 1, NORTH) && l == 4)
				flag = true;
			else if (!par1World.isSideSolid(par2, par3 - 1, par4, UP) && l == 5)
				flag = true;

			if (flag)
			{
				this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
				par1World.setBlockToAir(par2, par3, par4);
			}
		}
	}

	private boolean func_149820_e(World p_149820_1_, int p_149820_2_, int p_149820_3_, int p_149820_4_)
	{
		if (!this.canPlaceBlockAt(p_149820_1_, p_149820_2_, p_149820_3_, p_149820_4_))
		{
			this.dropBlockAsItem(p_149820_1_, p_149820_2_, p_149820_3_, p_149820_4_, p_149820_1_.getBlockMetadata(p_149820_2_, p_149820_3_, p_149820_4_), 0);
			p_149820_1_.setBlockToAir(p_149820_2_, p_149820_3_, p_149820_4_);
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * Updates the blocks bounds based on its current state. Args: world, x, y, z
	 */
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
	{
		int l = p_149719_1_.getBlockMetadata(p_149719_2_, p_149719_3_, p_149719_4_) & 7;
		float f = 0.1875F;
		float ySideMin = 0.44F - f; //bottom of the alarm when placed on a block side
		float ySideMax = 0.56F + f; //top of the alarm when placed on a block side
		float hSideMin = 0.44F - f; //the left start for s/w and right start for n/e
		float hSideMax = 0.56F + f; //the left start for n/e and right start for s/w

		switch(l)
		{
			case 0: //down
				f = 0.25F;
				this.setBlockBounds(0.5F - f, 0.5F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
				break;
			case 1: //east
				this.setBlockBounds(0.0F, ySideMin, hSideMin, 0.5F, ySideMax, hSideMax);
				break;
			case 2: //west
				this.setBlockBounds(0.5F, ySideMin, hSideMin, 1.0F, ySideMax, hSideMax);
				break;
			case 3: //north
				this.setBlockBounds(hSideMin, ySideMin, 0.0F, hSideMax, ySideMax, 0.5F);
				break;
			case 4: //south
				this.setBlockBounds(hSideMin, ySideMin, 0.5F, hSideMax, ySideMax, 1.0F);
				break;
			case 5: //up
			{
				f = 0.25F;
				this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.5F, 0.5F + f);
			}
		}
	}

	private void playSoundAndUpdate(World par1World, int par2, int par3, int par4){
		if(par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)){
			boolean isPowered = ((TileEntityAlarm) par1World.getTileEntity(par2, par3, par4)).isPowered();

			if(!isPowered){
				((TileEntityAlarm) par1World.getTileEntity(par2, par3, par4)).setPowered(true);
			}

		}else{
			boolean isPowered = ((TileEntityAlarm) par1World.getTileEntity(par2, par3, par4)).isPowered();

			if(isPowered){
				((TileEntityAlarm) par1World.getTileEntity(par2, par3, par4)).setPowered(false);
			}
		}
	}

	/**
	 * Gets an item for the block being called on. Args: world, x, y, z
	 */
	@SideOnly(Side.CLIENT)
	public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_){
		return Item.getItemFromBlock(mod_SecurityCraft.alarm);
	}

	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_){
		return Item.getItemFromBlock(mod_SecurityCraft.alarm);
	}

	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityAlarm();
	}

}
