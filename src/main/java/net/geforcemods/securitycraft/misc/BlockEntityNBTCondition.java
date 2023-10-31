package net.geforcemods.securitycraft.misc;

import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public record BlockEntityNBTCondition(String key, boolean value) implements LootItemCondition {

	//@formatter:off
	public static final Codec<BlockEntityNBTCondition> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.STRING.fieldOf("key").forGetter(BlockEntityNBTCondition::key),
					Codec.BOOL.fieldOf("value").forGetter(BlockEntityNBTCondition::value))
			.apply(instance, BlockEntityNBTCondition::new));
	//@formatter:on

	@Override
	public Set<LootContextParam<?>> getReferencedContextParams() {
		return Set.of(LootContextParams.ORIGIN);
	}

	@Override
	public boolean test(LootContext lootContext) {
		BlockEntity be = lootContext.getLevel().getBlockEntity(BlockPos.containing(lootContext.getParamOrNull(LootContextParams.ORIGIN)));
		CompoundTag nbt = be.saveWithFullMetadata();

		return nbt.contains(key) && nbt.getBoolean(key) == value;
	}

	@Override
	public LootItemConditionType getType() {
		return SCContent.BLOCK_ENTITY_NBT.get();
	}

	public static LootItemCondition.Builder nbt(String key, boolean value) {
		return () -> new BlockEntityNBTCondition(key, value);
	}
}
