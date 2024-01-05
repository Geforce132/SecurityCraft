package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ReinforcedDropperBlockEntity extends ReinforcedDispenserBlockEntity {
	public ReinforcedDropperBlockEntity() {
		super(SCContent.REINFORCED_DROPPER_BLOCK_ENTITY.get());
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("block.securitycraft.reinforced_dropper");
	}
}
