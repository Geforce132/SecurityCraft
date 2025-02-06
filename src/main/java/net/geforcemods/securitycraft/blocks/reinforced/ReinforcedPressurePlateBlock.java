package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.AllowlistOnlyBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedPressurePlateBlock extends PressurePlateBlock implements IReinforcedBlock, EntityBlock {
	private final Block vanillaBlock;

	public ReinforcedPressurePlateBlock(Sensitivity sensitivity, BlockBehaviour.Properties properties, Block vanillaBlock) {
		super(sensitivity, properties);
		this.vanillaBlock = vanillaBlock;
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, state, player, level, pos);
	}

	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
		return ConfigHandler.SERVER.alwaysDrop.get() || super.canHarvestBlock(state, level, pos, player);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		int redstoneStrength = getSignalForState(state);

		if (!level.isClientSide && redstoneStrength == 0 && entity instanceof Player player && level.getBlockEntity(pos) instanceof AllowlistOnlyBlockEntity be && isAllowedToPress(be, player))
			checkPressed(player, level, pos, state, redstoneStrength);
	}

	@Override
	protected int getSignalStrength(Level level, BlockPos pos) {
		AABB aabb = TOUCH_AABB.move(pos);
		List<? extends Entity> list = level.getEntities(null, aabb);

		if (!list.isEmpty() && level.getBlockEntity(pos) instanceof AllowlistOnlyBlockEntity be) {
			for (Entity entity : list) {
				if (entity instanceof Player player && isAllowedToPress(be, player))
					return 15;
			}
		}

		return 0;
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative() && level.getBlockEntity(pos) instanceof IModuleInventory inv)
			inv.getInventory().clear();

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (!ConfigHandler.SERVER.vanillaToolBlockBreaking.get() && level.getBlockEntity(pos) instanceof IModuleInventory inv)
				inv.dropAllModules();

			if (!isMoving && getSignalForState(state) > 0) {
				level.updateNeighborsAt(pos, this);
				level.updateNeighborsAt(pos.below(), this);
			}
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	public boolean isAllowedToPress(AllowlistOnlyBlockEntity be, Player entity) {
		return be.isOwnedBy(entity) || be.isAllowed(entity);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK; //Can't be PushReaction.NORMAL because pressure plates rely on scheduled ticks which don't support moving the block
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlock;
	}

	@Override
	public BlockState convertToReinforced(Level level, BlockPos pos, BlockState vanillaState) {
		return defaultBlockState();
	}

	@Override
	public BlockState convertToVanilla(Level level, BlockPos pos, BlockState reinforcedState) {
		return getVanillaBlock().defaultBlockState();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AllowlistOnlyBlockEntity(pos, state);
	}

	public static class DoorActivator implements IDoorActivator {
		//@formatter:off
		private final List<Block> blocks = Arrays.asList(
				SCContent.REINFORCED_STONE_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_OAK_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_SPRUCE_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_BIRCH_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_JUNGLE_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_ACACIA_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_DARK_OAK_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_CRIMSON_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_WARPED_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE.get());
		//@formatter:on

		@Override
		public boolean isPowering(Level level, BlockPos pos, BlockState state, BlockEntity be, Direction direction, int distance) {
			return state.getValue(POWERED) && (distance < 2 || direction == Direction.UP);
		}

		@Override
		public List<Block> getBlocks() {
			return blocks;
		}
	}
}
