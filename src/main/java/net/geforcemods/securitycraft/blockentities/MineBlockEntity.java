package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.TargetingModeOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.TargetingMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MineBlockEntity extends CustomizableBlockEntity {
	private TargetingModeOption targetingMode = new TargetingModeOption(TargetingMode.PLAYERS_AND_MOBS);

	public MineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public MineBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.OWNABLE_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				targetingMode
		};
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[0];
	}

	public TargetingMode getTargetingMode() {
		return targetingMode.get();
	}
}
