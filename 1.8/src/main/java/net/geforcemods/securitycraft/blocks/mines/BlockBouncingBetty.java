package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.entity.EntityTnTCompact;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockBouncingBetty extends BlockExplosive implements IIntersectable {

	public BlockBouncingBetty(Material par2Material) {
		super(par2Material);
		this.setBlockBounds(0.200F, 0.000F, 0.200F, 0.800F, 0.200F, 0.800F);
	}

	public boolean isOpaqueCube(){
		return false;
	}

	public boolean isNormalCube(){
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
	
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(entity instanceof EntityLivingBase){
			this.explode(world, pos);
		}else{
			return;
		}
	}

	public void onBlockClicked(World par1World, BlockPos pos, EntityPlayer par5EntityPlayer){
		if(par5EntityPlayer instanceof EntityLivingBase){
			this.explode(par1World, pos);
		}else{
			return;
		}
	}
	
	public void activateMine(World world, BlockPos pos) {}

	public void defuseMine(World world, BlockPos pos) {}
	
	public void explode(World par1World, BlockPos pos){
		if(par1World.isRemote){ return; }

		par1World.setBlockToAir(pos);
		EntityTnTCompact entitytntprimed = new EntityTnTCompact(par1World, (double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F));
		entitytntprimed.fuse = 15;
		entitytntprimed.motionY = 0.50D;
		par1World.spawnEntityInWorld(entitytntprimed);
		par1World.playSoundAtEntity(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
	}

	public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
		if(par1World.isRemote){
			return true;
		}else{
			if(!PlayerUtils.isHoldingItem(par5EntityPlayer, mod_SecurityCraft.remoteAccessMine)){
				this.explode(par1World, pos);
				return false;
			}else{
				return false;	   		
			}
		}
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	public Item getItemDropped(IBlockState state, Random par2Random, int par3)
	{
		return Item.getItemFromBlock(this);
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	public Item getItem(World par1World, BlockPos pos){
		return Item.getItemFromBlock(this);
	}
	
	public boolean isActive(World world, BlockPos pos) {
		return true;
	}
	
	public boolean isDefusable() {
		return false;
	}
	
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable().intersectsEntities();
	}
	
}
