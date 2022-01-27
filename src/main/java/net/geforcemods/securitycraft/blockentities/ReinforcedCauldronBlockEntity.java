package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.entity.player.PlayerEntity;

public class ReinforcedCauldronBlockEntity extends CustomizableBlockEntity {
	private final BooleanOption isPublic = new BooleanOption("isPublic", false);

	public ReinforcedCauldronBlockEntity() {
		super(SCContent.beTypeReinforcedCauldron);
	}

	public boolean isAllowedToInteract(PlayerEntity player) {
		return isPublic.get() || getOwner().isOwner(player) || ModuleUtils.isAllowed(this, player);
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
