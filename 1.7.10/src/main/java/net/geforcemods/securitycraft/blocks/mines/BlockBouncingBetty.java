package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.entity.EntityTnTCompact;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class BlockBouncingBetty extends BlockExplosive implements IExplosive {

	public BlockBouncingBetty(Material par2Material) {
		super(par2Material);
		this.setBlockBounds(0.200F, 0.000F, 0.200F, 0.800F, 0.200F, 0.800F);
	}

	public boolean isOpaqueCube(){
		return false;
	}

	public boolean renderAsNormalBlock(){
		return false;
	}

	/**
	 * Sets the block's bounds for rendering it as an item
	 */
	public void setBlockBoundsForItemRender(){
		this.setBlockBounds(0.200F, 0.000F, 0.200F, 0.800F, 0.200F, 0.800F);
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity){
		if(par5Entity instanceof EntityLivingBase){
			par1World.setBlockToAir(par2, par3, par4);
			EntityTnTCompact entitytntprimed = new EntityTnTCompact(par1World, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F));
			entitytntprimed.fuse = 15;
			entitytntprimed.motionY = 0.50D;
			par1World.spawnEntityInWorld(entitytntprimed);
			par1World.playSoundAtEntity(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
		}else{
			return;
		}

	}

	/**
	 * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
	 */
	public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer){
		if(par5EntityPlayer instanceof EntityLivingBase){
			this.explode(par1World, par2, par3, par4);
		}else{
			return;
		}
	}
		
	public void activateMine(World world, int par2, int par3, int par4) {}

	public void defuseMine(World world, int par2, int par3, int par4) {}

	public void explode(World world, int par2, int par3, int par4) {
		world.setBlockToAir(par2, par3, par4);
		EntityTnTCompact entitytntprimed = new EntityTnTCompact(world, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F));
		entitytntprimed.fuse = 15;
		entitytntprimed.motionY = 0.50D;
		world.spawnEntityInWorld(entitytntprimed);
		world.playSoundAtEntity(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
	}

	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(par1World.isRemote){
			return true;
		}else{
			if(par5EntityPlayer.getCurrentEquippedItem() == null || par5EntityPlayer.getCurrentEquippedItem().getItem() != mod_SecurityCraft.remoteAccessMine){
				this.explode(par1World, par2, par3, par4);
				return false;
			}else{
				return false;
			}
		}
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	public Item getItemDropped(int par1, Random par2Random, int par3){
		return BlockUtils.getItemFromBlock(this);
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	public Item getItem(World par1World, int par2, int par3, int par4){
		return BlockUtils.getItemFromBlock(this);
	}

	public void registerBlockIcons(IIconRegister par1IconRegister){
		this.blockIcon = par1IconRegister.registerIcon("securitycraft:bouncingBetty");
	}
	
	public boolean isActive(World world, int par2, int par3, int par4) {
		return false;
	}

	public boolean isDefusable() {
		return false;
	}

}
