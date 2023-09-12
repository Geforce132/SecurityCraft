package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BouncingBettyBlock extends ExplosiveBlock {
	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");
	private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 3, 13);

	public BouncingBettyBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(DEACTIVATED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx) {
		return SHAPE;
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		if (world.getBlockState(pos.below()).isAir()) {
			if (world.getBlockState(pos).getValue(DEACTIVATED))
				world.destroyBlock(pos, true);
			else
				explode(world, pos);
		}
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		return BlockUtils.isSideSolid(world, pos.below(), Direction.UP);
	}

	@Override
	public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
		if (getShape(state, world, pos, ISelectionContext.of(entity)).bounds().move(pos).inflate(0.01D).intersects(entity.getBoundingBox()) && !EntityUtils.doesEntityOwn(entity, world, pos) && !(entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative()) && !((IOwnable) world.getBlockEntity(pos)).allowsOwnableEntity(entity))
			explode(world, pos);
	}

	@Override
	public void attack(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		if (!player.isCreative() && !EntityUtils.doesPlayerOwn(player, world, pos))
			explode(world, pos);
	}

	@Override
	public boolean activateMine(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if (state.getValue(DEACTIVATED)) {
			world.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, false));
			return true;
		}

		return false;
	}

	@Override
	public boolean defuseMine(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if (!state.getValue(DEACTIVATED)) {
			world.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, true));
			return true;
		}

		return false;
	}

	@Override
	public void explode(World world, BlockPos pos) {
		if (world.isClientSide)
			return;

		if (world.getBlockState(pos).getValue(DEACTIVATED))
			return;

		BouncingBetty bouncingBetty = new BouncingBetty(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);

		world.destroyBlock(pos, false);
		bouncingBetty.setFuse(15);
		bouncingBetty.setDeltaMovement(bouncingBetty.getDeltaMovement().multiply(1, 0, 1).add(0, 0.5D, 0));
		LevelUtils.addScheduledTask(world, () -> world.addFreshEntity(bouncingBetty));
		bouncingBetty.playSound(SoundEvents.TNT_PRIMED, 1.0F, 1.0F);
	}

	@Override
	public ItemStack getCloneItemStack(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return new ItemStack(asItem());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(DEACTIVATED);
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return !world.getBlockState(pos).getValue(DEACTIVATED);
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new OwnableBlockEntity(SCContent.ABSTRACT_BLOCK_ENTITY.get());
	}
}
