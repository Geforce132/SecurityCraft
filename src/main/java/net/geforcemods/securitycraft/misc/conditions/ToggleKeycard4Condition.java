package net.geforcemods.securitycraft.misc.conditions;

import com.google.gson.JsonObject;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public final class ToggleKeycard4Condition implements ICondition
{
	public static final ToggleKeycard4Condition INSTANCE = new ToggleKeycard4Condition();
	private static final ResourceLocation NAME = new ResourceLocation(SecurityCraft.MODID, "toggle_keycard_4");

	private ToggleKeycard4Condition() {}

	@Override
	public ResourceLocation getID()
	{
		return NAME;
	}

	@Override
	public boolean test()
	{
		return ConfigHandler.CONFIG.ableToCraftKeycard4.get();
	}

	@Override
	public String toString()
	{
		return "Config value: " + test();
	}

	public static class Serializer implements IConditionSerializer<ToggleKeycard4Condition>
	{
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, ToggleKeycard4Condition value) {}

		@Override
		public ToggleKeycard4Condition read(JsonObject json)
		{
			return ToggleKeycard4Condition.INSTANCE;
		}

		@Override
		public ResourceLocation getID()
		{
			return ToggleKeycard4Condition.NAME;
		}
	}
}