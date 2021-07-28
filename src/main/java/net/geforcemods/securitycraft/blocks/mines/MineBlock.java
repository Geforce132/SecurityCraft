package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MineBlock extends ExplosiveBlock {

	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");
	private static final VoxelShape SHAPE = Block.box(5, 0, 5, 11, 3, 11);

	public MineBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(DEACTIVATED, false));
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag){
		if (!world.getBlockState(pos.below()).isAir())
			return;
		else if (world.getBlockState(pos).getValue(DEACTIVATED))
			world.destroyBlock(pos, true);
		else
			explode(world, pos);
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, pos
	 */
	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos){
		return BlockUtils.isSideSolid(world, pos.below(), Direction.UP);
	}

	@Override
	public boolean removedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid){
		if(!world.isClientSide)
			if(player != null && player.isCreative() && !ConfigHandler.SERVER.mineExplodesWhenInCreative.get())
				return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
			else if(!EntityUtils.doesPlayerOwn(player, world, pos)){
				explode(world, pos);
				return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
			}

		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter source, BlockPos pos, CollisionContext ctx)
	{
		return SHAPE;
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity){
		if(world.isClientSide)
			return;
		else if(entity instanceof ItemEntity)
			return;
		else if(entity instanceof LivingEntity lEntity && !PlayerUtils.isPlayerMountedOnCamera(lEntity) && !EntityUtils.doesEntityOwn(entity, world, pos))
			explode(world, pos);
	}

	@Override
	public boolean activateMine(Level world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if(state.getValue(DEACTIVATED))
		{
			world.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, false));
			return true;
		}

		return false;
	}

	@Override
	public boolean defuseMine(Level world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if(!state.getValue(DEACTIVATED))
		{
			world.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, true));
			return true;
		}

		return false;
	}

	@Override
	public void explode(Level world, BlockPos pos) {
		if(!world.isClientSide && !world.getBlockState(pos).getValue(DEACTIVATED)){
			world.destroyBlock(pos, false);
			world.explode((Entity) null, pos.getX(), pos.getY(), pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 1.0F : 3.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
		}
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state){
		return new ItemStack(SCContent.MINE.get().asItem());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(DEACTIVATED);
	}

	@Override
	public boolean isActive(Level world, BlockPos pos) {
		return !world.getBlockState(pos).getValue(DEACTIVATED);
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OwnableTileEntity(pos, state);
	}

}
