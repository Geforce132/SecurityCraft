package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.util.text.ITextComponent;

public class ReinforcedDropperBlockEntity extends ReinforcedDispenserBlockEntity {
	@Override
	public String getGuiID() {
		return "minecraft:dropper";
	}

	@Override
	public ITextComponent getDefaultName() {
		return Utils.localize(SCContent.reinforcedDropper);
	}
}
