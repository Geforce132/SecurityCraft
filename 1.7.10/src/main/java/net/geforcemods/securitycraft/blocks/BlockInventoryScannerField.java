package net.geforcemods.securitycraft.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockInventoryScannerField extends Block{

	public BlockInventoryScannerField(Material par2Material) {
		super(par2Material);
		setBlockBounds(0.250F, 0.300F, 0.300F, 0.750F, 0.700F, 0.700F);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4){
		return null;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity){
		if(!par1World.isRemote)
		{
			TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(par1World, par2, par3, par4);

			if(par5Entity instanceof EntityPlayer)
			{
				if(ModuleUtils.checkForModule(connectedScanner.getWorld(), connectedScanner.xCoord, connectedScanner.yCoord, connectedScanner.zCoord, (EntityPlayer)par5Entity, EnumCustomModules.WHITELIST))
					return;

				for(int i = 0; i < 10; i++)
				{
					for(int j = 0; j < ((EntityPlayer)par5Entity).inventory.mainInventory.length; j++)
					{
						if(connectedScanner.getStackInSlotCopy(i) != null && ((EntityPlayer)par5Entity).inventory.mainInventory[j] != null)
							checkInventory((EntityPlayer)par5Entity, connectedScanner, connectedScanner.getStackInSlotCopy(i));
					}
				}
			}
			else if(par5Entity instanceof EntityItem)
			{
				for(int i = 0; i < 10; i++)
				{
					if(connectedScanner.getStackInSlotCopy(i) != null && ((EntityItem)par5Entity).getEntityItem() != null)
						checkEntity((EntityItem)par5Entity, connectedScanner.getStackInSlotCopy(i));
				}
			}
		}
	}

	public void checkInventory(EntityPlayer par1EntityPlayer, TileEntityInventoryScanner par2TileEntity, ItemStack par3){
		if(par2TileEntity.getType().matches("redstone")){
			for(int i = 1; i <= par1EntityPlayer.inventory.mainInventory.length; i++)
				if(par1EntityPlayer.inventory.mainInventory[i - 1] != null)
					if(par1EntityPlayer.inventory.mainInventory[i - 1].getItem() == par3.getItem()){
						if(!par2TileEntity.shouldProvidePower())
							par2TileEntity.setShouldProvidePower(true);

						par2TileEntity.setCooldown(60);
						checkAndUpdateTEAppropriately(par2TileEntity);
						BlockUtils.updateAndNotify(par2TileEntity.getWorld(), par2TileEntity.xCoord, par2TileEntity.yCoord, par2TileEntity.zCoord, par2TileEntity.getWorld().getBlock(par2TileEntity.xCoord, par2TileEntity.yCoord, par2TileEntity.zCoord), 1, true);
					}
		}else if(par2TileEntity.getType().matches("check"))
			if(par2TileEntity.hasModule(EnumCustomModules.STORAGE)){
				for(int i = 1; i <= par1EntityPlayer.inventory.mainInventory.length; i++)
					if(par1EntityPlayer.inventory.mainInventory[i - 1] != null)
						if(par1EntityPlayer.inventory.mainInventory[i - 1].getItem() == par3.getItem()){
							par2TileEntity.addItemToStorage(par1EntityPlayer.inventory.mainInventory[i - 1]);
							par1EntityPlayer.inventory.mainInventory[i - 1] = null;
						}
			}
			else
				for(int i = 1; i <= par1EntityPlayer.inventory.mainInventory.length; i++)
					if(par1EntityPlayer.inventory.mainInventory[i - 1] != null)
						if(par1EntityPlayer.inventory.mainInventory[i - 1].getItem() == par3.getItem())
							par1EntityPlayer.inventory.mainInventory[i - 1] = null;
	}

	public void checkEntity(EntityItem par1EntityItem, ItemStack par2){
		if(par1EntityItem.getEntityItem().getItem() == par2.getItem())
			par1EntityItem.setDead();
	}

	private void checkAndUpdateTEAppropriately(TileEntityInventoryScanner par5TileEntityIS) {
		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(par5TileEntityIS.getWorld(), par5TileEntityIS.xCoord, par5TileEntityIS.yCoord, par5TileEntityIS.zCoord);

		par5TileEntityIS.setShouldProvidePower(true);
		par5TileEntityIS.setCooldown(60);
		BlockUtils.updateAndNotify(par5TileEntityIS.getWorld(), par5TileEntityIS.xCoord, par5TileEntityIS.yCoord, par5TileEntityIS.zCoord, par5TileEntityIS.getWorld().getBlock(par5TileEntityIS.xCoord, par5TileEntityIS.yCoord, par5TileEntityIS.zCoord), 1, true);
		connectedScanner.setShouldProvidePower(true);
		connectedScanner.setCooldown(60);
		BlockUtils.updateAndNotify(connectedScanner.getWorld(), connectedScanner.xCoord, connectedScanner.yCoord, connectedScanner.zCoord, connectedScanner.getWorld().getBlock(connectedScanner.xCoord, connectedScanner.yCoord, connectedScanner.zCoord), 1, true);
	}

	@Override
	public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5)
	{
		if(!par1World.isRemote)
		{
			for(int i = 0; i < SecurityCraft.config.inventoryScannerRange; i++)
			{
				if(par1World.getBlock(par2 - i, par3, par4) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						par1World.breakBlock(par2 - j, par3, par4, false);
					}

					break;
				}
			}

			for(int i = 0; i < SecurityCraft.config.inventoryScannerRange; i++)
			{
				if(par1World.getBlock(par2 + i, par3, par4) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						par1World.breakBlock(par2 + j, par3, par4, false);
					}

					break;
				}
			}

			for(int i = 0; i < SecurityCraft.config.inventoryScannerRange; i++)
			{
				if(par1World.getBlock(par2 , par3, par4 - i) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						par1World.breakBlock(par2, par3, par4 - j, false);
					}

					break;
				}
			}

			for(int i = 0; i < SecurityCraft.config.inventoryScannerRange; i++)
			{
				if(par1World.getBlock(par2, par3, par4 + i) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						par1World.breakBlock(par2, par3, par4 + j, false);
					}

					break;
				}
			}
		}
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
		int l = MathHelper.floor_double(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (l == 0)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);

		if (l == 1)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);

		if (l == 2)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);

		if (l == 3)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
	}

	/**
	 * Updates the blocks bounds based on its current state. Args: world, x, y, z
	 */
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4){
		if (par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 1)
			setBlockBounds(0.000F, 0.000F, 0.400F, 1.000F, 1.000F, 0.600F);
		else if (par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 2)
			setBlockBounds(0.400F, 0.000F, 0.000F, 0.600F, 1.000F, 1.000F);
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World par1World, int par2, int par3, int par4){
		return null;
	}

	/**
	 * When this method is called, your block should register all the icons it needs with the given IconRegister. This
	 * is the only chance you get to register icons.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister){
		blockIcon = par1IconRegister.registerIcon("securitycraft:aniLaser");
	}

}
