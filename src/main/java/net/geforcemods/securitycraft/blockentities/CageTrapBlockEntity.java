package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CageTrapBlockEntity extends DisguisableBlockEntity {

	private BooleanOption shouldCaptureMobsOption = new BooleanOption("captureMobs", false);

	public CageTrapBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.teTypeCageTrap, pos, state);
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[]{ModuleType.DISGUISE, ModuleType.ALLOWLIST};
	}

	public boolean capturesMobs()
	{
		return shouldCaptureMobsOption.get();
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] { shouldCaptureMobsOption };
	}

}
