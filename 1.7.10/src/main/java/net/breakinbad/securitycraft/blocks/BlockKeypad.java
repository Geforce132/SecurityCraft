package net.breakinbad.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.breakinbad.securitycraft.api.IPasswordProtected;
import net.breakinbad.securitycraft.gui.GuiHandler;
import net.breakinbad.securitycraft.main.Utils.ModuleUtils;
import net.breakinbad.securitycraft.main.Utils.PlayerUtils;
import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import net.breakinbad.securitycraft.misc.EnumCustomModules;
import net.breakinbad.securitycraft.tileentity.TileEntityKeypad;
import net.breakinbad.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockKeypad extends BlockContainer {

	public BlockKeypad(Material par2Material) {
		super(par2Material);
	}

	@SideOnly(Side.CLIENT)
	private IIcon keypadIconTop;
	@SideOnly(Side.CLIENT)
	private IIcon keypadIconFront;
	@SideOnly(Side.CLIENT)
	private IIcon keypadIconFrontActive;

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

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2){
		if(par1 == 3 && par2 == 0){
			return this.keypadIconFront;
		}

		if(par2 == 7 || par2 == 8 || par2 == 9 || par2 == 10){
			return par1 == 1 ? this.keypadIconTop : (par1 == 0 ? this.keypadIconTop : (par1 != (par2 - 5) ? this.blockIcon : this.keypadIconFrontActive));
		}else{
			return par1 == 1 ? this.keypadIconTop : (par1 == 0 ? this.keypadIconTop : (par1 != par2 ? this.blockIcon : this.keypadIconFront));
		}
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister){
		this.blockIcon = par1IconRegister.registerIcon("securitycraft:iron_block");
		this.keypadIconFront = par1IconRegister.registerIcon("securitycraft:keypadUnactive");
		this.keypadIconTop = par1IconRegister.registerIcon("securitycraft:iron_block");
		this.keypadIconFrontActive = par1IconRegister.registerIcon("securitycraft:keypadActive");
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	public TileEntity createNewTileEntity(World par1World, int par2){
		return new TileEntityKeypad();
	}

}
