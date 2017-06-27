package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockBouncingBetty extends BlockExplosive implements IExplosive {
	
	@SideOnly(Side.CLIENT)
	private IIcon defusedIcon;
	
	@SideOnly(Side.CLIENT)
	private IIcon activeIcon;
	
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
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4){
		return par1World.isSideSolid(par2, par3 - 1, par4, ForgeDirection.UP);
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity){
		if(par5Entity instanceof EntityLivingBase){
			explode(par1World, par2, par3, par4);
		}
	}

	/**
	 * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
	 */
	public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer){
		this.explode(par1World, par2, par3, par4);
	}
	
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest){
        if(!world.isRemote){
        	if(player != null && player.capabilities.isCreativeMode && !mod_SecurityCraft.configHandler.mineExplodesWhenInCreative){
            	return super.removedByPlayer(world, player, x, y, z, willHarvest);
        	}else{
        		this.explode(world, x, y, z);
            	return super.removedByPlayer(world, player, x, y, z, willHarvest);
        	}
        }
		
		return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }
		
	public void activateMine(World world, int par2, int par3, int par4) {
		world.setBlockMetadataWithNotify(par2, par3, par4, 0, 3);
	}

	public void defuseMine(World world, int par2, int par3, int par4) {
		world.setBlockMetadataWithNotify(par2, par3, par4, 1, 3);
	}

	public void explode(World world, int par2, int par3, int par4) {
		if(world.getBlockMetadata(par2, par3, par4) == 1) return;
		
		world.setBlockToAir(par2, par3, par4);
		EntityBouncingBetty entitytntprimed = new EntityBouncingBetty(world, par2 + 0.5F, par3 + 0.5F, par4 + 0.5F);
		entitytntprimed.fuse = 15;
		entitytntprimed.motionY = 0.50D;
		world.spawnEntityInWorld(entitytntprimed);
		world.playSoundAtEntity(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	public Item getItemDropped(int par1, Random par2Random, int par3){
		return Item.getItemFromBlock(this);
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	public Item getItem(World par1World, int par2, int par3, int par4){
		return Item.getItemFromBlock(this);
	}
	
	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2) {
		return par2 == 0 ? activeIcon : defusedIcon;
	}

	public void registerBlockIcons(IIconRegister par1IconRegister){
		this.activeIcon = par1IconRegister.registerIcon("securitycraft:bouncingBettyActive");
		this.defusedIcon = par1IconRegister.registerIcon("securitycraft:bouncingBettyDefused");
	}
	
	public boolean isActive(World world, int par2, int par3, int par4) {
		return world.getBlockMetadata(par2, par3, par4) == 0;
	}

	public boolean isDefusable() {
		return true;
	}

}
