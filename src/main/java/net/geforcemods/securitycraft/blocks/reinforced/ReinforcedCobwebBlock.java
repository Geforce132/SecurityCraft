package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReinforcedCobwebBlock extends BaseReinforcedBlock {
	public ReinforcedCobwebBlock(BlockBehaviour.Properties properties, Block vB) {
		super(properties, vB);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return Shapes.empty();
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean b) {
		if (entity instanceof Player player && level.getBlockEntity(pos) instanceof OwnableBlockEntity be && be.isOwnedBy(player))
			return;

		Vec3 slowness;

		if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(MobEffects.WEAVING))
			slowness = new Vec3(0.5F, 0.25F, 0.5F);
		else
			slowness = new Vec3(0.25, 0.05F, 0.25);

		entity.makeStuckInBlock(state, slowness);
	}
}
