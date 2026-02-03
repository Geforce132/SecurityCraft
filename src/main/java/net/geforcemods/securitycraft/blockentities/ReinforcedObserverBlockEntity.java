package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedObserverBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedObserverBlockEntity extends DisguisableBlockEntity {
	public ReinforcedObserverBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.OBSERVER_BLOCK_ENTITY.get(), pos, state);
	}

	public ReinforcedObserverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player, Owner oldOwner, Owner newOwner) {
		level.setBlockAndUpdate(pos, state.setValue(ReinforcedObserverBlock.POWERED, false));
		super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);
	}

	@Override
	public boolean needsValidation() {
		return true;
	}
}
