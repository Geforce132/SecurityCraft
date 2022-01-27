package net.geforcemods.securitycraft.blockentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.ModuleType;

public class AllowlistOnlyBlockEntity extends CustomizableBlockEntity {
	public AllowlistOnlyBlockEntity() {
		super(SCContent.beTypeAllowlistOnly);
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}
}