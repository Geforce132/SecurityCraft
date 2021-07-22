package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.item.Item.Properties;

public class ScannerDoorItem extends SpecialDoorItem
{
	public ScannerDoorItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public Block getDoorBlock()
	{
		return SCContent.SCANNER_DOOR.get();
	}
}
