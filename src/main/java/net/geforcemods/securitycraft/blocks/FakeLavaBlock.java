package net.geforcemods.securitycraft.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class FakeLavaBlock extends LiquidBlock {
	private static final MobEffectInstance SHORT_FIRE_RESISTANCE = new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1);

	public FakeLavaBlock(BlockBehaviour.Properties properties, FlowingFluid fluid) {
		super(fluid, properties);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean b) {
		super.entityInside(state, level, pos, entity, effectApplier, b);

		if (entity instanceof LivingEntity lEntity) {
			lEntity.clearFire();
			lEntity.setSharedFlagOnFire(false);

			if (!level.isClientSide()) {
				lEntity.addEffect(SHORT_FIRE_RESISTANCE);

				if (!lEntity.hasEffect(MobEffects.REGENERATION))
					lEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 2, false, false));
			}
		}
	}

	@Override
	public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, BlockState queryState, BlockPos queryPos) {
		return Blocks.LAVA.defaultBlockState().setValue(LEVEL, state.getValue(LEVEL));
	}
}
