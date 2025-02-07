package net.geforcemods.securitycraft.misc;

import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class BlockEntityNBTCondition implements LootItemCondition {
	private String key;
	private boolean value;

	private BlockEntityNBTCondition(String key, boolean value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public Set<LootContextParam<?>> getReferencedContextParams() {
		return Set.of(LootContextParams.ORIGIN);
	}

	@Override
	public boolean test(LootContext lootContext) {
		BlockEntity be = lootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);

		if (be != null) {
			CompoundTag nbt = be.saveWithFullMetadata();

			return nbt.contains(key) && nbt.getBoolean(key) == value;
		}

		return false;
	}

	@Override
	public LootItemConditionType getType() {
		return SCContent.BLOCK_ENTITY_NBT.get();
	}

	public static ConditionBuilder builder() {
		return new ConditionBuilder();
	}

	public static class ConditionBuilder implements Builder {
		private String key;
		private boolean value;

		public ConditionBuilder equals(String key, boolean value) {
			this.key = key;
			this.value = value;
			return this;
		}

		@Override
		public LootItemCondition build() {
			return new BlockEntityNBTCondition(key, value);
		}
	}

	public static class ConditionSerializer implements Serializer<BlockEntityNBTCondition> {
		@Override
		public void serialize(JsonObject json, BlockEntityNBTCondition condition, JsonSerializationContext ctx) {
			json.addProperty("key", condition.key);
			json.addProperty("value", condition.value);
		}

		@Override
		public BlockEntityNBTCondition deserialize(JsonObject json, JsonDeserializationContext ctx) {
			return new BlockEntityNBTCondition(GsonHelper.getAsString(json, "key"), GsonHelper.getAsBoolean(json, "value"));
		}
	}
}
