package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.AllowlistOnlyBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedPressurePlateBlock extends PressurePlateBlock implements IReinforcedBlock, EntityBlock {
	private final Block vanillaBlock;

	public ReinforcedPressurePlateBlock(Sensitivity sensitivity, Block.Properties properties, Block vanillaBlock, BlockSetType blockSetType) {
		super(sensitivity, properties, blockSetType);
		this.vanillaBlock = vanillaBlock;
		DoorActivator.addActivator(this);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		int redstoneStrength = getSignalForState(state);

		if (!level.isClientSide && redstoneStrength == 0 && entity instanceof Player player) {
			if (level.getBlockEntity(pos) instanceof AllowlistOnlyBlockEntity be) {
				if (isAllowedToPress(level, pos, be, player))
					checkPressed(player, level, pos, state, redstoneStrength);
			}
		}
	}

	@Override
	protected int getSignalStrength(Level level, BlockPos pos) {
		AABB aabb = TOUCH_AABB.move(pos);
		List<? extends Entity> list = level.getEntities(null, aabb);

		if (!list.isEmpty()) {
			if (level.getBlockEntity(pos) instanceof AllowlistOnlyBlockEntity be) {
				for (Entity entity : list) {
					if (entity instanceof Player player && isAllowedToPress(level, pos, be, player))
						return 15;
				}
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
			if (level.getBlockEntity(pos) instanceof IModuleInventory inv)
				inv.dropAllModules();

			if (!isMoving && getSignalForState(state) > 0) {
				level.updateNeighborsAt(pos, this);
				level.updateNeighborsAt(pos.below(), this);
			}

			if (!newState.hasBlockEntity())
				level.removeBlockEntity(pos);
		}
	}

	public boolean isAllowedToPress(Level level, BlockPos pos, AllowlistOnlyBlockEntity be, Player entity) {
		return be.isOwnedBy(entity) || be.isAllowed(entity);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (Player) placer));
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlock;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState) {
		return defaultBlockState();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AllowlistOnlyBlockEntity(pos, state);
	}

	public static class DoorActivator implements IDoorActivator {
		private static final List<Block> BLOCKS = new ArrayList<>();

		public static boolean addActivator(Block block) {
			if (BLOCKS.contains(block))
				return false;
			else
				return BLOCKS.add(block);
		}

		@Override
		public boolean isPowering(Level level, BlockPos pos, BlockState state, BlockEntity be, Direction direction, int distance) {
			return state.getValue(POWERED) && (distance < 2 || direction == Direction.UP);
		}

		@Override
		public List<Block> getBlocks() {
			return BLOCKS;
		}
	}
}
