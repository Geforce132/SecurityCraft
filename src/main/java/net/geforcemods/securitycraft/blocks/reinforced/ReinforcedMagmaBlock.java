package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.block.SeaGrassBlock;
import net.minecraft.block.TallSeaGrassBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;

public class ReinforcedMagmaBlock extends BaseReinforcedBlock {
	public ReinforcedMagmaBlock(Block vanillaBlock) {
		super(vanillaBlock);
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader level, BlockPos pos, Direction facing, IPlantable plantable) {
		if (plantable instanceof SeaGrassBlock || plantable instanceof TallSeaGrassBlock)
			return false;

		return super.canSustainPlant(state, level, pos, facing, plantable);
	}

	@Override
	public void stepOn(World level, BlockPos pos, Entity entity) {
		if (!entity.fireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity)) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof OwnableBlockEntity && !((OwnableBlockEntity) te).isOwnedBy(entity))
				entity.hurt(DamageSource.HOT_FLOOR, 1.0F);
		}

		super.stepOn(level, pos, entity);
	}

	@Override
	public void tick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
		BubbleColumnBlock.growColumn(level, pos.above(), true);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos) {
		if (facing == Direction.UP && facingState.is(Blocks.WATER))
			level.getBlockTicks().scheduleTick(currentPos, this, 20);

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public void onPlace(BlockState state, World level, BlockPos pos, BlockState oldState, boolean isMoving) {
		level.getBlockTicks().scheduleTick(pos, this, 20);
	}
}
