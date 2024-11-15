package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PanicButtonBlockEntity extends CustomizableBlockEntity {
	public PanicButtonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public PanicButtonBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.OWNABLE_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[0];
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST
		};
	}
}
