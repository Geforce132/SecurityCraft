package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.blockentities.ElectrifiedFenceAndGateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ElectrifiedIronFenceGateBlock extends OwnableFenceGateBlock {
	public ElectrifiedIronFenceGateBlock(BlockBehaviour.Properties properties) {
		super(properties, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_DOOR_CLOSE);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		return InteractionResult.FAIL;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean stillInside) {
		if (state.getValue(OPEN))
			return;

		ElectrifiedIronFenceBlock.hurtOrConvertEntity(this::getShape, state, level, pos, entity);
	}

	@Override
	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
		BlockEntity be = level.getBlockEntity(pos);

		return be != null && be.triggerEvent(id, param);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ElectrifiedFenceAndGateBlockEntity(pos, state);
	}
}
