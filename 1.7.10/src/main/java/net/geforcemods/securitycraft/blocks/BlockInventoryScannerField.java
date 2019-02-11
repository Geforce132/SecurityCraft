package net.geforcemods.securitycraft.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
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

	public BlockInventoryScannerField(Material xMaterial) {
		super(xMaterial);
		setBlockBounds(0.250F, 0.300F, 0.300F, 0.750F, 0.700F, 0.700F);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
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
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e){
		if(!world.isRemote)
		{
			TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(world, x, y, z);

			if(connectedScanner == null)
				return;

			if(e instanceof EntityPlayer)
			{
				if(ModuleUtils.checkForModule(connectedScanner.getWorld(), connectedScanner.xCoord, connectedScanner.yCoord, connectedScanner.zCoord, (EntityPlayer)e, EnumCustomModules.WHITELIST))
					return;

				for(int i = 0; i < 10; i++)
				{
					for(int j = 0; j < ((EntityPlayer)e).inventory.mainInventory.length; j++)
					{
						if(connectedScanner.getStackInSlotCopy(i) != null && ((EntityPlayer)e).inventory.mainInventory[j] != null)
							checkInventory((EntityPlayer)e, connectedScanner, connectedScanner.getStackInSlotCopy(i));
					}
				}
			}
			else if(e instanceof EntityItem)
			{
				for(int i = 0; i < 10; i++)
				{
					if(connectedScanner.getStackInSlotCopy(i) != null && ((EntityItem)e).getEntityItem() != null)
						checkEntityItem((EntityItem)e, connectedScanner, connectedScanner.getStackInSlotCopy(i));
				}
			}
		}
	}

	public void checkInventory(EntityPlayer entity, TileEntityInventoryScanner te, ItemStack stack)
	{
		if(te.getType().equals("redstone"))
		{
			for(int i = 1; i <= entity.inventory.mainInventory.length; i++)
			{
				if(entity.inventory.mainInventory[i - 1] != null)
				{
					if((((CustomizableSCTE) te).hasModule(EnumCustomModules.SMART) && areItemStacksEqual(entity.inventory.mainInventory[i - 1], stack) && ItemStack.areItemStackTagsEqual(entity.inventory.mainInventory[i - 1], stack))
							|| (!((CustomizableSCTE) te).hasModule(EnumCustomModules.SMART) && entity.inventory.mainInventory[i - 1].getItem() == stack.getItem()))
					{
						updateInventoryScannerPower(te);
					}
				}
			}
		}
		else if(te.getType().equals("check"))
		{
			for(int i = 1; i <= entity.inventory.mainInventory.length; i++)
			{
				if(entity.inventory.mainInventory[i - 1] != null)
				{
					if((((CustomizableSCTE) te).hasModule(EnumCustomModules.SMART) && areItemStacksEqual(entity.inventory.mainInventory[i - 1], stack) && ItemStack.areItemStackTagsEqual(entity.inventory.mainInventory[i - 1], stack))
							|| (!((CustomizableSCTE) te).hasModule(EnumCustomModules.SMART) && entity.inventory.mainInventory[i - 1].getItem() == stack.getItem()))
					{
						if(te.hasModule(EnumCustomModules.STORAGE))
							te.addItemToStorage(entity.inventory.mainInventory[i - 1]);

						entity.inventory.mainInventory[i - 1] = null;
					}
				}
			}
		}
	}

	public void checkEntityItem(EntityItem entity, TileEntityInventoryScanner te, ItemStack stack)
	{
		if(te.getType().equals("redstone"))
		{
			if((((CustomizableSCTE) te).hasModule(EnumCustomModules.SMART) && areItemStacksEqual(entity.getEntityItem(), stack) && ItemStack.areItemStackTagsEqual(entity.getEntityItem(), stack))
					|| (!((CustomizableSCTE) te).hasModule(EnumCustomModules.SMART) && entity.getEntityItem().getItem() == stack.getItem()))
			{
				updateInventoryScannerPower(te);
			}
		}
		else if(te.getType().equals("check"))
		{
			if((((CustomizableSCTE) te).hasModule(EnumCustomModules.SMART) && areItemStacksEqual(entity.getEntityItem(), stack) && ItemStack.areItemStackTagsEqual(entity.getEntityItem(), stack))
					|| (!((CustomizableSCTE) te).hasModule(EnumCustomModules.SMART) && entity.getEntityItem().getItem() == stack.getItem()))
			{
				if(te.hasModule(EnumCustomModules.STORAGE))
					te.addItemToStorage(entity.getEntityItem());

				entity.setDead();
			}
		}
	}

	public void updateInventoryScannerPower(TileEntityInventoryScanner te)
	{
		if(!te.shouldProvidePower())
			te.setShouldProvidePower(true);

		te.setCooldown(60);
		checkAndUpdateTEAppropriately(te);
		BlockUtils.updateAndNotify(te.getWorld(), te.xCoord, te.yCoord, te.zCoord, te.getWorld().getBlock(te.xCoord, te.yCoord, te.zCoord), 1, true);
	}

	/**
	 * See {@link ItemStack#areItemStacksEqual(ItemStack, ItemStack)} but without size restriction
	 */
	public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2)
	{
		ItemStack s1 = stack1.copy();
		ItemStack s2 = stack2.copy();

		s1.stackSize = 1;
		s2.stackSize = 1;
		return ItemStack.areItemStacksEqual(s1, s2);
	}

	private void checkAndUpdateTEAppropriately(TileEntityInventoryScanner te) {
		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(te.getWorld(), te.xCoord, te.yCoord, te.zCoord);

		if(connectedScanner == null)
			return;

		te.setShouldProvidePower(true);
		te.setCooldown(60);
		BlockUtils.updateAndNotify(te.getWorld(), te.xCoord, te.yCoord, te.zCoord, te.getWorld().getBlock(te.xCoord, te.yCoord, te.zCoord), 1, true);
		connectedScanner.setShouldProvidePower(true);
		connectedScanner.setCooldown(60);
		BlockUtils.updateAndNotify(connectedScanner.getWorld(), connectedScanner.xCoord, connectedScanner.yCoord, connectedScanner.zCoord, connectedScanner.getWorld().getBlock(connectedScanner.xCoord, connectedScanner.yCoord, connectedScanner.zCoord), 1, true);
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta)
	{
		if(!world.isRemote)
		{
			for(int i = 0; i < SecurityCraft.config.inventoryScannerRange; i++)
			{
				if(world.getBlock(x - i, y, z) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						world.breakBlock(x - j, y, z, false);
					}

					break;
				}
			}

			for(int i = 0; i < SecurityCraft.config.inventoryScannerRange; i++)
			{
				if(world.getBlock(x + i, y, z) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						world.breakBlock(x + j, y, z, false);
					}

					break;
				}
			}

			for(int i = 0; i < SecurityCraft.config.inventoryScannerRange; i++)
			{
				if(world.getBlock(x , y, z - i) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						world.breakBlock(x, y, z - j, false);
					}

					break;
				}
			}

			for(int i = 0; i < SecurityCraft.config.inventoryScannerRange; i++)
			{
				if(world.getBlock(x, y, z + i) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						world.breakBlock(x, y, z + j, false);
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
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack){
		int entityRotation = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (entityRotation == 0)
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);

		if (entityRotation == 1)
			world.setBlockMetadataWithNotify(x, y, z, 5, 2);

		if (entityRotation == 2)
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);

		if (entityRotation == 3)
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
	}

	/**
	 * Updates the blocks bounds based on its current state. Args: world, x, y, z
	 */
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z){
		if (access.getBlockMetadata(x, y, z) == 1)
			setBlockBounds(0.000F, 0.000F, 0.400F, 1.000F, 1.000F, 0.600F);
		else if (access.getBlockMetadata(x, y, z) == 2)
			setBlockBounds(0.400F, 0.000F, 0.000F, 0.600F, 1.000F, 1.000F);
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z){
		return null;
	}

	/**
	 * When this method is called, your block should register all the icons it needs with the given IconRegister. This
	 * is the only chance you get to register icons.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register){
		blockIcon = register.registerIcon("securitycraft:aniLaser");
	}
}
