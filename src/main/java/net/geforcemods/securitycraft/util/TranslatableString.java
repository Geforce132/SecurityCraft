package net.geforcemods.securitycraft.util;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class TranslatableString
{
	private String string;
	private Object[] args;

	public TranslatableString(Item item, Object... args)
	{
		if(item != null)
			string = item.getUnlocalizedName();
		else
			string = Blocks.air.getUnlocalizedName();

		string += ".name";
		this.args = args;
	}

	public TranslatableString(String string, Object... args)
	{
		this.string = string;
		this.args = args;
	}

	public String getString()
	{
		return string;
	}

	public Object[] getArgs()
	{
		return args;
	}
}
