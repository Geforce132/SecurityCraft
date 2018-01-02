package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
		setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, (g * 2.0F) / 2 + 0.1F, 0.5F + f);
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube()
	{
		return false;
	}

	@Override
	public int getRenderType(){
		return 3;
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World par1World, BlockPos pos, IBlockState state, Block par5){
		if (par1World.getBlockState(pos.down()).getBlock().getMaterial() != Material.air)
			return;
		else
			explode(par1World, pos);
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, pos
	 */
	@Override
	public boolean canPlaceBlockAt(World par1World, BlockPos pos){
		if(BlockUtils.getBlockMaterial(par1World, pos.down()) == Material.glass || BlockUtils.getBlockMaterial(par1World, pos.down()) == Material.cactus || BlockUtils.getBlockMaterial(par1World, pos.down()) == Material.air || BlockUtils.getBlockMaterial(par1World, pos.down()) == Material.cake || BlockUtils.getBlockMaterial(par1World, pos.down()) == Material.plants)
			return false;
		else
			return true;
	}

	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest){
		if(!world.isRemote)
			if(player != null && player.capabilities.isCreativeMode && !SecurityCraft.config.mineExplodesWhenInCreative)
				return super.removedByPlayer(world, pos, player, willHarvest);
			else{
				explode(world, pos);
				return super.removedByPlayer(world, pos, player, willHarvest);
			}

		return super.removedByPlayer(world, pos, player, willHarvest);
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityCollidedWithBlock(World par1World, BlockPos pos, Entity par5Entity){
		if(par1World.isRemote)
			return;
		else if(par5Entity instanceof EntityCreeper || par5Entity instanceof EntityOcelot || par5Entity instanceof EntityEnderman || par5Entity instanceof EntityItem)
			return;
		else if(par5Entity instanceof EntityLivingBase && !PlayerUtils.isPlayerMountedOnCamera((EntityLivingBase)par5Entity))
			explode(par1World, pos);
	}

	@Override
	public void activateMine(World world, BlockPos pos) {
		if(!world.isRemote)
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, false);
	}

	@Override
	public void defuseMine(World world, BlockPos pos) {
		if(!world.isRemote)
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
	}

	@Override
	public void explode(World par1World, BlockPos pos) {
		if(par1World.isRemote)
			return;

		if(!par1World.getBlockState(pos).getValue(DEACTIVATED).booleanValue()){
			par1World.destroyBlock(pos, false);
			if(SecurityCraft.config.smallerMineExplosion)
				par1World.createExplosion((Entity) null, pos.getX(), pos.getY(), pos.getZ(), 1.0F, true);
			else
				par1World.createExplosion((Entity) null, pos.getX(), pos.getY(), pos.getZ(), 3.0F, true);
		}
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	@Override
	public Item getItemDropped(IBlockState state, Random par2Random, int par3){
		return Item.getItemFromBlock(SCContent.mine);
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	public Item getItem(World par1World, BlockPos pos){
		return Item.getItemFromBlock(SCContent.mine);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(DEACTIVATED, meta == 1 ? true : false);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return (state.getValue(DEACTIVATED).booleanValue() ? 1 : 0);
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {DEACTIVATED});
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return !world.getBlockState(pos).getValue(DEACTIVATED).booleanValue();
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}

}
