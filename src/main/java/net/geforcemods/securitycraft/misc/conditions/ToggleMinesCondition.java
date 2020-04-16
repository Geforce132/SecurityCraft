package net.geforcemods.securitycraft.misc.conditions;

import com.google.gson.JsonObject;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public final class ToggleMinesCondition implements ICondition
{
	public static final ToggleMinesCondition INSTANCE = new ToggleMinesCondition();
	private static final ResourceLocation NAME = new ResourceLocation(SecurityCraft.MODID, "toggle_mines");

	private ToggleMinesCondition() {}

	@Override
	public ResourceLocation getID()
	{
		return NAME;
	}

	@Override
	public boolean test()
	{
		return ConfigHandler.CONFIG.ableToCraftMines.get();
	}

	@Override
	public String toString()
	{
		return "Config value: " + test();
	}

	public static class Serializer implements IConditionSerializer<ToggleMinesCondition>
	{
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, ToggleMinesCondition value) {}

		@Override
		public ToggleMinesCondition read(JsonObject json)
		{
			return ToggleMinesCondition.INSTANCE;
		}

		@Override
		public ResourceLocation getID()
		{
			return ToggleMinesCondition.NAME;
		}
	}
}