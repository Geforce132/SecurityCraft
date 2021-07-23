package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedCauldronTileEntity extends CustomizableTileEntity {

	private final BooleanOption isPublic = new BooleanOption("isPublic", false);

	public ReinforcedCauldronTileEntity(BlockPos pos, BlockState state) {
		super(SCContent.teTypeReinforcedCauldron, pos, state);
	}

	public boolean isAllowedToInteract(Player player) {
		return isPublic.get() || getOwner().isOwner(player) || ModuleUtils.isAllowed(this, player);
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{isPublic};
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.ALLOWLIST};
	}
}
