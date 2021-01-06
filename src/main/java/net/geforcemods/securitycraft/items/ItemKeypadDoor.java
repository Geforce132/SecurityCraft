package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;

public class ItemKeypadDoor extends ItemSpecialDoor
{
	@Override
	public Block getDoorBlock()
	{
		return SCContent.keypadDoor;
	}
}
