package net.geforcemods.securitycraft.blocks;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class FakeWaterBlock extends LiquidBlock
{
	public FakeWaterBlock(Block.Properties properties, Supplier<? extends FlowingFluid> fluid)
	{
		super(fluid, properties);
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity)
	{
		if(!world.isClientSide && !(entity instanceof ItemEntity) && !(entity instanceof Boat))
		{
			if(!(entity instanceof Player) || (!((Player) entity).isCreative() && !(((Player)entity).getVehicle() instanceof Boat)))
				entity.hurt(CustomDamageSources.FAKE_WATER, 1.5F);
		}
	}
}
