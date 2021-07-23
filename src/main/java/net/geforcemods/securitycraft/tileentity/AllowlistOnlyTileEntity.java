package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AllowlistOnlyTileEntity extends CustomizableTileEntity
{
	public AllowlistOnlyTileEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.teTypeAllowlistOnly, pos, state);
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