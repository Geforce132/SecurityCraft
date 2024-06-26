package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedObserverBlockEntity extends DisguisableBlockEntity {
	public ReinforcedObserverBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.OWNABLE_BLOCK_ENTITY.get(), pos, state);
	}

	public ReinforcedObserverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public boolean shouldRender() {
		return true;
	}
}
