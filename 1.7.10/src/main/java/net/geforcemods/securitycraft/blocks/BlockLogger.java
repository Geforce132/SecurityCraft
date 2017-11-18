package net.geforcemods.securitycraft.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockLogger extends BlockOwnable {

	@SideOnly(Side.CLIENT)
	private IIcon field_149935_N;
	@SideOnly(Side.CLIENT)
	private IIcon field_149936_O;

	public BlockLogger(Material par1Material) {
		super(par1Material);
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(par1World.isRemote)
			return true;
		else{
			par5EntityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.USERNAME_LOGGER_GUI_ID, par1World, par2, par3, par4);
			return true;
		}
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block p_149695_5_){
		if(!par1World.isRemote)
			if(par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
				((TileEntityLogger) par1World.getTileEntity(par2, par3, par4)).attackNextTick();
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_)
	{
		super.onBlockPlacedBy(p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_, p_149689_5_, p_149689_6_);

		int l = MathHelper.floor_double(p_149689_5_.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (l == 0)
			p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, 2, 2);

		if (l == 1)
			p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, 5, 2);

		if (l == 2)
			p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, 3, 2);

		if (l == 3)
			p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, 4, 2);

	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2) {
		if(par1 == 3 && par2 == 0)
			return field_149936_O;

		return par1 == 1 ? field_149935_N : (par1 == 0 ? field_149935_N : (par1 != par2 ? blockIcon : field_149936_O));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IIconRegister)
	{
		blockIcon = par1IIconRegister.registerIcon("securitycraft:usernameLoggerSide");
		field_149936_O = par1IIconRegister.registerIcon("securitycraft:usernameLoggerFront");
		field_149935_N = par1IIconRegister.registerIcon("securitycraft:usernameLoggerTop");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par1) {
		return new TileEntityLogger().attacks(EntityPlayer.class, mod_SecurityCraft.configHandler.usernameLoggerSearchRadius, 80);
	}

}
