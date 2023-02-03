package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.entity.player.EntityPlayer;

public class ReinforcedCauldronBlockEntity extends CustomizableBlockEntity {
	private final OptionBoolean isPublic = new OptionBoolean("isPublic", false);

	public boolean isAllowedToInteract(EntityPlayer player) {
		return isPublic.get() || isOwnedBy(player) || isAllowed(player);
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				isPublic
		};
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST
		};
	}
}
