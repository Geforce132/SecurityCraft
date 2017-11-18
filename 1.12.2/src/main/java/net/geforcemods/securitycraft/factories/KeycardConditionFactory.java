package net.geforcemods.securitycraft.factories;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class KeycardConditionFactory implements IConditionFactory
{
	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json)
	{
		String type = JsonUtils.getString(json, "type");

		if(type.equals("securitycraft_toggle_keycard_1"))
			return () -> mod_SecurityCraft.configHandler.ableToCraftKeycard1;
			else if(type.equals("securitycraft_toggle_keycard_2"))
				return () -> mod_SecurityCraft.configHandler.ableToCraftKeycard2;
				else if(type.equals("securitycraft_toggle_keycard_3"))
					return () -> mod_SecurityCraft.configHandler.ableToCraftKeycard3;
					else if(type.equals("securitycraft_toggle_keycard_4"))
						return () -> mod_SecurityCraft.configHandler.ableToCraftKeycard4;
						else if(type.equals("securitycraft_toggle_keycard_5"))
							return () -> mod_SecurityCraft.configHandler.ableToCraftKeycard5;
							else if(type.equals("securitycraft_toggle_lu_keycard"))
								return () -> mod_SecurityCraft.configHandler.ableToCraftLUKeycard;
								else return () -> true;
	}
}
