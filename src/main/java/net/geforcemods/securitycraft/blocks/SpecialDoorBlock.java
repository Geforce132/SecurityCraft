package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.misc.SaltData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;

public abstract class SpecialDoorBlock extends DoorBlock implements EntityBlock {
	protected SpecialDoorBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	//redstone signals should not be able to open these doors
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);

		if (level.getBlockEntity(pos) instanceof IOwnable lowerBe && level.getBlockEntity(pos.above()) instanceof IOwnable upperBe) {
			if (placer instanceof Player player) {
				lowerBe.setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
				upperBe.setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
			}

			if (lowerBe instanceof LinkableBlockEntity linkable1 && upperBe instanceof LinkableBlockEntity linkable2)
				LinkableBlockEntity.link(linkable1, linkable2);

			if (stack.hasCustomHoverName() && lowerBe instanceof INameSetter nameSetter1 && upperBe instanceof INameSetter nameSetter2) {
				nameSetter1.setCustomName(stack.getHoverName());
				nameSetter2.setCustomName(stack.getHoverName());
			}
		}
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
		level.gameEvent(null, GameEvent.BLOCK_CLOSE, pos);
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative() && level.getBlockEntity(pos) instanceof IModuleInventory inv)
			inv.getInventory().clear();

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState state = super.getStateForPlacement(ctx);

		return state == null ? null : state.setValue(OPEN, false).setValue(POWERED, false);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity be = level.getBlockEntity(pos);

			if (be instanceof IModuleInventory inv && state.getValue(HALF) == DoubleBlockHalf.LOWER)
				inv.dropAllModules();

			if (be instanceof IPasscodeProtected passcodeProtected)
				SaltData.removeSalt(passcodeProtected.getSaltKey());
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
		BlockEntity be = level.getBlockEntity(pos);

		return be != null && be.triggerEvent(id, param);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		return new ItemStack(getDoorItem());
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}

	public abstract Item getDoorItem();
}
