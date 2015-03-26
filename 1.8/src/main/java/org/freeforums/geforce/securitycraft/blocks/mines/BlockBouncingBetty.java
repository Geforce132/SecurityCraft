package org.freeforums.geforce.securitycraft.blocks.mines;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.entity.EntityTnTCompact;
import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.items.ItemRemoteAccess;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityMineLoc;

public class BlockBouncingBetty extends Block implements IHelpInfo{

	public BlockBouncingBetty(Material par2Material) {
		super(par2Material);
		this.setBlockBounds(0.200F, 0.000F, 0.200F, 0.800F, 0.200F, 0.800F);
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
	public boolean isNormalCube()
	{
		return false;
	} 

	public int getRenderType(){
		return 3;
	}

	/**
	 * Sets the block's bounds for rendering it as an item
	 */
	public void setBlockBoundsForItemRender()
	{
		this.setBlockBounds(0.200F, 0.000F, 0.200F, 0.800F, 0.200F, 0.800F);
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	public void onEntityCollidedWithBlock(World par1World, BlockPos pos, Entity par5Entity)
	{
		if(par5Entity instanceof EntityLivingBase){
			this.explode(par1World, pos, (EntityLivingBase) par5Entity);
		}else{
			return;
		}

	}

	/**
	 * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
	 */
	public void onBlockClicked(World par1World, BlockPos pos, EntityPlayer par5EntityPlayer)
	{
		if(par5EntityPlayer instanceof EntityLivingBase){
			this.explode(par1World, pos, par5EntityPlayer);
		}else{
			return;
		}
	}

	public void explode(World par1World, BlockPos pos, EntityLivingBase par5EntityPlayer){
		if(par1World.isRemote){ return; }

		par1World.setBlockToAir(pos);
		EntityTnTCompact entitytntprimed = new EntityTnTCompact(par1World, (double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), (EntityLivingBase) par5EntityPlayer);
		entitytntprimed.fuse = 15;
		entitytntprimed.motionY = 0.50D;
		par1World.spawnEntityInWorld(entitytntprimed);
		par1World.playSoundAtEntity(entitytntprimed, "random.fuse", 1.0F, 1.0F);
	}

	public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
		if(par1World.isRemote){		 
			return true;
		}else{	   
			if(par5EntityPlayer.getCurrentEquippedItem() == null || par5EntityPlayer.getCurrentEquippedItem().getItem() != mod_SecurityCraft.remoteAccessMine){
				this.explode(par1World, pos, par5EntityPlayer);
				return false;
			}else{
				this.setPosition((ItemRemoteAccess) par5EntityPlayer.getCurrentEquippedItem().getItem(), pos, (TileEntityMineLoc) par1World.getTileEntity(pos));
				return false;
			}
		}
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	public Item getItemDropped(IBlockState state, Random par2Random, int par3)
	{
		return HelpfulMethods.getItemFromBlock(this);
	}


	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	public Item getItem(World par1World, BlockPos pos){
		return HelpfulMethods.getItemFromBlock(this);
	}

	private void setPosition(ItemRemoteAccess par1Item, BlockPos pos, TileEntityMineLoc TEML) {	   
		for(int x = 1; x <= par1Item.tEList.length; x++){
			if(par1Item.tEList[x - 1] != null && par1Item.tEList[x - 1].getPos() == pos){
				break;
			}else if(par1Item.tEList[x - 1] == null){
				par1Item.tEList[x - 1] = TEML;
				break;
			}
		}

	}

	public String getHelpInfo() {
		return "The bouncing betty will launch up into the air and explode when touched.";
	}

	public String[] getRecipe() {
		return new String[]{"The bouncing betty requires: 2 iron ingots, 1 gunpowder, 1 weighted pressure plate (heavy)", " X ", "YZY", "   ", "X = weighted pressure plate (heavy), Y = iron ingot, Z = gunpowder"};
	}

}
