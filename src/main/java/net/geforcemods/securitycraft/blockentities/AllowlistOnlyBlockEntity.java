package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AllowlistOnlyBlockEntity extends CustomizableBlockEntity
{
	public AllowlistOnlyBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.beTypeAllowlistOnly, pos, state);
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[] {ModuleType.ALLOWLIST};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}
}