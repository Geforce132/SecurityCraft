package net.geforcemods.securitycraft.misc.conditions;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;

public class TileEntityNBTCondition implements ILootCondition
{
	private String key;
	private boolean value;

	private TileEntityNBTCondition(String key, boolean value)
	{
		this.key = key;
		this.value = value;
	}

	@Override
	public Set<LootParameter<?>> getRequiredParameters()
	{
		return ImmutableSet.of(LootParameters.field_237457_g_);
	}

	@Override
	public boolean test(LootContext lootContext)
	{
		TileEntity te = lootContext.getWorld().getTileEntity(new BlockPos(lootContext.get(LootParameters.field_237457_g_)));
		CompoundNBT nbt = te.write(new CompoundNBT());

		return nbt.contains(key) && nbt.getBoolean(key) == value;
	}

	@Override
	public LootConditionType func_230419_b_()
	{
		return SecurityCraft.TILE_ENTITY_NBT_LOOT_CONDITION;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static class Builder implements IBuilder
	{
		private String key;
		private boolean value;

		public Builder equals(String key, boolean value)
		{
			this.key = key;
			this.value = value;
			return this;
		}

		@Override
		public ILootCondition build()
		{
			return new TileEntityNBTCondition(key, value);
		}
	}

	public static class Serializer implements ILootSerializer<TileEntityNBTCondition>
	{
		@Override
		public void serialize(JsonObject json, TileEntityNBTCondition condition, JsonSerializationContext ctx)
		{
			json.addProperty("key", condition.key);
			json.addProperty("value", condition.value);
		}

		@Override
		public TileEntityNBTCondition deserialize(JsonObject json, JsonDeserializationContext ctx)
		{
			return new TileEntityNBTCondition(JSONUtils.getString(json, "key"), JSONUtils.getBoolean(json, "value"));
		}
	}
}
