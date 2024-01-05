package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedDropperBlockEntity extends ReinforcedDispenserBlockEntity {
	public ReinforcedDropperBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.REINFORCED_DROPPER_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent(SCContent.REINFORCED_DROPPER.get().getDescriptionId());
	}
}
