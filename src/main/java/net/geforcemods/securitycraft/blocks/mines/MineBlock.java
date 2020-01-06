package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.OwnableTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class MineBlock extends ExplosiveBlock {

	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");
	private static final VoxelShape SHAPE = Block.makeCuboidShape(5, 0, 5, 11, 3, 11);

	public MineBlock(Material material, float baseHardness) {
		super(SoundType.STONE, material, baseHardness);
		setDefaultState(stateContainer.getBaseState().with(DEACTIVATED, false));
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag){
		if (world.getBlockState(pos.down()).getMaterial() != Material.AIR)
			return;
		else
			explode(world, pos);
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, pos
	 */
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos){
		if(BlockUtils.getBlockMaterial(world, pos.down()) == Material.GLASS || BlockUtils.getBlockMaterial(world, pos.down()) == Material.CACTUS || BlockUtils.getBlockMaterial(world, pos.down()) == Material.AIR || BlockUtils.getBlockMaterial(world, pos.down()) == Material.CAKE || BlockUtils.getBlockMaterial(world, pos.down()) == Material.PLANTS)
			return false;
		else
			return true;
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid){
		if(!world.isRemote)
			if(player != null && player.isCreative() && !ConfigHandler.CONFIG.mineExplodesWhenInCreative.get())
				return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
			else{
				explode(world, pos);
				return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
			}

		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
	{
		return SHAPE;
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity){
		if(world.isRemote)
			return;
		else if(entity instanceof CreeperEntity || entity instanceof OcelotEntity || entity instanceof EndermanEntity || entity instanceof ItemEntity)
			return;
		else if(entity instanceof LivingEntity && !PlayerUtils.isPlayerMountedOnCamera((LivingEntity)entity))
			explode(world, pos);
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
	public void explode(World world, BlockPos pos) {
		if(world.isRemote)
			return;

		if(!world.getBlockState(pos).get(DEACTIVATED).booleanValue()){
			world.destroyBlock(pos, false);
			if(ConfigHandler.CONFIG.smallerMineExplosion.get())
				world.createExplosion((Entity) null, pos.getX(), pos.getY(), pos.getZ(), 1.0F, ConfigHandler.CONFIG.shouldSpawnFire.get(), Mode.BREAK);
			else
				world.createExplosion((Entity) null, pos.getX(), pos.getY(), pos.getZ(), 3.0F, ConfigHandler.CONFIG.shouldSpawnFire.get(), Mode.BREAK);
		}
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state){
		return new ItemStack(SCContent.mine.asItem());
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(DEACTIVATED);
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return !world.getBlockState(pos).get(DEACTIVATED).booleanValue();
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new OwnableTileEntity();
	}

}
