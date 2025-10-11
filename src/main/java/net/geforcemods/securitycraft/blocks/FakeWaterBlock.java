package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class FakeWaterBlock extends LiquidBlock {
	public FakeWaterBlock(BlockBehaviour.Properties properties, FlowingFluid fluid) {
		super(fluid, properties);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean b) {
		if (!level.isClientSide() && !(entity instanceof ItemEntity) && !(entity instanceof Boat) && (!(entity instanceof Player player) || (!player.isCreative() && !(player.getVehicle() instanceof Boat))))
			entity.hurt(CustomDamageSources.fakeWater(level.registryAccess()), 4F);
	}

	@Override
	public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, BlockState queryState, BlockPos queryPos) {
		return Blocks.WATER.defaultBlockState().setValue(LEVEL, state.getValue(LEVEL));
	}
}
