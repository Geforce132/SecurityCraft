package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ElectrifiedFenceAndGateBlockEntity extends AllowlistOnlyBlockEntity {
	public ElectrifiedFenceAndGateBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.ABSTRACT_BLOCK_ENTITY.get(), pos, state);
	}

	public ElectrifiedFenceAndGateBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public String getModuleDescriptionId(String denotation, ModuleType module) {
		return "module.generic.electrified_fence_and_gate.whitelist_module.description";
	}
}
