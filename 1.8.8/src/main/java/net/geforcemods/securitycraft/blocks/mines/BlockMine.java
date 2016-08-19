package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockMine extends BlockExplosive {

	public static final PropertyBool DEACTIVATED = PropertyBool.create("deactivated");

	public BlockMine(Material par1Material) {
		super(par1Material);
		float f = 0.2F;
		float g = 0.1F;
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
	public boolean isNormalCube()
	{
		return false;
	} 

	public int getRenderType(){
		return 3;
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor blockID
	 */
	public void onNeighborBlockChange(World par1World, BlockPos pos, IBlockState state, Block par5){
		if (par1World.getBlockState(pos.down()).getBlock().getMaterial() != Material.air){
			return;  	   
		}else{    	   
			this.explode(par1World, pos);   
		}
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, pos
	 */
	public boolean canPlaceBlockAt(World par1World, BlockPos pos){
		if(BlockUtils.getBlockMaterial(par1World, pos.down()) == Material.glass || BlockUtils.getBlockMaterial(par1World, pos.down()) == Material.cactus || BlockUtils.getBlockMaterial(par1World, pos.down()) == Material.air || BlockUtils.getBlockMaterial(par1World, pos.down()) == Material.cake || BlockUtils.getBlockMaterial(par1World, pos.down()) == Material.plants){
			return false;
		}else{
			return true;
		}
	}

	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest){
        if(!world.isRemote){
        	if(player != null && player.capabilities.isCreativeMode && !mod_SecurityCraft.configHandler.mineExplodesWhenInCreative){
            	return super.removedByPlayer(world, pos, player, willHarvest);
        	}else{
        		this.explode(world, pos);
            	return super.removedByPlayer(world, pos, player, willHarvest);
        	}
        }
		
		return super.removedByPlayer(world, pos, player, willHarvest);
    }

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	public void onEntityCollidedWithBlock(World par1World, BlockPos pos, Entity par5Entity){
		if(par1World.isRemote){
			return;
		}else{
			if(par5Entity instanceof EntityCreeper || par5Entity instanceof EntityOcelot || par5Entity instanceof EntityEnderman || par5Entity instanceof EntityItem){
				return;
			}else{
				this.explode(par1World, pos);
			}  		
		}
	}
	
	public void activateMine(World world, BlockPos pos) {
		if(!world.isRemote){
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, false);
		}
	}

	public void defuseMine(World world, BlockPos pos) {
		if(!world.isRemote){
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
		}
	}

	public void explode(World par1World, BlockPos pos) {
		if(par1World.isRemote){ return; }

		if(!par1World.getBlockState(pos).getValue(DEACTIVATED).booleanValue()){
			par1World.destroyBlock(pos, false);
			if(mod_SecurityCraft.configHandler.smallerMineExplosion){
				par1World.createExplosion((Entity) null, pos.getX(), pos.getY(), pos.getZ(), 1.0F, true);
			}else{
				par1World.createExplosion((Entity) null, pos.getX(), pos.getY(), pos.getZ(), 3.0F, true);
			}
		}
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	public Item getItemDropped(IBlockState state, Random par2Random, int par3){
		return Item.getItemFromBlock(mod_SecurityCraft.mine);
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	public Item getItem(World par1World, BlockPos pos){
		return Item.getItemFromBlock(mod_SecurityCraft.mine);
	}

	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(DEACTIVATED, meta == 1 ? true : false);
	}

	public int getMetaFromState(IBlockState state)
	{
		return (state.getValue(DEACTIVATED).booleanValue() ? 1 : 0);
	}

	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {DEACTIVATED});
	}
	
	public boolean isActive(World world, BlockPos pos) {
		return !world.getBlockState(pos).getValue(DEACTIVATED).booleanValue();
	}
	
	public boolean isDefusable() {
		return true;
	}      

	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}

}
