package net.geforcemods.securitycraft.items.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record ReinforcedTint(ItemTintSource base) implements ItemTintSource {
	//@formatter:off
	public static final MapCodec<ReinforcedTint> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
					ItemTintSources.CODEC.optionalFieldOf("base", new Constant(0xFFFFFFFF)).forGetter(ReinforcedTint::base))
			.apply(i, ReinforcedTint::new));
	//@formatter:on

	@Override
	public int calculate(ItemStack stack, ClientLevel level, LivingEntity entity) {
		return ClientHandler.mixWithReinforcedTintIfEnabled(base.calculate(stack, level, entity));
	}

	@Override
	public MapCodec<? extends ItemTintSource> type() {
		return MAP_CODEC;
	}
}
