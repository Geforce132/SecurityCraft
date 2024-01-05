package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.tileentity.TileEntityType;

public class AllowlistOnlyBlockEntity extends CustomizableBlockEntity {
	public AllowlistOnlyBlockEntity() {
		super(SCContent.ALLOWLIST_ONLY_BLOCK_ENTITY.get());
	}

	public AllowlistOnlyBlockEntity(TileEntityType<?> type) {
		super(type);
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[0];
	}

	@Override
	public String getModuleDescriptionId(String blockName, ModuleType module) {
		if (blockName.contains("pressure"))
			return super.getModuleDescriptionId("generic.reinforced_pressure_plate", module);
		else if (blockName.contains("button"))
			return super.getModuleDescriptionId("generic.reinforced_button", module);
		else if (blockName.contains("fence_gate"))
			return super.getModuleDescriptionId("generic.reinforced_fence_gate", module);
		else
			return super.getModuleDescriptionId(blockName, module);
	}
}