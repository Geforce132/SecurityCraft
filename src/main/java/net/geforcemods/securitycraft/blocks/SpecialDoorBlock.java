package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;

public abstract class SpecialDoorBlock extends DoorBlock implements EntityBlock {
	public SpecialDoorBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean flag) {
		Block neighborBlock = level.getBlockState(neighbor).getBlock();

		if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
			BlockPos posBelow = pos.below();
			BlockState stateBelow = level.getBlockState(posBelow);

			if (stateBelow.getBlock() != this)
				level.destroyBlock(pos, false);
			else if (neighborBlock != this)
				neighborChanged(stateBelow, level, posBelow, block, pos, flag);
		}
		else {
			boolean drop = false;
			BlockPos blockBelow = pos.above();
			BlockState stateBelow = level.getBlockState(blockBelow);

			if (stateBelow.getBlock() != this) {
				level.destroyBlock(pos, false);
				drop = true;
			}

			if (!level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
				level.destroyBlock(pos, false);
				drop = true;

				if (stateBelow.getBlock() == this)
					level.destroyBlock(blockBelow, false);
			}

			if (drop) {
				if (!level.isClientSide) {
					level.destroyBlock(pos, false);
					Block.popResource(level, pos, new ItemStack(getDoorItem()));
				}
			}
		}
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));

		super.setPlacedBy(level, pos, state, placer, stack);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random rand) {
		BlockState upperState = level.getBlockState(pos);

		if (!upperState.getValue(DoorBlock.OPEN))
			return;

		BlockState lowerState;

		if (upperState.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
			lowerState = upperState;
			pos = pos.above();
			upperState = level.getBlockState(pos);
		}
		else
			lowerState = level.getBlockState(pos.below());

		level.setBlock(pos, upperState.setValue(DoorBlock.OPEN, false), 3);
		level.setBlock(pos.below(), lowerState.setValue(DoorBlock.OPEN, false), 3);
		level.levelEvent(null, LevelEvent.SOUND_CLOSE_IRON_DOOR, pos, 0);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		super.onRemove(state, level, pos, newState, isMoving);

		if (state.getBlock() != newState.getBlock())
			level.removeBlockEntity(pos);
	}

	@Override
	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
		super.triggerEvent(state, level, pos, id, param);

		BlockEntity blockEntity = level.getBlockEntity(pos);

		return blockEntity == null ? false : blockEntity.triggerEvent(id, param);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		return new ItemStack(getDoorItem());
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}

	public abstract Item getDoorItem();
}
