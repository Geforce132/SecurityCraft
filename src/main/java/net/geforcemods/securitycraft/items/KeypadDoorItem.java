package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.item.Item.Properties;

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
