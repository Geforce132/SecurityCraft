package net.geforcemods.securitycraft.misc.conditions;

import com.google.gson.JsonObject;

import net.geforcemods.securitycraft.ConfigHandler.CommonConfig;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public final class ToggleKeycard5Condition implements ICondition
{
	public static final ToggleKeycard5Condition INSTANCE = new ToggleKeycard5Condition();
	private static final ResourceLocation NAME = new ResourceLocation(SecurityCraft.MODID, "toggle_keycard_5");

	private ToggleKeycard5Condition() {}

	@Override
	public ResourceLocation getID()
	{
		return NAME;
	}

	@Override
	public boolean test()
	{
		return CommonConfig.CONFIG.ableToCraftKeycard5.get();
	}

	@Override
	public String toString()
	{
		return "Config value: " + test();
	}

	public static class Serializer implements IConditionSerializer<ToggleKeycard5Condition>
	{
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, ToggleKeycard5Condition value) {}

		@Override
		public ToggleKeycard5Condition read(JsonObject json)
		{
			return ToggleKeycard5Condition.INSTANCE;
		}

		@Override
		public ResourceLocation getID()
		{
			return ToggleKeycard5Condition.NAME;
		}
	}
}