package net.geforcemods.securitycraft.blocks;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FakeWaterBlock extends FlowingFluidBlock
{
	public FakeWaterBlock(Block.Properties properties, Supplier<? extends FlowingFluid> fluid)
	{
		super(fluid, properties);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		if(!world.isRemote && !(entity instanceof ItemEntity) && !(entity instanceof BoatEntity))
		{
			if(!(entity instanceof PlayerEntity) || (!((PlayerEntity) entity).isCreative() && !(((PlayerEntity)entity).getRidingEntity() instanceof BoatEntity)))
				entity.attackEntityFrom(CustomDamageSources.FAKE_WATER, 1.5F);
		}
	}
}
