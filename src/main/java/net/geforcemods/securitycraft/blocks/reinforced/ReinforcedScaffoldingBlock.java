package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;

public class ReinforcedScaffoldingBlock extends ScaffoldingBlock implements IReinforcedBlock {
	public ReinforcedScaffoldingBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, state, player, level, pos);
	}

	@Override
	public boolean canHarvestBlock(BlockState state, IBlockReader level, BlockPos pos, PlayerEntity player) {
		return ConfigHandler.SERVER.alwaysDrop.get() || super.canHarvestBlock(state, level, pos, player);
	}

	@Override
	public ToolType getHarvestTool(BlockState state) {
		return getVanillaBlock().getHarvestTool(convertToVanilla(null, null, state));
	}

	@Override
	public int getHarvestLevel(BlockState state) {
		return getVanillaBlock().getHarvestLevel(convertToVanilla(null, null, state));
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) placer));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new OwnableBlockEntity();
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.SCAFFOLDING;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		if (ctx instanceof EntitySelectionContext && ownsScaffolding(level, pos, ((EntitySelectionContext) ctx).getEntity()))
			return super.getShape(state, level, pos, ctx);
		else
			return state.getValue(BOTTOM) ? UNSTABLE_SHAPE : STABLE_SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		if (ctx instanceof EntitySelectionContext && ownsScaffolding(level, pos, ((EntitySelectionContext) ctx).getEntity()))
			return super.getCollisionShape(state, level, pos, ctx);
		else
			return VoxelShapes.empty();
	}

	@Override
	public boolean isScaffolding(BlockState state, IWorldReader level, BlockPos pos, LivingEntity entity) {
		return ownsScaffolding(level, pos, entity);
	}

	@Override
	public boolean isLadder(BlockState state, IWorldReader level, BlockPos pos, LivingEntity entity) {
		return super.isLadder(state, level, pos, entity) && ownsScaffolding(level, pos, entity);
	}

	public boolean ownsScaffolding(IBlockReader level, BlockPos pos, Entity entity) {
		if (!(entity instanceof LivingEntity))
			return true;

		TileEntity te = level.getBlockEntity(pos);

		return te instanceof IOwnable && ((IOwnable) te).isOwnedBy(entity);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos pos = context.getClickedPos();
		World level = context.getLevel();
		int distance = getDistance(level, pos);

		return defaultBlockState().setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER).setValue(DISTANCE, distance).setValue(BOTTOM, isBottom(level, pos, distance));
	}

	@Override
	public void tick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
		int distance = getDistance(level, pos);
		BlockState newState = state.setValue(DISTANCE, distance).setValue(BOTTOM, isBottom(level, pos, distance));

		if (newState.getValue(DISTANCE) == 7) {
			if (state.getValue(DISTANCE) == 7) {
				CompoundNBT data = level.getBlockEntity(pos).save(new CompoundNBT());
				FallingBlockEntity entity = new FallingBlockEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, newState.setValue(WATERLOGGED, false));

				entity.blockData = data;
				level.addFreshEntity(entity);
			}
			else
				level.destroyBlock(pos, true);
		}
		else if (state != newState)
			level.setBlock(pos, newState, 3);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
		return getDistance(level, pos) < 7;
	}

	public static int getDistance(IBlockReader level, BlockPos pos) {
		BlockPos.Mutable mutable = pos.mutable().move(Direction.DOWN);
		BlockState mutableState = level.getBlockState(mutable);
		int distance = 7;

		if (mutableState.is(SCContent.REINFORCED_SCAFFOLDING.get()))
			distance = mutableState.getValue(DISTANCE);
		else if (mutableState.isFaceSturdy(level, mutable, Direction.UP))
			return 0;

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockState offsetState = level.getBlockState(mutable.setWithOffset(pos, direction));

			if (offsetState.is(SCContent.REINFORCED_SCAFFOLDING.get())) {
				distance = Math.min(distance, offsetState.getValue(DISTANCE) + 1);

				if (distance == 1)
					break;
			}
		}

		return distance;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState pState) {
		return PushReaction.BLOCK;
	}
}
