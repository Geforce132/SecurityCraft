package net.geforcemods.securitycraft.misc;

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

public class BlockEntityNBTCondition implements ILootCondition {
	private String key;
	private boolean value;

	private BlockEntityNBTCondition(String key, boolean value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public Set<LootParameter<?>> getReferencedContextParams() {
		return ImmutableSet.of(LootParameters.ORIGIN);
	}

	@Override
	public boolean test(LootContext lootContext) {
		TileEntity be = lootContext.getParamOrNull(LootParameters.BLOCK_ENTITY);

		if (be != null) {
			CompoundNBT nbt = be.save(new CompoundNBT());

			return nbt.contains(key) && nbt.getBoolean(key) == value;
		}

		return false;
	}

	@Override
	public LootConditionType getType() {
		return SecurityCraft.TILE_ENTITY_NBT_LOOT_CONDITION;
	}

	public static ConditionBuilder builder() {
		return new ConditionBuilder();
	}

	public static class ConditionBuilder implements IBuilder {
		private String key;
		private boolean value;

		public ConditionBuilder equals(String key, boolean value) {
			this.key = key;
			this.value = value;
			return this;
		}

		@Override
		public ILootCondition build() {
			return new BlockEntityNBTCondition(key, value);
		}
	}

	public static class ConditionSerializer implements ILootSerializer<BlockEntityNBTCondition> {
		@Override
		public void serialize(JsonObject json, BlockEntityNBTCondition condition, JsonSerializationContext ctx) {
			json.addProperty("key", condition.key);
			json.addProperty("value", condition.value);
		}

		@Override
		public BlockEntityNBTCondition deserialize(JsonObject json, JsonDeserializationContext ctx) {
			return new BlockEntityNBTCondition(JSONUtils.getAsString(json, "key"), JSONUtils.getAsBoolean(json, "value"));
		}
	}
}
