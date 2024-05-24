package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.ModuleType;

public class ReinforcedFenceGateBlockEntity extends AllowlistOnlyBlockEntity {
	public ReinforcedFenceGateBlockEntity() {
		super(SCContent.REINFORCED_FENCE_GATE_BLOCK_ENTITY.get());
	}

	@Override
	public String getModuleDescriptionId(String denotation, ModuleType module) {
		return super.getModuleDescriptionId("generic.reinforced_fence_gate", module);
	}
}
