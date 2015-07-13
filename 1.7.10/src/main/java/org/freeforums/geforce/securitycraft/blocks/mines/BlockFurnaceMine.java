package org.freeforums.geforce.securitycraft.blocks.mines;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.api.IExplosive;
import org.freeforums.geforce.securitycraft.api.IHelpInfo;
import org.freeforums.geforce.securitycraft.blocks.BlockOwnable;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFurnaceMine extends BlockOwnable implements IExplosive, IHelpInfo {

	@SideOnly(Side.CLIENT)
    private IIcon field_149935_N;
    @SideOnly(Side.CLIENT)
    private IIcon field_149936_O;
    
	public BlockFurnaceMine(Material par2Material) {
		super(par2Material);
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	public void onBlockDestroyedByExplosion(World par1World, int par2, int par3, int par4, Explosion par5Explosion)
	{
		if (!par1World.isRemote)
		{
			this.explode(par1World, par2, par3, par4);
		}
	}

	public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5){
		if (!par1World.isRemote)
		{
			this.explode(par1World, par2, par3, par4);
		}
	}	

	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(par1World.isRemote){
			return true;
		}else{
			if(par5EntityPlayer.getCurrentEquippedItem() == null || par5EntityPlayer.getCurrentEquippedItem().getItem() != mod_SecurityCraft.remoteAccessMine){
				this.explode(par1World, par2, par3, par4);
				return true;
			}else{
				return false;	   		
			}
		}
	}	

	public void activateMine(World world, int par2, int par3, int par4) {}

	public void defuseMine(World world, int par2, int par3, int par4) {}

	public void explode(World par1World, int par2, int par3, int par4) {
		par1World.func_147480_a(par2, par3, par4, false);

		if(mod_SecurityCraft.configHandler.smallerMineExplosion){
			par1World.createExplosion((Entity)null, par2, par3, par4, 2.5F, true);
		}else{
			par1World.createExplosion((Entity)null, par2, par3, par4, 5.0F, true);
		}

	}

	/**
	 * Return whether this block can drop from an explosion.
	 */
	public boolean canDropFromExplosion(Explosion par1Explosion)
	{
		return false;
	}
	
	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2){
		if(par1 == 3 && par2 == 0){
    		return this.field_149936_O;
    	}
		
        return par1 == 1 ? this.field_149935_N : (par1 == 0 ? this.field_149935_N : (par1 != par2 ? this.blockIcon : this.field_149936_O));
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_){  	
        this.blockIcon = p_149651_1_.registerIcon("furnace_side");
        this.field_149936_O = p_149651_1_.registerIcon("furnace_front_off");
        this.field_149935_N = p_149651_1_.registerIcon("furnace_top");
    }

	public boolean isActive(World world, int par2, int par3, int par4) {
		return true;
	}

	public boolean isDefusable() {
		return false;
	}

	public String getHelpInfo() {
		return "The furnace mine will explode when a player right-clicks on it.";
	}

	public String[] getRecipe() {
		return new String[]{"The furnace mine requires: 1 furnace, 1 mine. This is a shapeless recipe."};
	}

}
