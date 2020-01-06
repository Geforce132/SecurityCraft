package net.geforcemods.securitycraft.misc.conditions;

import com.google.gson.JsonObject;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public final class ToggleKeycard2Condition implements ICondition
{
	public static final ToggleKeycard2Condition INSTANCE = new ToggleKeycard2Condition();
	private static final ResourceLocation NAME = new ResourceLocation(SecurityCraft.MODID, "toggle_keycard_2");

	private ToggleKeycard2Condition() {}

	@Override
	public ResourceLocation getID()
	{
		return NAME;
	}

	@Override
	public boolean test()
	{
		return ConfigHandler.CONFIG.ableToCraftKeycard2.get();
	}

	@Override
	public String toString()
	{
		return "Config value: " + test();
	}

	public static class Serializer implements IConditionSerializer<ToggleKeycard2Condition>
	{
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, ToggleKeycard2Condition value) {}

		@Override
		public ToggleKeycard2Condition read(JsonObject json)
		{
			return ToggleKeycard2Condition.INSTANCE;
		}

		@Override
		public ResourceLocation getID()
		{
			return ToggleKeycard2Condition.NAME;
		}
	}
}