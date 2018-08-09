package net.geforcemods.securitycraft.factories;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class KeycardConditionFactory implements IConditionFactory
{
	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json)
	{
		String type = JsonUtils.getString(json, "type");

		if(type.equals("securitycraft_toggle_keycard_1")) return () -> SecurityCraft.config.ableToCraftKeycard1;
		else if(type.equals("securitycraft_toggle_keycard_2")) return () -> SecurityCraft.config.ableToCraftKeycard2;
		else if(type.equals("securitycraft_toggle_keycard_3")) return () -> SecurityCraft.config.ableToCraftKeycard3;
		else if(type.equals("securitycraft_toggle_keycard_4")) return () -> SecurityCraft.config.ableToCraftKeycard4;
		else if(type.equals("securitycraft_toggle_keycard_5")) return () -> SecurityCraft.config.ableToCraftKeycard5;
		else if(type.equals("securitycraft_toggle_lu_keycard")) return () -> SecurityCraft.config.ableToCraftLUKeycard;
		else return () -> true;
	}
}
