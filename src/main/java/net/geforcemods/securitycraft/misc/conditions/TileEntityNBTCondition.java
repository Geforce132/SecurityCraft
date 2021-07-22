package net.geforcemods.securitycraft.misc.conditions;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class TileEntityNBTCondition implements LootItemCondition
{
	private String key;
	private boolean value;

	private TileEntityNBTCondition(String key, boolean value)
	{
		this.key = key;
		this.value = value;
	}

	@Override
	public Set<LootContextParam<?>> getReferencedContextParams()
	{
		return ImmutableSet.of(LootContextParams.ORIGIN);
	}

	@Override
	public boolean test(LootContext lootContext)
	{
		BlockEntity te = lootContext.getLevel().getBlockEntity(new BlockPos(lootContext.getParamOrNull(LootContextParams.ORIGIN)));
		CompoundTag nbt = te.save(new CompoundTag());

		return nbt.contains(key) && nbt.getBoolean(key) == value;
	}

	@Override
	public LootItemConditionType getType()
	{
		return SecurityCraft.TILE_ENTITY_NBT_LOOT_CONDITION;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static class Builder implements Builder
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
		public LootItemCondition build()
		{
			return new TileEntityNBTCondition(key, value);
		}
	}

	public static class Serializer implements Serializer<TileEntityNBTCondition>
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
			return new TileEntityNBTCondition(GsonHelper.getAsString(json, "key"), GsonHelper.getAsBoolean(json, "value"));
		}
	}
}
