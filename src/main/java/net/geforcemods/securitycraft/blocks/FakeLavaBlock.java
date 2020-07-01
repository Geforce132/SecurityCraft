package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FakeLavaBlock extends FlowingFluidBlock
{
	private static final EffectInstance SHORT_FIRE_RESISTANCE = new EffectInstance(Effects.FIRE_RESISTANCE, 1);

	public FakeLavaBlock()
	{
		super(SCContent.FAKE_LAVA, Block.Properties.create(Material.LAVA).doesNotBlockMovement().tickRandomly().hardnessAndResistance(-1.0F, 6000000.0F).func_235838_a_(stat -> 15)); //lightValue
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		super.onEntityCollision(state, world, pos, entity);

		if(!world.isRemote && entity instanceof LivingEntity)
		{
			LivingEntity lEntity = (LivingEntity)entity;

			lEntity.extinguish();

			if(!lEntity.isPotionActive(Effects.FIRE_RESISTANCE))
				lEntity.addPotionEffect(SHORT_FIRE_RESISTANCE);

			lEntity.heal(4);
		}
	}
}
