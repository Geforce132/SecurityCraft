package org.freeforums.geforce.securitycraft.blocks.mines;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityMineLoc;

public class BlockMine extends BlockExplosive implements IHelpInfo {

	public boolean cut;

	public BlockMine(Material par1Material, boolean cut) {
		super(par1Material);
		float f = 0.2F;
		float g = 0.1F;
		this.cut = cut;
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, (g * 2.0F) / 2 + 0.1F, 0.5F + f);
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor blockID
	 */
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5){
		if (par1World.getBlock(par2, par3 - 1, par4).getMaterial() != Material.air){
			return;
		}else{
			HelpfulMethods.destroyBlock(par1World, par2, par3, par4, true);
		}
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4){
		if(par1World.getBlock(par2, par3 - 1, par4).getMaterial() == Material.glass || par1World.getBlock(par2, par3 - 1, par4).getMaterial() == Material.cactus || par1World.getBlock(par2, par3 - 1, par4).getMaterial() == Material.air || par1World.getBlock(par2, par3 - 1, par4).getMaterial() == Material.cake || par1World.getBlock(par2, par3 - 1, par4).getMaterial() == Material.plants){
			return false;
		}else{
			return true;
		}
	}

	public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5){
		this.explode(par1World, par2, par3, par4);
	}

	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(par1World.isRemote){
			return true;
		}else{
			if(par5EntityPlayer.getCurrentEquippedItem() == null || !isInteractibleItem(par5EntityPlayer.getCurrentEquippedItem().getItem())){
				this.explode(par1World, par2, par3, par4);
				return false;
			}else if(par5EntityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.wireCutters && par1World.getBlock(par2, par3, par4) == mod_SecurityCraft.Mine){
				par1World.setBlock(par2, par3, par4, mod_SecurityCraft.MineCut);
				par5EntityPlayer.getCurrentEquippedItem().damageItem(1, par5EntityPlayer);
				return true;
			}else if(par5EntityPlayer.getCurrentEquippedItem().getItem() == Items.flint_and_steel && par1World.getBlock(par2, par3, par4) == mod_SecurityCraft.MineCut){
				par1World.setBlock(par2, par3, par4, mod_SecurityCraft.Mine);
				return true;
			}else{
				return false;	   		
			}
		}
	}

	private boolean isInteractibleItem(Item item){
		if(item == mod_SecurityCraft.wireCutters || item == mod_SecurityCraft.remoteAccessMine || item == Items.flint_and_steel){
			return true;
		}else{
			return false;
		}

	}
	
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity){
		if(par1World.isRemote){
			return;
		}else{
			if(par5Entity instanceof EntityCreeper || par5Entity instanceof EntityOcelot || par5Entity instanceof EntityEnderman || par5Entity instanceof EntityItem){
				return;
			}else{
				this.explode(par1World, par2, par3, par4);
			}
		}
	}
	
	public void activateMine(World world, int par2, int par3, int par4) {
		if(!world.isRemote){
			world.setBlock(par2, par3, par4, mod_SecurityCraft.Mine);
		}
	}

	public void defuseMine(World world, int par2, int par3, int par4) {
		if(!world.isRemote){
			world.setBlock(par2, par3, par4, mod_SecurityCraft.MineCut);
		}
	}

	public void explode(World par1World, int par2, int par3, int par4) {
		if(!cut){
			par1World.func_147480_a(par2, par3, par4, false);
			if(mod_SecurityCraft.configHandler.smallerMineExplosion){
				this.newExplosion((Entity)null, (double) par2, (double) par3, (double) par4,  1.0F, true, true, par1World);
			}else{
				this.newExplosion((Entity)null, (double) par2, (double) par3, (double) par4,  3.0F, true, true, par1World);
			}
		}
	}

	/**
	 * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
	 */
	public Explosion newExplosion(Entity par1Entity, double par2, double par4, double par6, float par8, boolean par9, boolean par10, World par11World){
		Explosion explosion = new Explosion(par11World, par1Entity, par2, par4, par6, par8);
		if(mod_SecurityCraft.configHandler.shouldSpawnFire){
			explosion.isFlaming = true;
		}else{
			explosion.isFlaming = false;
		}
		explosion.isSmoking = par10;
		explosion.doExplosionA();


		explosion.doExplosionB(true);
		return explosion;
	}

	public void registerBlockIcons(IIconRegister par1IconRegister){
		if(cut){
			this.blockIcon = par1IconRegister.registerIcon("securitycraft:mineCut");
		}else{
			this.blockIcon = par1IconRegister.registerIcon("securitycraft:mine");
		}

	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	public Item getItemDropped(int par1, Random par2Random, int par3){
		return HelpfulMethods.getItemFromBlock(mod_SecurityCraft.Mine);
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	public Item getItem(World par1World, int par2, int par3, int par4){
		return HelpfulMethods.getItemFromBlock(mod_SecurityCraft.Mine);
	}
	
	public boolean isActive(World world, int par2, int par3, int par4) {
		return !cut;
	}

	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityMineLoc();
	}

	public String getHelpInfo() {
		return "The mine explodes when stepped on by any entity other then creepers, cats, and ocelots. Right-clicking the mine while holding wire cutters will defuse the mine and allow you to break it. Right-clicking with flint and steel equipped will re-enable it.";
	}

	public String[] getRecipe() {
		return new String[]{"The mine requires: 3 iron ingots, 1 gunpowder", " X ", "XYX", "   ", "X = iron ingot, Y = gunpowder"};
	}
	
}
