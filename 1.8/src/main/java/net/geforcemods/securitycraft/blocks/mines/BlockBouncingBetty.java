package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
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

	public static final PropertyBool DEACTIVATED = PropertyBool.create("deactivated");

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
	
	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	public boolean canPlaceBlockAt(World par1World, BlockPos pos){
		return par1World.isSideSolid(pos.down(), EnumFacing.UP);
	}
	
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(entity instanceof EntityLivingBase){
			if(!PlayerUtils.isPlayerMountedOnCamera((EntityLivingBase)entity))
				this.explode(world, pos);
		}
	}

	public void onBlockClicked(World par1World, BlockPos pos, EntityPlayer par5EntityPlayer){
		this.explode(par1World, pos);
	}
	
	public void activateMine(World world, BlockPos pos) {
		BlockUtils.setBlockProperty(world, pos, DEACTIVATED, false);
	}

	public void defuseMine(World world, BlockPos pos) {
		BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
	}
	
	public void explode(World par1World, BlockPos pos){
		if(par1World.isRemote){ return; }
		if(BlockUtils.getBlockPropertyAsBoolean(par1World, pos, DEACTIVATED))
			return;
		
		par1World.setBlockToAir(pos);
		EntityBouncingBetty entitytntprimed = new EntityBouncingBetty(par1World, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
		entitytntprimed.fuse = 15;
		entitytntprimed.motionY = 0.50D;
		par1World.spawnEntityInWorld(entitytntprimed);
		par1World.playSoundAtEntity(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
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
	
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(DEACTIVATED, meta == 1 ? true : false);
	}

	public int getMetaFromState(IBlockState state)
	{
		return (((Boolean) state.getValue(DEACTIVATED)).booleanValue() ? 1 : 0);
	}

	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {DEACTIVATED});
	}
	
	public boolean isActive(World world, BlockPos pos) {
		return !((Boolean) world.getBlockState(pos).getValue(DEACTIVATED)).booleanValue();
	}
	
	public boolean isDefusable() {
		return true;
	}
	
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable().intersectsEntities();
	}
	
}
