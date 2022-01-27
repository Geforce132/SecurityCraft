package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

public class ReinforcedIronBarsBlockEntity extends OwnableBlockEntity {
	private boolean canDrop = true;

	public ReinforcedIronBarsBlockEntity() {
		super(SCContent.beTypeReinforcedIronBars);
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		tag.putBoolean("canDrop", canDrop);
		return super.save(tag);
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);
		canDrop = tag.getBoolean("canDrop");
	}

	public boolean canDrop() {
		return canDrop;
	}

	public void setCanDrop(boolean canDrop) {
		this.canDrop = canDrop;
	}
}
