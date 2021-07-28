package net.geforcemods.securitycraft.blocks;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class FakeLavaBlock extends LiquidBlock
{
	private static final MobEffectInstance SHORT_FIRE_RESISTANCE = new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1);

	public FakeLavaBlock(Block.Properties properties, Supplier<? extends FlowingFluid> fluid)
	{
		super(fluid, properties);
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity)
	{
		super.entityInside(state, world, pos, entity);

		if(entity instanceof LivingEntity lEntity)
		{
			lEntity.clearFire();

			if(!world.isClientSide)
			{
				lEntity.addEffect(SHORT_FIRE_RESISTANCE);

				if(!lEntity.hasEffect(MobEffects.REGENERATION))
					lEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 2));
			}
		}
	}
}
