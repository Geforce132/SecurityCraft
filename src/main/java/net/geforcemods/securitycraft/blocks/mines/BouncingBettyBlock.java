package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

public class BouncingBettyBlock extends ExplosiveBlock {
	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");
	private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 3, 13);

	public BouncingBettyBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(DEACTIVATED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return SHAPE;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		if (!level.getBlockState(pos.below()).isAir())
			return;
		else if (level.getBlockState(pos).getValue(DEACTIVATED))
			level.destroyBlock(pos, true);
		else
			explode(level, pos);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return BlockUtils.isSideSolid(level, pos.below(), Direction.UP);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!getShape(state, level, pos, CollisionContext.of(entity)).bounds().move(pos).inflate(0.01D).intersects(entity.getBoundingBox()))
			return;
		else if (!EntityUtils.doesEntityOwn(entity, level, pos) && !(entity instanceof Player player && player.isCreative()))
			explode(level, pos);
	}

	@Override
	public void attack(BlockState state, Level level, BlockPos pos, Player player) {
		if (!player.isCreative() && !EntityUtils.doesPlayerOwn(player, level, pos))
			explode(level, pos);
	}

	@Override
	public boolean activateMine(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (state.getValue(DEACTIVATED)) {
			level.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, false));
			level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
			return true;
		}

		return false;
	}

	@Override
	public boolean defuseMine(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (!state.getValue(DEACTIVATED)) {
			level.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, true));
			level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
			return true;
		}

		return false;
	}

	@Override
	public void explode(Level level, BlockPos pos) {
		if (level.isClientSide || level.getBlockState(pos).getValue(DEACTIVATED))
			return;

		BouncingBetty bouncingBettyEntity = new BouncingBetty(level, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);

		level.destroyBlock(pos, false);
		bouncingBettyEntity.fuse = 15;
		bouncingBettyEntity.setDeltaMovement(bouncingBettyEntity.getDeltaMovement().multiply(1, 0, 1).add(0, 0.5D, 0));
		LevelUtils.addScheduledTask(level, () -> level.addFreshEntity(bouncingBettyEntity));
		bouncingBettyEntity.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.tnt.primed")), 1.0F, 1.0F);
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		return new ItemStack(asItem());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(DEACTIVATED);
	}

	@Override
	public boolean isActive(Level level, BlockPos pos) {
		return !level.getBlockState(pos).getValue(DEACTIVATED);
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OwnableBlockEntity(SCContent.ABSTRACT_BLOCK_ENTITY.get(), pos, state);
	}
}
