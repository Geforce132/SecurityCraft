package net.geforcemods.securitycraft.misc.conditions;

import com.google.gson.JsonObject;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public final class ToggleLimitedUseKeycardCondition implements ICondition
{
	public static final ToggleLimitedUseKeycardCondition INSTANCE = new ToggleLimitedUseKeycardCondition();
	private static final ResourceLocation NAME = new ResourceLocation(SecurityCraft.MODID, "toggle_lu_keycard");

	private ToggleLimitedUseKeycardCondition() {}

	@Override
	public ResourceLocation getID()
	{
		return NAME;
	}

	@Override
	public boolean test()
	{
		return ConfigHandler.CONFIG.ableToCraftLUKeycard.get();
	}

	@Override
	public String toString()
	{
		return "Config value: " + test();
	}

	public static class Serializer implements IConditionSerializer<ToggleLimitedUseKeycardCondition>
	{
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, ToggleLimitedUseKeycardCondition value) {}

		@Override
		public ToggleLimitedUseKeycardCondition read(JsonObject json)
		{
			return ToggleLimitedUseKeycardCondition.INSTANCE;
		}

		@Override
		public ResourceLocation getID()
		{
			return ToggleLimitedUseKeycardCondition.NAME;
		}
	}
}