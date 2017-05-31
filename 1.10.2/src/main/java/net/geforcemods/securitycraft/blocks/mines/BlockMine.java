package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMine extends BlockExplosive {

	public static final PropertyBool DEACTIVATED = PropertyBool.create("deactivated");

	public BlockMine(Material par1Material) {
		super(par1Material);
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube(IBlockState state)
	{
		return false;
	} 

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor){
		if (world.getBlockState(pos.down()).getBlock().getMaterial(world.getBlockState(pos.down())) != Material.AIR){
			return;  	   
		}else{    	   
			this.explode((World)world, pos);   
		}
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, pos
	 */
	@Override
	public boolean canPlaceBlockAt(World par1World, BlockPos pos){
		if(BlockUtils.getBlockMaterial(par1World, pos.down()) == Material.GLASS || BlockUtils.getBlockMaterial(par1World, pos.down()) == Material.CACTUS || BlockUtils.getBlockMaterial(par1World, pos.down()) == Material.AIR || BlockUtils.getBlockMaterial(par1World, pos.down()) == Material.CAKE || BlockUtils.getBlockMaterial(par1World, pos.down()) == Material.PLANTS){
			return false;
		}else{
			return true;
		}
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest){
        if(!player.capabilities.isCreativeMode && !world.isRemote){
        	if(player != null && player.capabilities.isCreativeMode && !mod_SecurityCraft.configHandler.mineExplodesWhenInCreative){
            	return super.removedByPlayer(state, world, pos, player, willHarvest);
        	}else{
        		this.explode(world, pos);
            	return super.removedByPlayer(state, world, pos, player, willHarvest);
        	}
        }
		
		return super.removedByPlayer(state, world, pos, player, willHarvest);
    }
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		float f = 0.2F;
		float g = 0.1F;
		
		return BlockUtils.fromBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, (g * 2.0F) / 2 + 0.1F, 0.5F + f);
	}
	
	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn){
		if(worldIn.isRemote){
			return;
		}else{
			if(entityIn instanceof EntityCreeper || entityIn instanceof EntityOcelot || entityIn instanceof EntityEnderman || entityIn instanceof EntityItem){
				return;
			}else{
				this.explode(worldIn, pos);
			}  		
		}
	}
	
	@Override
	public void activateMine(World world, BlockPos pos) {
		if(!world.isRemote){
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, false);
		}
	}

	@Override
	public void defuseMine(World world, BlockPos pos) {
		if(!world.isRemote){
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
		}
	}

	@Override
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
	@Override
	public Item getItemDropped(IBlockState state, Random par2Random, int par3){
		return Item.getItemFromBlock(mod_SecurityCraft.mine);
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	public ItemStack getItem(World par1World, BlockPos pos, IBlockState state){
		return new ItemStack(Item.getItemFromBlock(mod_SecurityCraft.mine));
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(DEACTIVATED, meta == 1 ? true : false);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return (state.getValue(DEACTIVATED).booleanValue() ? 1 : 0);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {DEACTIVATED});
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
