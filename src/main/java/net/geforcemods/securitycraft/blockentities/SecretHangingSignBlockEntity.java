package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SecretHangingSignBlockEntity extends SecretSignBlockEntity {
	public SecretHangingSignBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public int getTextLineHeight() {
		return 9;
	}

	@Override
	public int getMaxTextLineWidth() {
		return 60;
	}

	@Override
	public BlockEntityType<?> getType() {
		return SCContent.SECRET_HANGING_SIGN_BLOCK_ENTITY.get();
	}
}
