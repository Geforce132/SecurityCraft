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

	public BlockBouncingBetty(Material material) {
		super(material);
		setBlockBounds(0.200F, 0.000F, 0.200F, 0.800F, 0.200F, 0.800F);
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean isNormalCube(){
		return false;
	}

	@Override
	public int getRenderType(){
		return 3;
	}

	/**
	 * Sets the block's bounds for rendering it as an item
	 */
	@Override
	public void setBlockBoundsForItemRender()
	{
		setBlockBounds(0.200F, 0.000F, 0.200F, 0.800F, 0.200F, 0.800F);
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos){
		return world.isSideSolid(pos.down(), EnumFacing.UP);
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(entity instanceof EntityLivingBase)
			if(!PlayerUtils.isPlayerMountedOnCamera((EntityLivingBase)entity))
				explode(world, pos);
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player){
		explode(world, pos);
	}

	@Override
	public void activateMine(World world, BlockPos pos) {
		BlockUtils.setBlockProperty(world, pos, DEACTIVATED, false);
	}

	@Override
	public void defuseMine(World world, BlockPos pos) {
		BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
	}

	@Override
	public void explode(World world, BlockPos pos){
		if(world.isRemote)
			return;
		if(BlockUtils.getBlockPropertyAsBoolean(world, pos, DEACTIVATED))
			return;

		world.setBlockToAir(pos);
		EntityBouncingBetty bouncingBetty = new EntityBouncingBetty(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
		bouncingBetty.fuse = 15;
		bouncingBetty.motionY = 0.50D;
		world.spawnEntityInWorld(bouncingBetty);
		world.playSoundAtEntity(bouncingBetty, "game.tnt.primed", 1.0F, 1.0F);
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	@Override
	public Item getItemDropped(IBlockState state, Random random, int par3)
	{
		return Item.getItemFromBlock(this);
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	public Item getItem(World world, BlockPos pos){
		return Item.getItemFromBlock(this);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(DEACTIVATED, meta == 1 ? true : false);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return (((Boolean) state.getValue(DEACTIVATED)).booleanValue() ? 1 : 0);
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {DEACTIVATED});
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return !((Boolean) world.getBlockState(pos).getValue(DEACTIVATED)).booleanValue();
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable().intersectsEntities();
	}

}
