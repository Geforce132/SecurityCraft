package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class GlowDisplayCaseBlockEntity extends DisplayCaseBlockEntity {
	public GlowDisplayCaseBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.GLOW_DISPLAY_CASE_BLOCK_ENTITY.get(), pos, state);
	}
}
