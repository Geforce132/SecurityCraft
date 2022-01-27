package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.minecraft.nbt.CompoundNBT;

public class ReinforcedIronBarsBlockEntity extends OwnableTileEntity {
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
	public void load(CompoundNBT tag) {
		super.load(tag);
		canDrop = tag.getBoolean("canDrop");
	}

	public boolean canDrop() {
		return canDrop;
	}

	public void setCanDrop(boolean canDrop) {
		this.canDrop = canDrop;
	}
}
