package org.freeforums.geforce.securitycraft.blocks.mines;

import java.util.Random;

import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.interfaces.IExplosive;
import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

public class BlockFurnaceMine extends BlockFurnace implements IExplosive, IHelpInfo {

	public BlockFurnaceMine(Material par2Material) {
		super(false);
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

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	public Item getItemDropped(int par1, Random par2Random, int par3){
		return null;
	}

	public boolean isActive(World world, int par2, int par3, int par4) {
		return true;
	}

	public boolean isDefusable() {
		return false;
	}

	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityOwnable();
	}

	public String getHelpInfo() {
		return "The furnace mine will explode when a player right-clicks on it.";
	}

	public String[] getRecipe() {
		return new String[]{"The furnace mine requires: 1 furnace, 1 mine. This is a shapeless recipe."};
	}

}
