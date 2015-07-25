package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import org.freeforums.geforce.securitycraft.api.IPasswordProtected;
import org.freeforums.geforce.securitycraft.gui.GuiHandler;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFurnace;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockKeypadFurnace extends BlockContainer {

	private Random random = new Random();

	public BlockKeypadFurnace(Material materialIn) {
		super(materialIn);
	}

	public boolean renderAsNormalBlock(){
		return false;
	}

	public boolean isNormalCube(){
		return false;
	}

	public boolean isOpaqueCube(){
		return false;
	}

	public int getRenderType(){
		return -1;
	}

	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(!par1World.isRemote){
			if(par5EntityPlayer.getCurrentEquippedItem() != null && par5EntityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.Codebreaker){
				activate(par1World, par2, par3, par4, par5EntityPlayer);
			}

			if(par1World.getTileEntity(par2, par3, par4) != null && par1World.getTileEntity(par2, par3, par4) instanceof TileEntityKeypadFurnace){
				if(((IPasswordProtected) par1World.getTileEntity(par2, par3, par4)).getPassword() == null){
					par5EntityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, par1World, par2, par3, par4);
				}else{
					par5EntityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, par1World, par2, par3, par4);
				}
			}
		}

		return true;
	}
	
	public static void activate(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer){
		par5EntityPlayer.openGui(mod_SecurityCraft.instance, 16, par1World, par2, par3, par4); 
		par1World.setBlockMetadataWithNotify(par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4) + 5, 3);
	}

	/**
	 * Called when the block is placed in the world.
	 */
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
		int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

		((TileEntityKeypadFurnace) par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());

		if(l == 0){
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 2);    
		}

		if(l == 1){
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);         
		}

		if(l == 2){
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);           
		}

		if(l == 3){
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);                   
		}else{
			return;
		}         
	}

	public void breakBlock(World par1World, int par2, int par3, int par4, Block par5Block, int par6){
		TileEntityKeypadFurnace tileentityfurnace = (TileEntityKeypadFurnace) par1World.getTileEntity(par2, par3, par4);

		if (tileentityfurnace != null)
		{
			for (int i1 = 0; i1 < tileentityfurnace.getSizeInventory(); ++i1)
			{
				ItemStack itemstack = tileentityfurnace.getStackInSlot(i1);

				if (itemstack != null)
				{
					float f = this.random.nextFloat() * 0.8F + 0.1F;
					float f1 = this.random.nextFloat() * 0.8F + 0.1F;
					float f2 = this.random.nextFloat() * 0.8F + 0.1F;

					while (itemstack.stackSize > 0)
					{
						int j1 = this.random.nextInt(21) + 10;

						if (j1 > itemstack.stackSize)
						{
							j1 = itemstack.stackSize;
						}

						itemstack.stackSize -= j1;
						EntityItem entityitem = new EntityItem(par1World, (double)((float)par2 + f), (double)((float)par3 + f1), (double)((float)par4 + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));

						if (itemstack.hasTagCompound())
						{
							entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
						}

						float f3 = 0.05F;
						entityitem.motionX = (double)((float)this.random.nextGaussian() * f3);
						entityitem.motionY = (double)((float)this.random.nextGaussian() * f3 + 0.2F);
						entityitem.motionZ = (double)((float)this.random.nextGaussian() * f3);
						par1World.spawnEntityInWorld(entityitem);
					}
				}
			}

			par1World.func_147453_f(par2, par3, par4, par5Block);
		}

		super.breakBlock(par1World, par2, par3, par4, par5Block, par6);
	}

	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityKeypadFurnace();
	}

}
