package net.geforcemods.securitycraft.misc.conditions;

import com.google.gson.JsonObject;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public final class ToggleKeycard1Condition implements ICondition
{
	public static final ToggleKeycard1Condition INSTANCE = new ToggleKeycard1Condition();
	private static final ResourceLocation NAME = new ResourceLocation(SecurityCraft.MODID, "toggle_keycard_1");

	private ToggleKeycard1Condition() {}

	@Override
	public ResourceLocation getID()
	{
		return NAME;
	}

	@Override
	public boolean test()
	{
		return ConfigHandler.CONFIG.ableToCraftKeycard1.get();
	}

	@Override
	public String toString()
	{
		return "Config value: " + test();
	}

	public static class Serializer implements IConditionSerializer<ToggleKeycard1Condition>
	{
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, ToggleKeycard1Condition value) {}

		@Override
		public ToggleKeycard1Condition read(JsonObject json)
		{
			return ToggleKeycard1Condition.INSTANCE;
		}

		@Override
		public ResourceLocation getID()
		{
			return ToggleKeycard1Condition.NAME;
		}
	}
}