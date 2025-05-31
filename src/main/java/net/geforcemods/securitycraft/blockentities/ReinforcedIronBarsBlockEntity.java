package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ReinforcedIronBarsBlockEntity extends OwnableBlockEntity {
	private boolean canDrop = true;

	public ReinforcedIronBarsBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.REINFORCED_IRON_BARS_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void saveAdditional(ValueOutput tag) {
		tag.putBoolean("canDrop", canDrop);
		super.saveAdditional(tag);
	}

	@Override
	public void loadAdditional(ValueInput tag) {
		super.loadAdditional(tag);
		canDrop = tag.getBooleanOr("canDrop", true);
	}

	public boolean canDrop() {
		return canDrop;
	}

	public void setCanDrop(boolean canDrop) {
		this.canDrop = canDrop;
		setChanged();
	}
}
