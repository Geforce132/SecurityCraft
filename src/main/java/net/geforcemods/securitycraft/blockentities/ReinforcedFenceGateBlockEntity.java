package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedFenceGateBlockEntity extends AllowlistOnlyBlockEntity {
	public ReinforcedFenceGateBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.REINFORCED_FENCE_GATE_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public String getModuleDescriptionId(String blockName, ModuleType module) {
		return super.getModuleDescriptionId("generic.reinforced_fence_gate", module);
	}
}
