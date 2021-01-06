package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;

public class KeypadDoorItem extends SpecialDoorItem
{
	public KeypadDoorItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public Block getDoorBlock()
	{
		return SCContent.KEYPAD_DOOR.get();
	}
}
