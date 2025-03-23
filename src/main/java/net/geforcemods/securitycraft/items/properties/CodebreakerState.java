package net.geforcemods.securitycraft.items.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.components.CodebreakerData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

public record CodebreakerState(HitCheck hitCheck) implements SelectItemModelProperty<String> {
	public static final String DEFAULT = "default", DECODING = "decoding", SUCCESS = "success", FAILURE = "failure";
	//@formatter:off
	public static final SelectItemModelProperty.Type<CodebreakerState, String> TYPE = SelectItemModelProperty.Type.create(
			RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							HitCheck.CODEC.fieldOf("hit_check").forGetter(CodebreakerState::hitCheck))
					.apply(instance, CodebreakerState::new)),
			Codec.STRING);
	//@formatter:on

	@Override
	public String get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed, ItemDisplayContext ctx) {
		CodebreakerData codebreakerData = stack.getOrDefault(SCContent.CODEBREAKER_DATA, CodebreakerData.DEFAULT);
		boolean isPlayer = entity instanceof Player;

		if ((!isPlayer || !((Player) entity).isCreative() && !((Player) entity).isSpectator()) && codebreakerData.wasRecentlyUsed())
			return codebreakerData.wasSuccessful() ? SUCCESS : FAILURE;

		if (level != null && isPlayer) {
			Player player = (Player) entity;
			BlockHitResult hitResult = HitCheck.getHitResult(level, player);

			if (hitResult != null) {
				BlockPos pos = hitResult.getBlockPos();
				BlockEntity be = level.getBlockEntity(pos);

				//if the block is not ownable/not owned by the player looking at it, don't show the indicator if it's disguised
				if (!(be instanceof IOwnable ownable) || !ownable.isOwnedBy(player)) {
					if (IDisguisable.getDisguisedBlockState(be).isPresent())
						return DEFAULT;
				}

				if (hitCheck.isValidHitResult(level, hitResult))
					return DECODING;
			}
		}

		return DEFAULT;
	}

	@Override
	public Codec<String> valueCodec() {
		return Codec.STRING;
	}

	@Override
	public Type<? extends SelectItemModelProperty<String>, String> type() {
		return TYPE;
	}
}
