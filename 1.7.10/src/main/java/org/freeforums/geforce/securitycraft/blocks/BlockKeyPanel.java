package org.freeforums.geforce.securitycraft.blocks;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.Random;

import org.freeforums.geforce.securitycraft.api.IPasswordProtected;
import org.freeforums.geforce.securitycraft.gui.GuiHandler;
import org.freeforums.geforce.securitycraft.main.Utils.ModuleUtils;
import org.freeforums.geforce.securitycraft.main.Utils.PlayerUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeyPanel;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockKeyPanel extends BlockContainer
{
	public BlockKeyPanel(Material m)
	{
		super(m);
		setCreativeTab(mod_SecurityCraft.tabSCTechnical);
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean isNormalCube()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		return false;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4){
		return null;
	}

	/**
	 * Called when the block is placed in the world.
	 */
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
		int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

		((TileEntityOwnable) par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());

		if (l == 0){
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);         
		}

		if (l == 1){
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);
		}

		if (l == 2){
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
		}

		if (l == 3){
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
		}else{
			return;
		}
	}

	@SuppressWarnings("static-access")
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(par1World.isRemote){
			return true;
		}else{
			if(par1World.getBlockMetadata(par2, par3, par4) > 6 && par1World.getBlockMetadata(par2, par3, par4) < 11){
				return false;
			}

			if(par5EntityPlayer.getCurrentEquippedItem() == null || par5EntityPlayer.getCurrentEquippedItem().getItem() != mod_SecurityCraft.Codebreaker){
				TileEntityKeypad TEK = (TileEntityKeypad) par1World.getTileEntity(par2, par3, par4);

				if(ModuleUtils.checkForModule(par1World, par2, par3, par4, par5EntityPlayer, EnumCustomModules.WHITELIST) || ModuleUtils.checkForModule(par1World, par2, par3, par4, par5EntityPlayer, EnumCustomModules.BLACKLIST)){
					return true;
				}

				if(((IPasswordProtected) TEK).getPassword() == null){
					par5EntityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, par1World, par2, par3, par4);
				}else{
					par5EntityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, par1World, par2, par3, par4);
				}

				return true;       		
			}else if(par5EntityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.Codebreaker){
				if(mod_SecurityCraft.instance.configHandler.allowCodebreakerItem){
					if(((IPasswordProtected) par1World.getTileEntity(par2, par3, par4)).getPassword() != null && (par1World.getBlock(par2, par3, par4) == mod_SecurityCraft.Keypad && (par1World.getBlockMetadata(par2, par3, par4) <= 6 || par1World.getBlockMetadata(par2, par3, par4) >= 11))){
						activate(par1World, par2, par3, par4);
					}
				}else{	
					PlayerUtils.sendMessageToPlayer(par5EntityPlayer, "The codebreaker has been disabled through the config file.", null);  				
				}	
			}     	    	     	
		}

		return false;
	}
	
	public static void activate(World par1World, int par2, int par3, int par4){
		par1World.setBlockMetadataWithNotify(par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4) + 5, 3);
		par1World.notifyBlocksOfNeighborChange(par2, par3, par4, mod_SecurityCraft.Keypad);
		par1World.scheduleBlockUpdate(par2, par3, par4, mod_SecurityCraft.Keypad, 60);
	}

	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random){
		if(!par1World.isRemote && par1World.getBlockMetadata(par2, par3, par4) > 6 && par1World.getBlockMetadata(par2, par3, par4) < 11){
			par1World.setBlockMetadataWithNotify(par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4) - 5, 3);
		}                      
	}

	public boolean canProvidePower(){
		return true;
	}

	public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5){
		if(par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 7 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 8 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 9 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 10){
			return 15;
		}else{
			return 0;
		}
	}

	public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5){
		if(par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 7 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 8 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 9 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 10){
			return 15;
		}else{
			return 0;
		}
	}

	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4){
		return par1World.isSideSolid(par2 - 1, par3, par4, EAST ) ||
				par1World.isSideSolid(par2 + 1, par3, par4, WEST ) ||
				par1World.isSideSolid(par2, par3, par4 - 1, SOUTH) ||
				par1World.isSideSolid(par2, par3, par4 + 1, NORTH);
	}

	public Item getItemDropped(int par1, Random par2Random, int par3){
		return mod_SecurityCraft.keyPanel;
	}
	
    @SideOnly(Side.CLIENT)
    public Item getItem(World par1World, int par2, int par3, int par4){
		return mod_SecurityCraft.keyPanel;
    }

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityKeyPanel();
	}
}
