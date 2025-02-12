package net.geforcemods.securitycraft.items.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.api.IBlockMine;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.components.GlobalPositionComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

@SuppressWarnings("rawtypes")
public record BlockLinked(DataComponentType<?> positionComponent, HitCheck hitCheck) implements SelectItemModelProperty<String> {
	public static final String NO_POSITIONS = "no_positions", UNKNOWN = "unknown", NOT_LINKED = "not_linked", LINKED = "linked";
	//@formatter:off
	public static final SelectItemModelProperty.Type<BlockLinked, String> TYPE = SelectItemModelProperty.Type.create(
			RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							DataComponentType.CODEC.optionalFieldOf("position_component", null).forGetter(BlockLinked::positionComponent),
							HitCheck.CODEC.fieldOf("hit_check").forGetter(BlockLinked::hitCheck))
					.apply(instance, BlockLinked::new)),
			Codec.STRING);
	//@formatter:on

	@Override
	public final String get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed, ItemDisplayContext ctx) {
		if (stack.get(positionComponent) instanceof GlobalPositionComponent positions) {
			if (level != null && entity instanceof Player player) {
				BlockHitResult hitResult = HitCheck.getHitResult(level, player);

				if (hitResult != null) {
					BlockPos pos = hitResult.getBlockPos();
					BlockEntity be = level.getBlockEntity(pos);

					//if the block is not ownable/not owned by the player looking at it, don't show the indicator if it's disguised
					if (!(be instanceof IOwnable ownable) || !ownable.isOwnedBy(player)) {
						if (be == null || be.getBlockState().getBlock() instanceof IBlockMine || IDisguisable.getDisguisedBlockState(be).isPresent())
							return returnBasedOnComponent(positions);
					}

					if (hitCheck.isValidHitResult(level, hitResult)) {
						GlobalPos globalPos = new GlobalPos(level.dimension(), pos);

						return positions.isPositionAdded(globalPos) ? LINKED : NOT_LINKED;
					}
				}
			}

			return returnBasedOnComponent(positions);
		}

		return NO_POSITIONS;
	}

	private final String returnBasedOnComponent(GlobalPositionComponent positions) {
		return positions.isEmpty() ? NO_POSITIONS : UNKNOWN;
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
