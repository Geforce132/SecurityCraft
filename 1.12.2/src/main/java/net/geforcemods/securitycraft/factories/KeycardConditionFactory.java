package net.geforcemods.securitycraft.factories;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;

import net.geforcemods.securitycraft.ConfigHandler;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class KeycardConditionFactory implements IConditionFactory
{
	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json)
	{
		String type = JsonUtils.getString(json, "type");

		if(type.equals("securitycraft_toggle_keycard_1")) return () -> ConfigHandler.ableToCraftKeycard1;
		else if(type.equals("securitycraft_toggle_keycard_2")) return () -> ConfigHandler.ableToCraftKeycard2;
		else if(type.equals("securitycraft_toggle_keycard_3")) return () -> ConfigHandler.ableToCraftKeycard3;
		else if(type.equals("securitycraft_toggle_keycard_4")) return () -> ConfigHandler.ableToCraftKeycard4;
		else if(type.equals("securitycraft_toggle_keycard_5")) return () -> ConfigHandler.ableToCraftKeycard5;
		else if(type.equals("securitycraft_toggle_lu_keycard")) return () -> ConfigHandler.ableToCraftLUKeycard;
		else return () -> true;
	}
}
